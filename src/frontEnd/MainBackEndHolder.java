package frontEnd;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;

import core.config.Config;
import core.controller.Core;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.keyChain.TaskActivation;
import core.keyChain.managers.GlobalEventsManager;
import core.languageHandler.Language;
import core.languageHandler.compiler.AbstractNativeCompiler;
import core.languageHandler.compiler.DynamicCompilerOutput;
import core.languageHandler.compiler.PythonRemoteCompiler;
import core.recorder.Recorder;
import core.recorder.ReplayConfig;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskInvoker;
import core.userDefinedTask.TaskSourceManager;
import core.userDefinedTask.UserDefinedAction;
import globalListener.GlobalListenerHookController;
import staticResources.BootStrapResources;
import utilities.FileUtility;
import utilities.Function;
import utilities.Pair;
import utilities.StringUtilities;
import utilities.ZipUtility;
import utilities.logging.LogHolder;
import utilities.swing.SwingUtil.DialogUtil;

public class MainBackEndHolder {

	private static final Logger LOGGER = Logger.getLogger(MainBackEndHolder.class.getName());

	protected MinimizedFrame trayIcon;
	protected LogHolder logHolder;

	protected ScheduledThreadPoolExecutor executor;
	private Thread compiledExecutor;
	private Language compilingLanguage;

	private ReplayConfig replayConfig;
	protected Recorder recorder;

	private UserDefinedAction customFunction;

	protected final List<TaskGroup> taskGroups;
	private TaskGroup currentGroup;

	// To allow executing other tasks programmatically.
	private final TaskInvoker taskInvoker;
	protected final GlobalEventsManager keysManager;

	protected final Config config;

	protected final UserDefinedAction switchRecord, switchReplay, switchReplayCompiled;
	private boolean isRecording, isReplaying, isRunningCompiledTask;

	private File tempSourceFile;

	public MainBackEndHolder() {
		config = new Config(this);

		if (!SystemTray.isSupported()) {
			LOGGER.warning("System tray is not supported!");
			trayIcon = null;
		} else {
			trayIcon = new MinimizedFrame(BootStrapResources.TRAY_IMAGE, this);
		}
		logHolder = new LogHolder();

		executor = new ScheduledThreadPoolExecutor(10);
		compilingLanguage = Language.JAVA;

		taskGroups = new ArrayList<>();

		taskInvoker = new TaskInvoker(taskGroups);
		keysManager = new GlobalEventsManager(config);
		replayConfig = ReplayConfig.of();
		recorder = new Recorder(keysManager);

		switchRecord = new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				switchRecord();
			}
		};

		switchReplay = new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				switchReplay();
			}
		};

		switchReplayCompiled = new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				switchRunningCompiledAction();
			}
		};

		TaskProcessorManager.setProcessorIdentifyCallback(new Function<Language, Void>(){
			@Override
			public Void apply(Language language) {
				for (TaskGroup group : taskGroups) {
					List<UserDefinedAction> tasks = group.getTasks();
					for (int i = 0; i < tasks.size(); i++) {
						UserDefinedAction task = tasks.get(i);
						if (task.getCompiler() != language) {
							continue;
						}

						AbstractNativeCompiler compiler = config.getCompilerFactory().getCompiler(task.getCompiler());
						UserDefinedAction recompiled = task.recompile(compiler, false);
						if (recompiled == null) {
							continue;
						}

						tasks.set(i, recompiled);

						if (recompiled.isEnabled()) {
							Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(recompiled);
							boolean conflict = false;
							if (!collisions.isEmpty()) {
								if (collisions.size() != 1) {
									conflict = true;
								} else {
									conflict = !collisions.iterator().next().equals(task);
								}
							}

							if (!conflict) {
								keysManager.registerTask(recompiled);
							} else {
								List<String> collisionNames = collisions.stream().map(t -> t.getName()).collect(Collectors.toList());
								LOGGER.warning("Unable to register task " + recompiled.getName() + ". Collisions are " + collisionNames);
							}
						}
					}
				}
				return null;
			}
		});
	}

	/*************************************************************************************************************/
	/************************************************Config*******************************************************/
	protected void loadConfig(File file) {
		config.loadConfig(file);
		setTaskInvoker();

		if (trayIcon != null) {
			if (config.isUseTrayIcon()) {
				try {
					trayIcon.add();
				} catch (AWTException e) {
					LOGGER.log(Level.WARNING, "Exception when adding tray icon.", e);
				}
			}
		}

		File pythonExecutable = ((PythonRemoteCompiler) (config.getCompilerFactory()).getCompiler(Language.PYTHON)).getPath();
		((PythonIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.PYTHON)).setExecutingProgram(pythonExecutable);
	}

	/*************************************************************************************************************/
	/************************************************IPC**********************************************************/
	protected void initiateBackEndActivities() {
		try {
			IPCServiceManager.initiateServices(this);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "IO Exception when launching ipcs.", e);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception when launching ipcs.", e);
		}
	}

	protected void stopBackEndActivities() {
		executor.shutdown();
		try {
			LOGGER.info("Waiting for main executor to terminate...");
			executor.awaitTermination(5, TimeUnit.SECONDS);
			LOGGER.info("Main executor terminated.");
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Interrupted while awaiting backend executor termination.", e);
		}

		try {
			IPCServiceManager.stopServices();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to stop ipcs.", e);
		}

		GlobalListenerHookController.of().cleanup();
	}

	public void exit() {
		stopBackEndActivities();

		if (!writeConfigFile()) {
			JOptionPane.showMessageDialog(null, "Error saving configuration file.");
			System.exit(2);
		}

		if (trayIcon != null) {
			trayIcon.remove();
		}

		System.exit(0);
	}

	/*************************************************************************************************************/
	/****************************************Main hotkeys*********************************************************/
	protected void configureMainHotkeys() {
		keysManager.reRegisterTask(switchRecord, TaskActivation.newBuilder().withHotKey(config.getRECORD()).build());
		keysManager.reRegisterTask(switchReplay, TaskActivation.newBuilder().withHotKey(config.getREPLAY()).build());
		keysManager.reRegisterTask(switchReplayCompiled, TaskActivation.newBuilder().withHotKey(config.getCOMPILED_REPLAY()).build());
	}

	/*************************************************************************************************************/
	/****************************************Record and replay****************************************************/
	public synchronized void startRecording() {
		if (isRecording) {
			return;
		}
		switchRecord();
	}

	public synchronized void stopRecording() {
		if (!isRecording) {
			return;
		}
		switchRecord();
	}

	protected synchronized void switchRecord() {
		if (isReplaying) { // Do not switch record when replaying.
			return;
		}

		if (!isRecording) { // Start record
			recorder.clear();
			recorder.record();
			isRecording = true;
		} else { // Stop record
			recorder.stopRecord();
			isRecording = false;
		}
	}

	public void setReplayCount(long count) {
		replayConfig = ReplayConfig.of(count, replayConfig.getDelay(), replayConfig.getSpeedup());
	}

	public void setReplayDelay(long delay) {
		replayConfig = ReplayConfig.of(replayConfig.getCount(), delay, replayConfig.getSpeedup());
	}

	public void setReplaySpeedup(float speedup) {
		replayConfig = ReplayConfig.of(replayConfig.getCount(), replayConfig.getDelay(), speedup);
	}

	public synchronized void startReplay() {
		if (isReplaying) {
			return;
		}
		switchReplay();
	}

	public synchronized void stopReplay() {
		if (!isReplaying) {
			return;
		}
		switchReplay();
	}

	protected void switchReplay() {
		if (isRecording) { // Cannot switch replay when recording.
			return;
		}

		if (isReplaying) {
			isReplaying = false;
			recorder.stopReplay();
		} else {
			if (!applySpeedup()) {
				return;
			}

			isReplaying = true;
			recorder.replay(replayConfig.getCount(), replayConfig.getDelay(), new Function<Void, Void>() {
				@Override
				public Void apply(Void r) {
					switchReplay();
					return null;
				}
			}, 5, false);
		}
	}

	public synchronized void runCompiledAction() {
		if (isRunningCompiledTask) {
			return;
		}
		switchRunningCompiledAction();
	}

	public synchronized void stopRunningCompiledAction() {
		if (!isRunningCompiledTask) {
			return;
		}
		switchRunningCompiledAction();
	}

	protected synchronized void switchRunningCompiledAction() {
		if (isRunningCompiledTask) {
			isRunningCompiledTask = false;
			if (compiledExecutor != null) {
				if (compiledExecutor != Thread.currentThread()) {
					while (compiledExecutor.isAlive()) {
						compiledExecutor.interrupt();
					}
				}
			}
		} else {
			if (customFunction == null) {
				JOptionPane.showMessageDialog(null, "No compiled action in memory");
				return;
			}

			isRunningCompiledTask = true;

			compiledExecutor = new Thread(new Runnable() {
			    @Override
				public void run() {
			    	try {
						customFunction.action(Core.getInstance());
					} catch (InterruptedException e) { // Stopped prematurely
						return;
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception caught while executing custom function", e);
					}

					switchRunningCompiledAction();
			    }
			});
			compiledExecutor.start();
		}
	}

	/*************************************************************************************************************/
	/*****************************************Task group related**************************************************/
	protected void renderTaskGroup() {
		for (TaskGroup group : taskGroups) {
			if (!group.isEnabled()) {
				continue;
			}

			for (UserDefinedAction task : group.getTasks()) {
				Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(task);
				if (task.isEnabled() && (collisions.isEmpty())) {
					keysManager.registerTask(task);
				}
			}
		}
	}

	/**
	 * Add a task group with actions already filled in.
	 * This also registers the task activations for the tasks in group.
	 * Note that this does not replace existing activations with colliding activations.
	 * Task activation registration  continues on failures and only reports one failure at the end.
	 *
	 * @param group group to add.
	 * @return whether operation succeeds (i.e. no activation collision).
	 */
	public boolean addPopulatedTaskGroup(TaskGroup group) {
		boolean result = true;
		taskGroups.add(group);
		for (UserDefinedAction action : group.getTasks()) {
			Set<UserDefinedAction> collisions = keysManager.isActivationRegistered(action.getActivation());
			if (collisions.isEmpty()) {
				keysManager.registerTask(action);
			} else {
				result &= false;
				String collisionNames = StringUtilities.join(collisions.stream().map(t -> t.getName()).collect(Collectors.toList()), ", ");
				LOGGER.log(Level.WARNING, "Cannot register action " + action.getName() + ". There are collisions with " + collisionNames + " in hotkeys!");
			}
		}
		return result;
	}

	public void addTaskGroup(String name) {
		for (TaskGroup group : taskGroups) {
			if (group.getName().equals(name)) {
				LOGGER.warning("This name already exists. Try again.");
				return;
			}
		}

		taskGroups.add(new TaskGroup(name));
	}

	public void removeTaskGroup(int index) {
		if (index < 0 || index >= taskGroups.size()) {
			return;
		}

		TaskGroup removed = taskGroups.remove(index);
		if (taskGroups.size() < 1) {
			taskGroups.add(new TaskGroup("default"));
		}

		if (getCurrentTaskGroup() == removed) {
			setCurrentTaskGroup(taskGroups.get(0));
		}

		for (UserDefinedAction action : removed.getTasks()) {
			keysManager.unregisterTask(action);
		}
		renderTaskGroup();
	}

	public void moveTaskGroupUp(int index) {
		if (index < 1) {
			return;
		}
		Collections.swap(taskGroups, index, index - 1);
	}

	public void moveTaskGroupDown(int index) {
		if (index >= 0 && index < taskGroups.size() - 1) {
			Collections.swap(taskGroups, index, index + 1);
		}
	}

	/*************************************************************************************************************/
	/*****************************************Task related********************************************************/

	/**
	 * Edit source code using the default program to open the source code file (with appropriate extension
	 * depending on the currently selected language).
	 *
	 * This does not update the source code in the text area in the main GUI.
	 */
	public void editSourceCode(String source) {
		AbstractNativeCompiler currentCompiler = getCompiler();
		// Create a temporary file
		try {
			tempSourceFile = File.createTempFile("source", currentCompiler.getExtension());
			tempSourceFile.deleteOnExit();

			FileUtility.writeToFile(source, tempSourceFile, false);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Encountered error creating temporary source file.\n" + e.getMessage());
			return;
		}

		try {
			Desktop.getDesktop().open(tempSourceFile);
			int update = JOptionPane.showConfirmDialog(null, "Update source code from editted source file? (Confirm once done)", "Reload source code", JOptionPane.YES_NO_OPTION);
			if (update == JOptionPane.YES_OPTION) {
				reloadSourceCode();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to open file for editting.\n" + e.getMessage());
		}
	}

	/**
	 * Load the source code from the temporary source code file into the text area (if the source code file exists).
	 */
	public String reloadSourceCode() {
		if (tempSourceFile == null || !tempSourceFile.exists()) {
			JOptionPane.showMessageDialog(null, "Temp file not accessible.");
			return null;
		}

		StringBuffer sourceCode = FileUtility.readFromFile(tempSourceFile);
		if (sourceCode == null) {
			JOptionPane.showMessageDialog(null, "Unable to read from temp file.");
			return null;
		}
		String source = sourceCode.toString();
		return source;
	}

	private void unregisterTask(UserDefinedAction task) {
		keysManager.unregisterTask(task);
		if (!TaskSourceManager.removeTask(task)) {
			JOptionPane.showMessageDialog(null, "Encountered error removing source file " + task.getSourcePath());
		}
	}

	public void addCurrentTask() {
		addCurrentTask(currentGroup);
	}

	public void addCurrentTask(TaskGroup group) {
		if (customFunction != null) {
			if (customFunction.getName() == null || customFunction.getName().isEmpty()) {
				customFunction.setName("New task");
			}
			currentGroup.getTasks().add(customFunction);

			customFunction = null;

			writeConfigFile();
		} else {
			JOptionPane.showMessageDialog(null, "Nothing to add. Compile first?");
		}
	}

	public void removeCurrentTask(int selectedRow) {
		if (selectedRow >= 0 && selectedRow < currentGroup.getTasks().size()) {
			UserDefinedAction selectedTask = currentGroup.getTasks().get(selectedRow);
			unregisterTask(selectedTask);

			currentGroup.getTasks().remove(selectedRow);

			writeConfigFile();
		} else {
			LOGGER.info("Select a row from the table to remove.");
		}
	}

	public void removeTask(UserDefinedAction toRemove) {
		for (TaskGroup group : taskGroups) {
			for (Iterator<UserDefinedAction> iterator = group.getTasks().iterator(); iterator.hasNext();) {
				UserDefinedAction action = iterator.next();
				if (action != toRemove) {
					continue;
				}
				unregisterTask(action);

				iterator.remove();

				writeConfigFile();
				return;
			}
		}
	}

	public void moveTaskUp(int selected) {
		if (selected < 1) {
			return;
		}
		Collections.swap(currentGroup.getTasks(), selected, selected - 1);
	}

	public void moveTaskDown(int selected) {
		if (selected >= 0 && selected < currentGroup.getTasks().size() - 1) {
			Collections.swap(currentGroup.getTasks(), selected, selected + 1);
		}
	}

	public void changeTaskGroup(int taskIndex, int newGroupIndex) {
		TaskGroup destination = taskGroups.get(newGroupIndex);
		if (destination == currentGroup) {
			LOGGER.warning("Cannot move to the same group.");
			return;
		}

		if (currentGroup.isEnabled() ^ destination.isEnabled()) {
			LOGGER.warning("Two groups must be both enabled or disabled to move...");
			return;
		}

		UserDefinedAction toMove = currentGroup.getTasks().remove(taskIndex);
		destination.getTasks().add(toMove);
		writeConfigFile();
	}

	public void overwriteTask(int selected) {
		if (customFunction == null) {
			LOGGER.info("Nothing to override. Compile first?");
			return;
		}

		UserDefinedAction toRemove = currentGroup.getTasks().get(selected);
		customFunction.override(toRemove);

		unregisterTask(toRemove);
		keysManager.registerTask(customFunction);
		currentGroup.getTasks().set(selected, customFunction);

		LOGGER.info("Successfully overridden task " + customFunction.getName());
		customFunction = null;
		if (!config.writeConfig()) {
			LOGGER.warning("Unable to update config.");
		}
	}

	public void changeHotkeyTask(UserDefinedAction action, TaskActivation newActivation) {
		if (newActivation == null) {
			return;
		}

		Set<UserDefinedAction> collisions = keysManager.isActivationRegistered(newActivation);
		collisions.remove(action);
		if (!collisions.isEmpty()) {
			GlobalEventsManager.showCollisionWarning(null, collisions);
			return;
		}

		keysManager.reRegisterTask(action, newActivation);
	}

	public void switchEnableTask(UserDefinedAction action) {
		if (action.isEnabled()) { // Then disable it
			action.setEnabled(false);
			if (!action.isEnabled()) {
				keysManager.unregisterTask(action);
			}
		} else { // Then enable it
			Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(action);
			if (!collisions.isEmpty()) {
				GlobalEventsManager.showCollisionWarning(null, collisions);
				return;
			}

			action.setEnabled(true);
			if (action.isEnabled()) {
				keysManager.registerTask(action);
			}
		}
	}

	public String getSource(int row) {
		// Load source if possible
		if (row < 0 || row >= currentGroup.getTasks().size()) {
			return null;
		}

		UserDefinedAction task = currentGroup.getTasks().get(row);
		String source = task.getSource();

		if (source == null) {
			LOGGER.warning("Cannot retrieve source code for task " + task.getName() + ".\nTry recompiling and add again");
			return null;
		}

		return source;
	}

	/**
	 * Populate all tasks with task invoker to dynamically execute other tasks.
	 */
	private void setTaskInvoker() {
		for (TaskGroup taskGroup : taskGroups) {
			for (UserDefinedAction task : taskGroup.getTasks()) {
				task.setTaskInvoker(taskInvoker);
			}
		}
	}

	/*************************************************************************************************************/
	/********************************************Source code related**********************************************/

	public String generateSource() {
		String source = "";
		if (applySpeedup()) {
			source = recorder.getGeneratedCode(getSelectedLanguage());
		}
		return source;
	}

	public void importTasks(File inputFile) {
		ZipUtility.unZipFile(inputFile.getAbsolutePath(), ".");
		File src = new File("tmp");
		File dst = new File(".");
		boolean moved = FileUtility.moveDirectory(src, dst);
		if (!moved) {
			LOGGER.warning("Failed to move files from " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
			JOptionPane.showMessageDialog(null, "Failed to move files.");
			return;
		}
		int existingGroupCount = taskGroups.size();
		boolean result = config.importTaskConfig();
		FileUtility.deleteFile(new File("tmp"));

		if (taskGroups.size() > existingGroupCount) {
			currentGroup = taskGroups.get(existingGroupCount); // Take the new group with lowest index.
			setTaskInvoker();
		} else {
			JOptionPane.showMessageDialog(null, "No new task group found!");
			return;
		}
		if (result) {
			JOptionPane.showMessageDialog(null, "Successfully imported tasks. Switching to a new task group...");
		} else {
			JOptionPane.showMessageDialog(null, "Encountered error(s) while importing tasks. Switching to a new task group...");
		}
	}

	public void exportTasks(File outputDirectory) {
		File destination = new File(FileUtility.joinPath(outputDirectory.getAbsolutePath(), "tmp"));
		String zipPath = FileUtility.joinPath(outputDirectory.getAbsolutePath(), "repeat_export.zip");

		FileUtility.createDirectory(destination.getAbsolutePath());
		config.exportTasksConfig(destination);
		// Now create a zip file containing all source codes together with the config file
		for (TaskGroup group : taskGroups) {
			for (UserDefinedAction task : group.getTasks()) {
				File sourceFile = new File(task.getSourcePath());
				String destPath = FileUtility.joinPath(destination.getAbsolutePath(), FileUtility.getRelativePwdPath(sourceFile));
				File destFile = new File(destPath);
				FileUtility.copyFile(sourceFile, destFile);
			}
		}

		File zipFile = new File(zipPath);
		ZipUtility.zipDir(destination, zipFile);
		FileUtility.deleteFile(destination);

		JOptionPane.showMessageDialog(null, "Data exported to " + zipPath);
	}

	public void cleanUnusedSource() {
		List<File> files = FileUtility.walk(FileUtility.joinPath("data", "source"));
		Set<String> allNames = new HashSet<>(new Function<File, String>() {
			@Override
			public String apply(File file) {
				return file.getAbsolutePath();
			}
		}.map(files));

		Set<String> using = new HashSet<>();
		for (TaskGroup group : taskGroups) {
			using.addAll(new Function<UserDefinedAction, String>() {
				@Override
				public String apply(UserDefinedAction task) {
					return new File(task.getSourcePath()).getAbsolutePath();
				}
			}.map(group.getTasks()));
		}

		allNames.removeAll(using);
		if (allNames.size() == 0) {
			LOGGER.info("Nothing to clean...");
			return;
		}

		int count = 0, failed = 0;
		for (String name : allNames) {
			if (FileUtility.removeFile(new File(name))) {
				count++;
			} else {
				failed++;
			}
		}

		LOGGER.info("Successfully cleaned " + count + " files.\n Failed to clean " + failed + " files.");
	}

	/*************************************************************************************************************/
	/***************************************Source compilation****************************************************/

	public Language getSelectedLanguage() {
		return compilingLanguage;
	}

	public AbstractNativeCompiler getCompiler() {
		return config.getCompilerFactory().getCompiler(getSelectedLanguage());
	}

	public void setCompilingLanguage(Language language) {
		compilingLanguage = language;

		customFunction = null;
	}

	protected void configureCurrentCompiler() {
		getCompiler().configure();
	}

	protected void changeCompilerPath() {
		getCompiler().promptChangePath(null);
	}

	protected boolean compileSource(String source) {
		return compileSource(source, null);
	}

	public boolean compileSource(String source, String taskName) {
		source = source.replaceAll("\t", "    "); // Use spaces instead of tabs

		AbstractNativeCompiler compiler = getCompiler();
		Pair<DynamicCompilerOutput, UserDefinedAction> compilationResult = compiler.compile(source);
		DynamicCompilerOutput compilerStatus = compilationResult.getA();
		UserDefinedAction createdInstance = compilationResult.getB();
		if (taskName != null && !taskName.isEmpty()) {
			createdInstance.setName(taskName);
		}

		if (compilerStatus != DynamicCompilerOutput.COMPILATION_SUCCESS) {
			return false;
		}

		customFunction = createdInstance;
		customFunction.setTaskInvoker(taskInvoker);
		customFunction.setCompiler(compiler.getName());

		if (!TaskSourceManager.submitTask(customFunction, source)) {
			JOptionPane.showMessageDialog(null, "Error writing source file...");
			return false;
		}
		return true;
	}

	/*************************************************************************************************************/
	/***************************************Configurations********************************************************/
	// Write configuration file
	protected boolean writeConfigFile() {
		boolean result = config.writeConfig();
		if (!result) {
			LOGGER.warning("Unable to update config.");
		}

		return result;
	}

	public void changeDebugLevel(Level level) {
		config.setNativeHookDebugLevel(level);

		// Get the logger for "org.jnativehook" and set the level to appropriate level.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(config.getNativeHookDebugLevel());
	}

	public void haltAllTasks() {
		keysManager.haltAllTasks();
	}

	/**
	 * Apply the current speedup in the textbox.
	 * This attempts to parse the speedup.
	 *
	 * @return if the speedup was successfully parsed and applied.
	 */
	private boolean applySpeedup() {
		recorder.setSpeedup(replayConfig.getSpeedup());
		return true;
	}

	/*************************************************************************************************************/
	/***************************************User Interface********************************************************/
	protected void launchUI() {
		int port = IPCServiceManager.getIPCService(IPCServiceName.WEB_UI_SERVER).getPort();
		String url = "http://localhost:" + port;

		if (DialogUtil.getConfirmation(null, "Server ready!", "Initialization finished. UI server is at " + url + ". Go there?")) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				LOGGER.log(Level.WARNING, "Unable to go to UI server.", e);
			}
		}
	}

	/*************************************************************************************************************/
	/***************************************Generic Getters and Setters*******************************************/
	public Recorder getRecorder() {
		return recorder;
	}

	public Config getConfig() {
		return config;
	}

	public synchronized boolean isRecording() {
		return isRecording;
	}

	public ReplayConfig getReplayConfig() {
		return replayConfig;
	}

	public synchronized boolean isReplaying() {
		return isReplaying;
	}

	public synchronized boolean isRunningCompiledAction() {
		return isRunningCompiledTask;
	}

	public GlobalEventsManager getKeysManager() {
		return keysManager;
	}

	public String getLogsSince(long time) {
		return logHolder.getContentSince(time);
	}

	public void clearLogs() {
		logHolder.clear();
	}

	public void addTaskGroup(TaskGroup group) {
		taskGroups.add(group);
	}

	public void clearTaskGroup() {
		taskGroups.clear();
	}

	/**
	 * Get the task group with the index, returning null if index is out of range.
	 */
	public TaskGroup getTaskGroup(int index) {
		if (index < 0 || index >= taskGroups.size()) {
			return null;
		}
		return taskGroups.get(index);
	}

	/**
	 * Get the first task group with the given name, or null if no such group
	 * exists.
	 */
	public TaskGroup getTaskGroup(String name) {
		for (TaskGroup group : taskGroups) {
			if (group.getName().equals(name)) {
				return group;
			}
		}
		return null;
	}

	/** Retrieve an immutable view of the list of task groups. */
	public List<TaskGroup> getTaskGroups() {
		return Collections.unmodifiableList(taskGroups);
	}

	public int getCurentTaskGroupIndex() {
		TaskGroup current = getCurrentTaskGroup();
		for (int i = 0; i < taskGroups.size(); i++) {
			TaskGroup group = taskGroups.get(i);
			if (group == current) {
				return i;
			}
		}
		return -1;
	}

	public TaskGroup getCurrentTaskGroup() {
		return this.currentGroup;
	}

	public void setCurrentTaskGroup(TaskGroup currentTaskGroup) {
		if (currentTaskGroup != this.currentGroup) {
			this.currentGroup = currentTaskGroup;
			keysManager.setCurrentTaskGroup(currentTaskGroup);
		}
	}

	/**
	 * Get first task with given name in the task group.
	 * Returning null if no task with given name exists.
	 */
	public UserDefinedAction getTask(String name) {
		for (TaskGroup group : taskGroups) {
			UserDefinedAction task = group.getTask(name);
			if (task != null) {
				return task;
			}
		}
		return null;
	}
}
