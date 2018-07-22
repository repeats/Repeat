package frontEnd;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;

import core.config.Config;
import core.controller.Core;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;
import core.keyChain.managers.GlobalEventsManager;
import core.languageHandler.Language;
import core.languageHandler.compiler.AbstractNativeCompiler;
import core.languageHandler.compiler.DynamicCompilerOutput;
import core.languageHandler.compiler.PythonRemoteCompiler;
import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import core.recorder.Recorder;
import core.recorder.ReplayConfig;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskInvoker;
import core.userDefinedTask.TaskSourceManager;
import core.userDefinedTask.UserDefinedAction;
import staticResources.BootStrapResources;
import utilities.DateUtility;
import utilities.FileUtility;
import utilities.Function;
import utilities.OSIdentifier;
import utilities.Pair;
import utilities.StringUtilities;
import utilities.ZipUtility;
import utilities.swing.KeyChainInputPanel;
import utilities.swing.SwingUtil;

public class MainBackEndHolder {

	private static final Logger LOGGER = Logger.getLogger(MainBackEndHolder.class.getName());

	protected StringBuffer logHolder;

	protected ScheduledThreadPoolExecutor executor;
	private Thread compiledExecutor;

	private ReplayConfig replayConfig;
	protected Recorder recorder;

	private UserDefinedAction customFunction;

	protected final List<TaskGroup> taskGroups;
	private TaskGroup currentGroup;
	private int selectedTaskIndex;

	// To allow executing other tasks programmatically.
	private final TaskInvoker taskInvoker;
	protected final GlobalEventsManager keysManager;

	protected final Config config;

	protected final UserDefinedAction switchRecord, switchReplay, switchReplayCompiled;
	private boolean isRecording, isReplaying, isRunningCompiledTask;

	private File tempSourceFile;

	protected final MainFrame main;

	public MainBackEndHolder(MainFrame main) {
		this.main = main;
		config = new Config(this);

		logHolder = new StringBuffer();

		executor = new ScheduledThreadPoolExecutor(10);

		taskGroups = new ArrayList<>();
		selectedTaskIndex = -1;

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
				renderTasks();
				return null;
			}
		});
	}

	/*************************************************************************************************************/
	/************************************************Config*******************************************************/
	protected void loadConfig(File file) {
		config.loadConfig(file);
		setTaskInvoker();

		File pythonExecutable = ((PythonRemoteCompiler) (config.getCompilerFactory()).getCompiler(Language.PYTHON)).getPath();
		((PythonIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.PYTHON)).setExecutingProgram(pythonExecutable);

		applyDebugLevel();
		renderSettings();
	}

	/*************************************************************************************************************/
	/************************************************IPC**********************************************************/
	protected void initiateBackEndActivities() {
		executor.scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
				final Point p = Core.getInstance().mouse().getPosition();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						main.tfMousePosition.setText(p.x + ", " + p.y);
					}
				});
			}
		}, 0, 500, TimeUnit.MILLISECONDS);

		executor.scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						long time = 0;
						for (TaskGroup group : taskGroups) {
							for (UserDefinedAction action : group.getTasks()) {
								time += action.getStatistics().getTotalExecutionTime();
							}
						}
						main.lSecondsSaved.setText((time/1000f) + "");
						renderTasks();
					}
				});
			}
		}, 0, 1500, TimeUnit.MILLISECONDS);

		try {
			IPCServiceManager.initiateServices(this);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "IO Exception when launching ipcs.", e);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception when launching ipcs.", e);
		}
	}

	protected void stopBackEndActivities() {
		try {
			IPCServiceManager.stopServices();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to stop ipcs.", e);
		}
	}

	protected void exit() {
		stopBackEndActivities();

		if (!writeConfigFile()) {
			JOptionPane.showMessageDialog(main, "Error saving configuration file.");
			System.exit(2);
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
			main.bRecord.setIcon(BootStrapResources.STOP);

			setEnableReplay(false);
		} else { // Stop record
			recorder.stopRecord();
			isRecording = false;
			main.bRecord.setIcon(BootStrapResources.RECORD);

			setEnableReplay(true);
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bReplay.setIcon(BootStrapResources.PLAY);
					setEnableRecord(true);
				}
			});
			recorder.stopReplay();
		} else {
			if (!applySpeedup()) {
				return;
			}

			isReplaying = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bReplay.setIcon(BootStrapResources.STOP);
					setEnableRecord(false);
				}
			});

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

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bRun.setIcon(BootStrapResources.PLAY_COMPILED_IMAGE);
				}
			});
		} else {
			if (customFunction == null) {
				JOptionPane.showMessageDialog(main, "No compiled action in memory");
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

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bRun.setIcon(BootStrapResources.STOP_COMPILED_IMAGE);
				}
			});
		}
	}

	/*************************************************************************************************************/
	/*****************************************Task group related**************************************************/
	protected void renderTaskGroup() {
		main.taskGroup.renderTaskGroup();

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
		if (index < 0 || index >= taskGroups.size()) {
			return;
		}
		Collections.swap(taskGroups, index, index - 1);
	}

	public void moveTaskGroupDown(int index) {
		if (index < 0 || index >= taskGroups.size()) {
			return;
		}
		Collections.swap(taskGroups, index, index + 1);
	}

	/*************************************************************************************************************/
	/*****************************************Task related********************************************************/

	/**
	 * @see #editSourceCode(String)
	 */
	protected void editSourceCode() {
		editSourceCode(main.taSource.getText());
	}

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
			JOptionPane.showMessageDialog(main, "Encountered error creating temporary source file.\n" + e.getMessage());
			return;
		}

		try {
			Desktop.getDesktop().open(tempSourceFile);
			int update = JOptionPane.showConfirmDialog(main, "Update source code from editted source file? (Confirm once done)", "Reload source code", JOptionPane.YES_NO_OPTION);
			if (update == JOptionPane.YES_OPTION) {
				reloadSourceCode();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(main, "Unable to open file for editting.\n" + e.getMessage());
		}
	}

	/**
	 * Load the source code from the temporary source code file into the text area (if the source code file exists).
	 */
	public String reloadSourceCode() {
		if (tempSourceFile == null || !tempSourceFile.exists()) {
			JOptionPane.showMessageDialog(main, "Temp file not accessible.");
			return null;
		}

		StringBuffer sourceCode = FileUtility.readFromFile(tempSourceFile);
		if (sourceCode == null) {
			JOptionPane.showMessageDialog(main, "Unable to read from temp file.");
			return null;
		}
		String source = sourceCode.toString();
		main.taSource.setText(source);
		return source;
	}

	private void unregisterTask(UserDefinedAction task) {
		keysManager.unregisterTask(task);
		if (!TaskSourceManager.removeTask(task)) {
			JOptionPane.showMessageDialog(main, "Encountered error removing source file " + task.getSourcePath());
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

			renderTasks();
			writeConfigFile();
		} else {
			JOptionPane.showMessageDialog(main, "Nothing to add. Compile first?");
		}

		int selectedRow = main.tTasks.getSelectedRow();
		selectedTaskIndex = selectedRow;
	}

	protected void removeCurrentTask() {
		int selectedRow = main.tTasks.getSelectedRow();
		removeCurrentTask(selectedRow);
	}

	public void removeCurrentTask(int selectedRow) {
		selectedTaskIndex = selectedRow;

		if (selectedRow >= 0 && selectedRow < currentGroup.getTasks().size()) {
			UserDefinedAction selectedTask = currentGroup.getTasks().get(selectedRow);
			unregisterTask(selectedTask);

			currentGroup.getTasks().remove(selectedRow);
			selectedTaskIndex = - 1; // Reset selected index

			renderTasks();
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

				renderTasks();
				writeConfigFile();
				return;
			}
		}
	}

	protected void moveTaskUp() {
		int selected = main.tTasks.getSelectedRow();
		moveTaskUp(selected);
	}

	public void moveTaskUp(int selected) {
		if (selected < 1) {
			return;
		}
		Collections.swap(currentGroup.getTasks(), selected, selected - 1);
		main.tTasks.setRowSelectionInterval(selected - 1, selected - 1);
		renderTasks();
	}

	protected void moveTaskDown() {
		int selected = main.tTasks.getSelectedRow();
		moveTaskDown(selected);
	}

	public void moveTaskDown(int selected) {
		if (selected >= 0 && selected < currentGroup.getTasks().size() - 1) {
			Collections.swap(currentGroup.getTasks(), selected, selected + 1);
			main.tTasks.setRowSelectionInterval(selected + 1, selected + 1);
			renderTasks();
		}
	}

	protected void changeTaskGroup() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 0 && selected < currentGroup.getTasks().size()) {
			int newGroupIndex = SwingUtil.DialogUtil.getSelection(null, "Select new group",
				new Function<TaskGroup, String>() {
					@Override
					public String apply(TaskGroup d) {
						return d.getName();
					}
				}.map(taskGroups).toArray(new String[taskGroups.size()]), -1);

			if (newGroupIndex < 0) {
				return;
			}

			changeTaskGroup(selected, newGroupIndex);
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
		renderTasks();
		writeConfigFile();
	}

	protected void overwriteTask() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 0) {
			overwriteTask(selected);
		} else {
			JOptionPane.showMessageDialog(main, "Select a task to override");
		}
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

	protected void changeHotkeyTask(int row) {
		final UserDefinedAction action = currentGroup.getTasks().get(row);
		TaskActivation newActivation = KeyChainInputPanel.getInputActivation(main, action.getActivation());
		if (newActivation == null) {
			return;
		}

		Set<UserDefinedAction> collisions = keysManager.isActivationRegistered(newActivation);
		collisions.remove(action);
		if (!collisions.isEmpty()) {
			GlobalEventsManager.showCollisionWarning(main, collisions);
			return;
		}

		keysManager.reRegisterTask(action, newActivation);

		KeyChain representative = action.getRepresentativeHotkey();
		main.tTasks.setValueAt(representative.isEmpty() ? "None" : representative.toString(), row, MainFrame.TTASK_COLUMN_TASK_HOTKEY);
	}

	protected void switchEnableTask(int row) {
		final UserDefinedAction action = currentGroup.getTasks().get(row);

		if (action.isEnabled()) { // Then disable it
			action.setEnabled(false);
			if (!action.isEnabled()) {
				keysManager.unregisterTask(action);
			}
		} else { // Then enable it
			Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(action);
			if (!collisions.isEmpty()) {
				GlobalEventsManager.showCollisionWarning(main, collisions);
				return;
			}

			action.setEnabled(true);
			if (action.isEnabled()) {
				keysManager.registerTask(action);
			}
		}

		main.tTasks.setValueAt(action.isEnabled(), row, MainFrame.TTASK_COLUMN_ENABLED);
	}

	protected void renderTasks() {
		main.bTaskGroup.setText(currentGroup.getName());
		SwingUtil.TableUtil.setRowNumber(main.tTasks, currentGroup.getTasks().size());
		SwingUtil.TableUtil.clearTable(main.tTasks);

		int row = 0;
		for (UserDefinedAction task : currentGroup.getTasks()) {
			main.tTasks.setValueAt(task.getName(), row, MainFrame.TTASK_COLUMN_TASK_NAME);

			KeyChain representative = task.getRepresentativeHotkey();
			if (representative != null && !representative.isEmpty()) {
				main.tTasks.setValueAt(representative.toString(), row, MainFrame.TTASK_COLUMN_TASK_HOTKEY);
			} else {
				main.tTasks.setValueAt("None", row, MainFrame.TTASK_COLUMN_TASK_HOTKEY);
			}

			main.tTasks.setValueAt(task.isEnabled(), row, MainFrame.TTASK_COLUMN_ENABLED);
			main.tTasks.setValueAt(task.getStatistics().getCount(), row, MainFrame.TTASK_COLUMN_USE_COUNT);
			main.tTasks.setValueAt(DateUtility.calendarToDateString(task.getStatistics().getLastUse()), row, MainFrame.TTASK_COLUMN_LAST_USE);
			row++;
		}
	}

	protected void keyReleaseTaskTable(KeyEvent e) {
		int row = main.tTasks.getSelectedRow();
		int column = main.tTasks.getSelectedColumn();

		if (column == MainFrame.TTASK_COLUMN_TASK_NAME && row >= 0) {
			currentGroup.getTasks().get(row).setName(SwingUtil.TableUtil.getStringValueTable(main.tTasks, row, column));
		} else if (column == MainFrame.TTASK_COLUMN_TASK_HOTKEY && row >= 0) {
			if (e.getKeyCode() == Config.HALT_TASK) {
				final UserDefinedAction action = currentGroup.getTasks().get(row);
				keysManager.unregisterTask(action);
				action.getActivation().getHotkeys().clear();
				main.tTasks.setValueAt("None", row, column);
			} else {
				changeHotkeyTask(row);
			}
		} else if (column == MainFrame.TTASK_COLUMN_ENABLED && row >= 0) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				switchEnableTask(row);
			}
		}

		loadSource(row);
		selectedTaskIndex = row;
	}

	protected void mouseReleaseTaskTable(MouseEvent e) {
		int row = main.tTasks.getSelectedRow();
		int column = main.tTasks.getSelectedColumn();

		if (column == MainFrame.TTASK_COLUMN_TASK_HOTKEY && row >= 0) {
			changeHotkeyTask(row);
		} else if (column == MainFrame.TTASK_COLUMN_ENABLED && row >= 0) {
			switchEnableTask(row);
		}

		loadSource(row);
		selectedTaskIndex = row;
	}

	private void loadSource(int row) {
		String source = getSource(row);
		if (source == null) {
			return;
		}

		main.taSource.setText(source);
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

		if (!task.getCompiler().equals(getCompiler().getName())) {
			main.languageSelection.get(task.getCompiler()).setSelected(true);
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

	protected void promptSource() {
		StringBuffer sb = new StringBuffer();
		sb.append(AbstractSourceGenerator.getReferenceSource(getSelectedLanguage()));
		main.taSource.setText(sb.toString());
	}

	protected void generateSource() {
		if (applySpeedup()) {
			main.taSource.setText(recorder.getGeneratedCode(getSelectedLanguage()));
		}
	}

	protected void importTasks(File inputFile) {
		ZipUtility.unZipFile(inputFile.getAbsolutePath(), ".");
		File src = new File("tmp");
		File dst = new File(".");
		boolean moved = FileUtility.moveDirectory(src, dst);
		if (!moved) {
			LOGGER.warning("Failed to move files from " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
			JOptionPane.showMessageDialog(main, "Failed to move files.");
			return;
		}
		int existingGroupCount = taskGroups.size();
		boolean result = config.importTaskConfig();
		FileUtility.deleteFile(new File("tmp"));

		if (taskGroups.size() > existingGroupCount) {
			currentGroup = taskGroups.get(existingGroupCount); // Take the new group with lowest index.
			setTaskInvoker();
		} else {
			JOptionPane.showMessageDialog(main, "No new task group found!");
			return;
		}
		if (result) {
			JOptionPane.showMessageDialog(main, "Successfully imported tasks. Switching to a new task group...");
		} else {
			JOptionPane.showMessageDialog(main, "Encountered error(s) while importing tasks. Switching to a new task group...");
		}
	}

	protected void exportTasks(File outputDirectory) {
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

		JOptionPane.showMessageDialog(main, "Data exported to " + zipPath);
	}

	protected void cleanUnusedSource() {
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
			JOptionPane.showMessageDialog(main, "Nothing to clean...");
			return;
		}

		String[] titles = new String[allNames.size()];
		Arrays.fill(titles, "Deleting");
		int confirmDelete = SwingUtil.OptionPaneUtil.confirmValues("Delete these files?", titles, allNames.toArray(new String[0]));
		if (confirmDelete == JOptionPane.OK_OPTION) {
			int count = 0, failed = 0;
			for (String name : allNames) {
				if (FileUtility.removeFile(new File(name))) {
					count++;
				} else {
					failed++;
				}
			}

			JOptionPane.showMessageDialog(main, "Successfully cleaned " + count + " files.\n Failed to clean " + failed + " files.");
		}
	}

	/*************************************************************************************************************/
	/***************************************Source compilation****************************************************/

	public Language getSelectedLanguage() {
		for (JRadioButtonMenuItem rbmi : main.rbmiSelection.keySet()) {
			if (rbmi.isSelected()) {
				return main.rbmiSelection.get(rbmi);
			}
		}

		throw new IllegalStateException("Undefined state. No language selected.");
	}

	protected AbstractNativeCompiler getCompiler() {
		return config.getCompilerFactory().getCompiler(getSelectedLanguage());
	}

	protected void refreshCompilingLanguage() {
		customFunction = null;
		getCompiler().changeCompilationButton(main.bCompile);
		promptSource();
	}

	protected void configureCurrentCompiler() {
		getCompiler().configure();
	}

	protected void changeCompilerPath() {
		getCompiler().promptChangePath(main);
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
			JOptionPane.showMessageDialog(main, "Error writing source file...");
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

	private final Level[] DEBUG_LEVELS = {Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE};

	protected void applyDebugLevel() {
		Level debugLevel = config.getNativeHookDebugLevel();
		final JRadioButtonMenuItem[] buttons = {main.rbmiDebugSevere, main.rbmiDebugWarning, main.rbmiDebugInfo, main.rbmiDebugFine};

		for (int i = 0; i < DEBUG_LEVELS.length; i++) {
			if (debugLevel == DEBUG_LEVELS[i]) {
				buttons[i].setSelected(true);
				break;
			}
		}
	}

	protected void changeDebugLevel() {
		Level debugLevel = Level.WARNING;
		final JRadioButtonMenuItem[] buttons = {main.rbmiDebugSevere, main.rbmiDebugWarning, main.rbmiDebugInfo, main.rbmiDebugFine};

		for (int i = 0; i < DEBUG_LEVELS.length; i++) {
			if (buttons[i].isSelected()) {
				debugLevel = DEBUG_LEVELS[i];
				break;
			}
		}
		config.setNativeHookDebugLevel(debugLevel);

		// Get the logger for "org.jnativehook" and set the level to appropriate level.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(config.getNativeHookDebugLevel());
	}

	protected void renderSettings() {
		if (!OSIdentifier.IS_WINDOWS) {
			main.rbmiCompileCS.setEnabled(false);
		}
		main.cbmiUseTrayIcon.setSelected(config.isUseTrayIcon());
		main.cbmiHaltByKey.setSelected(config.isEnabledHaltingKeyPressed());
		main.cbmiExecuteOnReleased.setSelected(config.isExecuteOnKeyReleased());
	}

	protected void switchTrayIconUse() {
		boolean trayIconEnabled = main.cbmiUseTrayIcon.isSelected();
		config.setUseTrayIcon(trayIconEnabled);
	}

	protected void haltAllTasks() {
		keysManager.haltAllTasks();
	}

	protected void switchHaltByKey() {
		config.setEnabledHaltingKeyPressed(main.cbmiHaltByKey.isSelected());
	}

	protected void switchExecuteOnReleased() {
		config.setExecuteOnKeyReleased(main.cbmiExecuteOnReleased.isSelected());
	}

	/*************************************************************************************************************/
	private void setEnableRecord(boolean state) {
		main.bRecord.setEnabled(state);
	}

	private void setEnableReplay(boolean state) {
		main.bReplay.setEnabled(state);
		main.tfRepeatCount.setEnabled(state);
		main.tfRepeatDelay.setEnabled(state);
		main.tfSpeedup.setEnabled(state);

		if (state) {
			main.tfRepeatCount.setText("1");
			main.tfRepeatDelay.setText("0");
		}
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
	/***************************************JFrame operations*****************************************************/
	protected void focusMainFrame() {
		if (main.taskGroup.isVisible()) {
			main.taskGroup.setVisible(false);
		}

		if (main.hotkey.isVisible()) {
			main.hotkey.setVisible(false);
		}

		if (main.ipcs.isVisible()) {
			main.ipcs.setVisible(false);
		}
	}

	/*************************************************************************************************************/
	/***************************************Generic Getters and Setters*******************************************/
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

	public String getLogs() {
		return logHolder.toString();
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
			this.selectedTaskIndex = -1;
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
