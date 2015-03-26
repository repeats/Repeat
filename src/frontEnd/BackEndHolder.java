package frontEnd;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import utilities.FileUtility;
import utilities.Function;
import utilities.NumberUtility;
import utilities.SwingUtil;
import core.GlobalKeysManager;
import core.TaskSourceManager;
import core.UserDefinedAction;
import core.config.Config;
import core.controller.Core;
import core.languageHandler.compiler.DynamicCompiler;
import core.languageHandler.sourceGenerator.JavaSourceGenerator;
import core.recorder.Recorder;

public class BackEndHolder {

	protected ScheduledThreadPoolExecutor executor;
	protected Thread compiledExecutor;

	protected Core core;
	protected Recorder recorder;

	protected UserDefinedAction customFunction;

	protected final List<UserDefinedAction> customTasks;
	protected int selectedTaskIndex;
	protected final TaskSourceManager taskManager;

	protected final GlobalKeysManager keysManager;
	protected final Config config;

	protected boolean isRecording, isReplaying, isRunning;

	private final Main main;

	public BackEndHolder(Main main) {
		this.main = main;
		config = new Config(this);

		executor = new ScheduledThreadPoolExecutor(10);
		core = new Core();
		keysManager = new GlobalKeysManager(config, core);
		recorder = new Recorder(core, keysManager);

		customTasks = new ArrayList<>();
		selectedTaskIndex = -1;
		taskManager = new TaskSourceManager();
	}

	/*************************************************************************************************************/

	protected DynamicCompiler getCompiler() {
		if (main.rbmiCompileJava.isSelected()) {
			return config.compilerFactory().getCompiler("java");
		} else if (main.rbmiCompilePython.isSelected()) {
			return config.compilerFactory().getCompiler("python");
		} else {
			return null;
		}
	}

	protected void switchRecord() {
		if (!isRecording) {//Start record
			recorder.clear();
			recorder.record();
			isRecording = true;
			main.bRecord.setText("Stop");

			setEnableReplay(false);
		} else {//Stop record
			recorder.stopRecord();
			isRecording = false;
			main.bRecord.setText("Record");

			setEnableReplay(true);
		}
	}

	protected void switchReplay() {
		if (isReplaying) {
			isReplaying = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bReplay.setText("Replay");
					setEnableRecord(true);
				}
			});
			recorder.stopReplay();
		} else {
			isReplaying = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					main.bReplay.setText("Stop replay");
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
						customFunction.action(core);
					} catch (InterruptedException e) {//Stopped prematurely
						return;
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
	protected void addCurrentTask() {
		if (customFunction != null) {
			customFunction.setName("New task");
			customFunction.setHotkey(-1);
			customTasks.add(customFunction);

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

		if (selectedRow >= 0 && selectedRow < customTasks.size()) {
			UserDefinedAction selectedTask = customTasks.get(selectedRow);
			keysManager.unregisterKey(selectedTask.getHotkey());
			if (!taskManager.removeTask(selectedTask)) {
				JOptionPane.showMessageDialog(main, "Encountered error removing source file " + customTasks.get(selectedRow).getSourcePath());
			}

			customTasks.remove(selectedRow);


			renderTasks();
		} else {
			JOptionPane.showMessageDialog(main, "Select a row from the table to remove");
		}
	}

	protected void renderTasks() {
		SwingUtil.TableUtil.setRowNumber(main.tTasks, customTasks.size());
		SwingUtil.TableUtil.clearTable(main.tTasks);

		int row = 0;
		for (UserDefinedAction task : customTasks) {
			main.tTasks.setValueAt(task.getName(), row, 0);
			if (task.getHotkey() != -1) {
				main.tTasks.setValueAt(KeyEvent.getKeyText(task.getHotkey()), row, 1);

				if (!keysManager.isKeyRegistered(task.getHotkey())) {
					keysManager.registerKey(task.getHotkey(), task);
				}
			} else {
				main.tTasks.setValueAt("None", row, 1);
			}
			row++;
		}
	}

	protected void keyReleaseTaskTable(KeyEvent e) {
		int row = main.tTasks.getSelectedRow();
		int column = main.tTasks.getSelectedColumn();

		final int COLUMN_TASK_NAME = 0;
		final int COLUMN_TASK_HOTKEY = 1;

		if (column == COLUMN_TASK_NAME && row >= 0) {
			customTasks.get(row).setName(SwingUtil.TableUtil.getStringValueTable(main.tTasks, row, column));
		} else if (column == COLUMN_TASK_HOTKEY && row >= 0) {
			final UserDefinedAction action = customTasks.get(row);
			if (e.getKeyCode() == config.HALT_TASK) {
				keysManager.unregisterKey(action.getHotkey());
				action.setHotkey(-1);
				main.tTasks.setValueAt("None", row, column);
			} else if (!keysManager.isKeyRegistered(e.getKeyCode())) {
				keysManager.reRegisterKey(e.getKeyCode(), action.getHotkey(), action);

				action.setHotkey(e.getKeyCode());
				main.tTasks.setValueAt(KeyEvent.getKeyText(e.getKeyCode()), row, column);
			} else {
				JOptionPane.showMessageDialog(main, "Key " + KeyEvent.getKeyText(e.getKeyCode()) + " already registered. Remove it first");
			}
		}

		//Load source if needed and possible
		if (row >= 0 && row < customTasks.size()) {
			if (selectedTaskIndex != row) {
				String sourcePath = customTasks.get(row).getSourcePath();
				StringBuffer source = FileUtility.readFromFile(new File(sourcePath));
				if (source != null) {
					main.taSource.setText(source.toString());
				} else {
					JOptionPane.showMessageDialog(main, "Unable to load source from file " + sourcePath);
				}
			}
		}
		selectedTaskIndex = row;
	}

	protected void mouseReleaseTaskTable(MouseEvent e) {
		int row = main.tTasks.getSelectedRow();

		//Load source if possible
		if (row >= 0 && row < customTasks.size()) {
			if (selectedTaskIndex != row) {
				String sourcePath = customTasks.get(row).getSourcePath();

				StringBuffer source = FileUtility.readFromFile(new File(sourcePath));
				if (source != null) {
					main.taSource.setText(source.toString());
				} else {
					JOptionPane.showMessageDialog(main, "Cannot load source file " + sourcePath + ".\nTry recompiling and add again");
				}
			}
		}
		selectedTaskIndex = row;
	}

	/*************************************************************************************************************/

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
	public List<UserDefinedAction> getCustomTasks() {
		return customTasks;
	}

}