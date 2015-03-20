package frontEnd;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import utilities.Function;
import utilities.NumberUtility;
import utilities.SwingUtil;
import core.GlobalKeysManager;
import core.TaskSourceManager;
import core.UserDefinedAction;
import core.controller.Core;
import core.languageHandler.DynamicCompiler;
import core.languageHandler.DynamicJavaCompiler;
import core.languageHandler.DynamicPythonCompiler;
import core.recorder.Recorder;

public class BackEndHolder {

	protected ScheduledThreadPoolExecutor executor;
	protected Thread compiledExecutor;

	protected Core core;
	protected Recorder recorder;

	protected final DynamicJavaCompiler javaCompiler;
	protected final DynamicPythonCompiler pythonCompiler;
	protected UserDefinedAction customFunction;

	protected final List<UserDefinedAction> customTasks;
	protected final TaskSourceManager taskManager;

	protected final GlobalKeysManager keysManager;

	protected boolean isRecording, isReplaying, isRunning;


	private final Main main;

	public BackEndHolder(Main main) {
		this.main = main;

		executor = new ScheduledThreadPoolExecutor(10);
		core = new Core();
		keysManager = new GlobalKeysManager(core);
		recorder = new Recorder(core, keysManager);

		customTasks = new ArrayList<>();
		taskManager = new TaskSourceManager();

		javaCompiler = new DynamicJavaCompiler("CustomAction", new String[]{"core"}, new String[]{});
		pythonCompiler = new DynamicPythonCompiler();
	}

	/*************************************************************************************************************/

	protected DynamicCompiler getCompiler() {
		if (main.rbmiCompileJava.isSelected()) {
			return javaCompiler;
		} else if (main.rbmiCompilePython.isSelected()) {
			return pythonCompiler;
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

			renderTasks();
		} else {
			JOptionPane.showMessageDialog(main, "Nothing to add. Compile first?");
		}
	}

	protected void removeCurrentTask() {
		int selectedRow = main.tTasks.getSelectedRow();
		if (selectedRow >= 0 && selectedRow < customTasks.size()) {
			if (!taskManager.removeTask(customTasks.get(selectedRow))) {
				JOptionPane.showMessageDialog(main, "Encountered error removing source file " + customTasks.get(selectedRow).getSourcePath());
			}
			customTasks.remove(selectedRow);

			renderTasks();
		} else {
			JOptionPane.showMessageDialog(main, "Select a row from the table to remove");
		}
	}

	protected void renderTasks() {
		SwingUtil.TableUtil.ensureRowNumber(main.tTasks, customTasks.size());
		SwingUtil.TableUtil.clearTable(main.tTasks);

		int row = 0;
		for (UserDefinedAction task : customTasks) {
			main.tTasks.setValueAt(task.getName(), row, 0);
			if (task.getHotkey() != -1) {
				main.tTasks.setValueAt(KeyEvent.getKeyText(task.getHotkey()), row, 1);
			} else {
				main.tTasks.setValueAt("None", row, 1);
			}
			row++;
		}
	}

	/*************************************************************************************************************/

	protected void promptSource() {
		StringBuffer sb = new StringBuffer();
		if (main.rbmiCompileJava.isSelected()) {
			sb.append("package core;\n");
			sb.append("import core.controller.Core;\n");
			sb.append("import core.UserDefinedAction;\n");


			sb.append("public class CustomAction extends UserDefinedAction {\n");
			sb.append("    public void action(final Core controller) throws InterruptedException {\n");
			sb.append("        System.out.println(\"hello\");\n");
			sb.append("        controller.mouse().move(0, 0);\n");
			sb.append("        controller.mouse().moveBy(300, 200);\n");
			sb.append("        controller.mouse().moveBy(-200, 200);\n");
			sb.append("        controller.blockingWait(1000);\n");
			sb.append("    }\n");
			sb.append("}");
		} else if (main.rbmiCompilePython.isSelected()) {
			sb.append("import repeat_lib\n");
			sb.append("if __name__ == \"__main__\":\n");
			sb.append("    print \"Hello\"\n");
			sb.append("    repeat_lib.mouseMoveBy(100, 0)");
		}
		main.taSource.setText(sb.toString());
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
}