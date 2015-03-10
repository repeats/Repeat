package frontEnd;

import globalListener.GlobalKeyListener;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import utilities.CodeConverter;
import utilities.Function;
import core.Config;
import core.Core;
import core.DynamicCompiler;
import core.DynamicJavaCompiler;
import core.DynamicPythonCompiler;
import core.Recorder;
import core.UserDefinedAction;

public class BackEndHolder {

	protected ScheduledThreadPoolExecutor executor;
	protected ScheduledFuture<?> mouseTracker;
	protected Thread compiledExecutor;

	protected Core core;
	protected Recorder recorder;

	protected final DynamicJavaCompiler javaCompiler;
	protected final DynamicPythonCompiler pythonCompiler;
	protected UserDefinedAction customFunction;

	protected boolean isRecording, isReplaying, isRunning;

	private final Main main;

	public BackEndHolder(Main main) {
		this.main = main;

		executor = new ScheduledThreadPoolExecutor(10);
		core = new Core();
		recorder = new Recorder(core);

		javaCompiler = new DynamicJavaCompiler("CustomAction", new String[]{"core"}, new String[]{});
		pythonCompiler = new DynamicPythonCompiler();
	}

	protected void startGlobalHotkey() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.registerNativeHook();
		}
		GlobalKeyListener keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				if (!main.hotkey.isVisible()) {
					if (CodeConverter.getKeyEventCode(r.getKeyCode()) == Config.RECORD) {
						main.bRecord.doClick();
					} else if (CodeConverter.getKeyEventCode(r.getKeyCode()) == Config.REPLAY) {
						main.bReplay.doClick();
					} else if (CodeConverter.getKeyEventCode(r.getKeyCode()) == Config.COMPILED_REPLAY) {
						main.bRun.doClick();
					}
				}
				return true;
			}
		});
		keyListener.startListening();
	}

	protected DynamicCompiler getCompiler() {
		if (main.rbmiCompileJava.isSelected()) {
			return javaCompiler;
		} else if (main.rbmiCompilePython.isSelected()) {
			return pythonCompiler;
		} else {
			return null;
		}
	}

	protected void forceStopRunningCompiledAction() {
		isRunning = false;
		if (compiledExecutor != null) {
			while (compiledExecutor.isAlive()) {
				compiledExecutor.interrupt();
			}
		}
		
		main.bRun.setText("Run Compiled Action");
	}
	
	protected void promptSource() {
		StringBuffer sb = new StringBuffer();
		if (main.rbmiCompileJava.isSelected()) {
			sb.append("package core;\n");
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
}