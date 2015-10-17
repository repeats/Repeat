package frontEnd;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import utilities.ExceptableFunction;
import utilities.FileUtility;
import utilities.Function;
import utilities.NumberUtility;
import utilities.swing.KeyChainInputPanel;
import utilities.swing.SwingUtil;

import com.sun.istack.internal.logging.Logger;

import core.config.Config;
import core.controller.Core;
import core.keyChain.GlobalKeysManager;
import core.keyChain.KeyChain;
import core.languageHandler.compiler.DynamicCompiler;
import core.languageHandler.sourceGenerator.JavaSourceGenerator;
import core.recorder.Recorder;
import core.server.ControllerServer;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskSourceManager;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.graphics.BootStrapResources;

public class BackEndHolder {

	private static final Logger LOGGER = Logger.getLogger(BackEndHolder.class);

	protected ScheduledThreadPoolExecutor executor;
	protected Thread compiledExecutor;

	protected Core core;
	protected Recorder recorder;
	protected ControllerServer controllerServer;

	protected UserDefinedAction customFunction;

	protected final List<TaskGroup> taskGroups;
	private TaskGroup currentGroup;

	protected int selectedTaskIndex;
	protected final TaskSourceManager taskManager;

	protected final GlobalKeysManager keysManager;
	protected final Config config;

	protected final UserDefinedAction switchRecord, switchReplay, switchReplayCompiled;
	protected boolean isRecording, isReplaying, isRunning;

	protected final MainFrame main;

	public BackEndHolder(MainFrame main) throws IOException {
		this.main = main;
		config = new Config(this);

		executor = new ScheduledThreadPoolExecutor(10);
		core = new Core();
		controllerServer = new ControllerServer(core);

		keysManager = new GlobalKeysManager(config, core);
		recorder = new Recorder(core, keysManager);

		taskGroups = new ArrayList<>();

		selectedTaskIndex = -1;
		taskManager = new TaskSourceManager();


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

		controllerServer.start();
	}

	protected void exit() {
		controllerServer.stop();

		if (!writeConfigFile()) {
			JOptionPane.showMessageDialog(main, "Error saving configuration file.");
			System.exit(2);
		}

		System.exit(0);
	}

	/*************************************************************************************************************/
	/****************************************Main hotkeys*********************************************************/
	protected void configureMainHotkeys() {
		keysManager.reRegisterTask(switchRecord, Arrays.asList(config.getRECORD()));
		keysManager.reRegisterTask(switchReplay, Arrays.asList(config.getREPLAY()));
		keysManager.reRegisterTask(switchReplayCompiled, Arrays.asList(config.getCOMPILED_REPLAY()));
	}

	/*************************************************************************************************************/
	/****************************************Record and replay****************************************************/

	protected void switchRecord() {
		if (!isRecording) {//Start record
			recorder.clear();
			recorder.record();
			isRecording = true;
			main.bRecord.setIcon(BootStrapResources.STOP);

			setEnableReplay(false);
		} else {//Stop record
			recorder.stopRecord();
			isRecording = false;
			main.bRecord.setIcon(BootStrapResources.RECORD);

			setEnableReplay(true);
		}
	}

	protected void switchReplay() {
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
			isReplaying = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bReplay.setIcon(BootStrapResources.STOP);
					setEnableRecord(false);
				}
			});

			String repeatText = main.tfRepeatCount.getText();
			String delayText = main.tfRepeatDelay.getText();
			if (NumberUtility.isPositiveInteger(repeatText) && NumberUtility.isNonNegativeInteger(delayText)) {
				long repeatCount = Long.parseLong(repeatText);
				long delay = Long.parseLong(delayText);

				recorder.replay(repeatCount, delay, new Function<Void, Void>() {
					@Override
					public Void apply(Void r) {
						switchReplay();
						return null;
					}
				}, 5, false);
			}
		}
	}

	protected void switchRunningCompiledAction() {
		if (isRunning) {
			isRunning = false;
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
					main.bRun.setText("Run Compiled Action");
				}
			});
		} else {
			if (customFunction == null) {
				JOptionPane.showMessageDialog(main, "No compiled action in memory");
				return;
			}

			isRunning = true;

			compiledExecutor = new Thread(new Runnable() {
			    @Override
				public void run() {
			    	try {
			    		customFunction.setExecuteTaskInGroup(new ExceptableFunction<Integer, Void, InterruptedException> () {
							@Override
							public Void apply(Integer d) throws InterruptedException {
								if (currentGroup == null) {
									LOGGER.warning("Task group is null. Cannot execute given task with index " + d);
									return null;
								}
								List<UserDefinedAction> tasks = currentGroup.getTasks();

								if (d >= 0 && d < tasks.size()) {
									currentGroup.getTasks().get(d).action(core);
								} else {
									LOGGER.warning("Index out of bound. Cannot execute given task with index " + d + " given task group only has " + tasks.size() + " elements.");
								}

								return null;
							}
						});
						customFunction.action(core);
					} catch (InterruptedException e) {//Stopped prematurely
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
					main.bRun.setText("Stop running");
				}
			});
		}
	}

	/*************************************************************************************************************/
	/*****************************************Task group related**************************************************/
	protected void renderTaskGroup() {
		main.taskGroup.renderTaskGroup();

		for (TaskGroup group : taskGroups) {
			if (group.isEnabled()) {
				for (UserDefinedAction task : group.getTasks()) {
					Set<KeyChain> collisions = keysManager.areKeysRegistered(task.getHotkeys());
					if (task.isEnabled() && (collisions == null || collisions.isEmpty())) {
						keysManager.registerTask(task);
					}
				}
			}
		}
	}

	/*************************************************************************************************************/
	/*****************************************Task related********************************************************/

	private void removeTask(UserDefinedAction task) {
		keysManager.unregisterTask(task);
		if (!taskManager.removeTask(task)) {
			JOptionPane.showMessageDialog(main, "Encountered error removing source file " + task.getSourcePath());
		}
	}

	protected void addCurrentTask() {
		if (customFunction != null) {
			customFunction.setName("New task");
			currentGroup.getTasks().add(customFunction);

			customFunction = null;

			renderTasks();
		} else {
			JOptionPane.showMessageDialog(main, "Nothing to add. Compile first?");
		}

		int selectedRow = main.tTasks.getSelectedRow();
		selectedTaskIndex = selectedRow;
	}

	protected void removeCurrentTask() {
		int selectedRow = main.tTasks.getSelectedRow();
		selectedTaskIndex = selectedRow;

		if (selectedRow >= 0 && selectedRow < currentGroup.getTasks().size()) {
			UserDefinedAction selectedTask = currentGroup.getTasks().get(selectedRow);
			removeTask(selectedTask);

			currentGroup.getTasks().remove(selectedRow);
			selectedTaskIndex = - 1; //Reset selected index

			renderTasks();
		} else {
			JOptionPane.showMessageDialog(main, "Select a row from the table to remove");
		}
	}

	protected void moveTaskUp() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 1) {
			Collections.swap(currentGroup.getTasks(), selected, selected - 1);
			main.tTasks.setRowSelectionInterval(selected - 1, selected - 1);
			renderTasks();
		}
	}

	protected void moveTaskDown() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 0 && selected < currentGroup.getTasks().size() - 1) {
			Collections.swap(currentGroup.getTasks(), selected, selected + 1);
			main.tTasks.setRowSelectionInterval(selected + 1, selected + 1);
			renderTasks();
		}
	}

	protected void changeTaskGroup() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 0 && selected < currentGroup.getTasks().size()) {
			int newGroupIndex = SwingUtil.OptionPaneUtil.getSelection("Select new group",
				new Function<TaskGroup, String>() {
					@Override
					public String apply(TaskGroup d) {
						return d.getName();
					}
				}.map(taskGroups).toArray(new String[taskGroups.size()]), -1);

			if (newGroupIndex >= 0) {
				TaskGroup destination = taskGroups.get(newGroupIndex);
				if (destination == currentGroup) {
					JOptionPane.showMessageDialog(main, "Cannot move to the same group...");
					return;
				}
				UserDefinedAction toMove = currentGroup.getTasks().remove(selected);
				destination.getTasks().add(toMove);

				renderTasks();
			}
		}
	}

	protected void overrideTask() {
		int selected = main.tTasks.getSelectedRow();
		if (selected >= 0) {
			if (customFunction == null) {
				JOptionPane.showMessageDialog(main, "Nothing to override. Compile first?");
				return;
			}

			UserDefinedAction toRemove = currentGroup.getTasks().get(selected);
			customFunction.setName(toRemove.getName());
			customFunction.setHotKeys(toRemove.getHotkeys());

			removeTask(toRemove);
			keysManager.registerTask(customFunction);
			currentGroup.getTasks().set(selected, customFunction);

			LOGGER.info("Successfully overridden task " + customFunction.getName());
		} else {
			JOptionPane.showMessageDialog(main, "Select a task to override");
		}
	}

	protected void changeHotkeyTask(int row) {
		final UserDefinedAction action = currentGroup.getTasks().get(row);
		Set<KeyChain> newKeyChains = KeyChainInputPanel.getInputKeyChains(main, action.getHotkeys());
		if (newKeyChains != null) {
			Set<KeyChain> collisions = keysManager.areKeysRegistered(newKeyChains);
			if (!collisions.isEmpty()) {
				JOptionPane.showMessageDialog(main,
					"Newly registered keychains "
					+ "will collide with previously registered keychain \"" + collisions
					+ "\"\nYou cannot assign this key chain unless you remove the conflicting key chain...",
					"Key chain collision!", JOptionPane.WARNING_MESSAGE);
				return;
			}

			keysManager.reRegisterTask(action, newKeyChains);

			KeyChain representative = action.getRepresentativeHotkey();
			main.tTasks.setValueAt(representative.getKeys().isEmpty() ? "None" : representative.toString(), row, 1);
		}
	}

	protected void switchEnableTask(int row) {
		final UserDefinedAction action = currentGroup.getTasks().get(row);

		if (action.isEnabled()) {
			keysManager.unregisterTask(action);
		} else {
			Set<KeyChain> collisions = keysManager.areKeysRegistered(action.getHotkeys());
			if (!collisions.isEmpty()) {
				JOptionPane.showMessageDialog(main,
					"One of the newly registered keychains"
					+ " will collide with previously registered keychains \"" + collisions
					+ "\"\nYou cannot assign this key chain unless you remove the conflicting key chain...",
					"Key chain collision!", JOptionPane.WARNING_MESSAGE);
				return;
			}

			keysManager.registerTask(action);
		}

		action.setEnabled(!action.isEnabled());
		main.tTasks.setValueAt(action.isEnabled(), row, 2);
	}

	protected void renderTasks() {
		main.bTaskGroup.setText(currentGroup.getName());
		SwingUtil.TableUtil.setRowNumber(main.tTasks, currentGroup.getTasks().size());
		SwingUtil.TableUtil.clearTable(main.tTasks);

		int row = 0;
		for (UserDefinedAction task : currentGroup.getTasks()) {
			main.tTasks.setValueAt(task.getName(), row, 0);

			KeyChain representative = task.getRepresentativeHotkey();
			if (representative != null && !representative.getKeys().isEmpty()) {
				main.tTasks.setValueAt(representative.toString(), row, 1);
			} else {
				main.tTasks.setValueAt("None", row, 1);
			}

			main.tTasks.setValueAt(task.isEnabled(), row, 2);
			row++;
		}
	}

	protected void keyReleaseTaskTable(KeyEvent e) {
		int row = main.tTasks.getSelectedRow();
		int column = main.tTasks.getSelectedColumn();

		final int COLUMN_TASK_NAME = 0;
		final int COLUMN_TASK_HOTKEY = 1;
		final int COLUMN_ENABLED = 2;

		if (column == COLUMN_TASK_NAME && row >= 0) {
			currentGroup.getTasks().get(row).setName(SwingUtil.TableUtil.getStringValueTable(main.tTasks, row, column));
		} else if (column == COLUMN_TASK_HOTKEY && row >= 0) {
			if (e.getKeyCode() == Config.HALT_TASK) {
				final UserDefinedAction action = currentGroup.getTasks().get(row);
				keysManager.unregisterTask(action);
				action.getHotkeys().clear();
				main.tTasks.setValueAt("None", row, column);
			} else {
				changeHotkeyTask(row);
			}
		} else if (column == COLUMN_ENABLED && row >= 0) {
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

		final int COLUMN_TASK_HOTKEY = 1;
		final int COLUMN_ENABLED = 2;

		if (column == COLUMN_TASK_HOTKEY && row >= 0) {
			changeHotkeyTask(row);
		} else if (column == COLUMN_ENABLED && row >= 0) {
			switchEnableTask(row);
		}

		loadSource(row);
		selectedTaskIndex = row;
	}

	private void loadSource(int row) {
		//Load source if possible
		if (row >= 0 && row < currentGroup.getTasks().size()) {
			if (selectedTaskIndex != row) {
				UserDefinedAction task = currentGroup.getTasks().get(row);
				String sourcePath = task.getSourcePath();

				StringBuffer source = FileUtility.readFromFile(new File(sourcePath));
				if (source != null) {
					main.taSource.setText(source.toString());

					if (!task.getCompiler().equals(getCompiler().getName())) {
						if (task.getCompiler().equals("java")) {
							main.rbmiCompileJava.setSelected(true);
						} else if (task.getCompiler().equals("python")) {
							main.rbmiCompilePython.setSelected(true);
						}
						refreshCompilingLanguage();
					}
				} else {
					JOptionPane.showMessageDialog(main, "Cannot load source file " + sourcePath + ".\nTry recompiling and add again");
				}
			}
		}
	}

	/*************************************************************************************************************/
	/********************************************Source code related**********************************************/

	protected void promptSource() {
		StringBuffer sb = new StringBuffer();
		if (main.rbmiCompileJava.isSelected()) {
			sb.append(new JavaSourceGenerator().getSource());
		} else if (main.rbmiCompilePython.isSelected()) {
			sb.append("import repeat_lib\n");
			sb.append("if __name__ == \"__main__\":\n");
			sb.append("    print \"Hello\"\n");
			sb.append("    repeat_lib.mouseMoveBy(100, 0)");
		}
		main.taSource.setText(sb.toString());
	}

	protected void generateSource() {
		if (main.rbmiCompileJava.isSelected()) {
			main.taSource.setText(recorder.getGeneratedCode(Recorder.JAVA_LANGUAGE));
		} else if (main.rbmiCompilePython.isSelected()) {
			main.taSource.setText(recorder.getGeneratedCode(Recorder.PYTHON_LANGUAGE));
		}
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

	protected DynamicCompiler getCompiler() {
		if (main.rbmiCompileJava.isSelected()) {
			return config.compilerFactory().getCompiler("java");
		} else if (main.rbmiCompilePython.isSelected()) {
			return config.compilerFactory().getCompiler("python");
		} else {
			return null;
		}
	}

	protected void refreshCompilingLanguage() {
		customFunction = null;
		if (main.rbmiCompileJava.isSelected()) {
			main.bCompile.setText("Compile source");
		} else if (main.rbmiCompilePython.isSelected()) {
			main.bCompile.setText("Load source");
			JOptionPane.showMessageDialog(main, "Using python interpreter at "
					+ config.compilerFactory().getCompiler("python").getPath().getAbsolutePath(),
					"Python interpreter not chosen", JOptionPane.OK_OPTION);
		}

		promptSource();
	}

	protected void compileSource() {
		String source = main.taSource.getText();

		DynamicCompiler compiler = getCompiler();
		UserDefinedAction createdInstance = compiler.compile(source);

		if (createdInstance != null) {
			customFunction = createdInstance;
			customFunction.setCompiler(compiler.getName());

			if (!taskManager.submitTask(customFunction, source)) {
				JOptionPane.showMessageDialog(main, "Error writing source file...");
			}
		}
	}

	/*************************************************************************************************************/
	//Write config file
	protected boolean writeConfigFile() {
		return config.writeConfig();
	}

	/*************************************************************************************************************/
	private void setEnableRecord(boolean state) {
		main.bRecord.setEnabled(state);
	}

	private void setEnableReplay(boolean state) {
		main.bReplay.setEnabled(state);
		main.tfRepeatCount.setEnabled(state);
		main.tfRepeatDelay.setEnabled(state);

		if (state) {
			main.tfRepeatCount.setText("1");
			main.tfRepeatDelay.setText("0");
		}
	}

	/*************************************************************************************************************/
	public List<TaskGroup> getTaskGroups() {
		return taskGroups;
	}

	protected TaskGroup getCurrentTaskGroup() {
		return this.currentGroup;
	}

	public void setCurrentTaskGroup(TaskGroup currentTaskGroup) {
		if (currentTaskGroup != this.currentGroup) {
			this.selectedTaskIndex = -1;
			this.currentGroup = currentTaskGroup;
			keysManager.setCurrentTaskGroup(currentTaskGroup);
		}
	}
}