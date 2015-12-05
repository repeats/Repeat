package core.ipc.repeatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;

import staticResources.PythonResources;
import core.languageHandler.compiler.DynamicPythonCompiler;

public class PythonIPCClientService extends IPCClientService {

	private static final long TIMEOUT_MS = 5000;
	protected DynamicPythonCompiler compiler;

	private Thread mainThread;
	private Process mainProcess;
	private BufferedReader input;

	@Override
	public void start() throws IOException {
		final String[] cmd = { compiler.getPath().getAbsolutePath(), PythonResources.PYTHON_IPC_CLIENT.getAbsolutePath() };

		mainThread = new Thread(){
			@Override
			public void run() {
				try {
					String line;
					ProcessBuilder processBuilder = new ProcessBuilder(cmd);
					processBuilder.redirectOutput(Redirect.INHERIT);
					processBuilder.redirectError(Redirect.INHERIT);
					mainProcess = processBuilder.start();

					input = new BufferedReader(new InputStreamReader(mainProcess.getInputStream()));

					do {
						line = input.readLine();
						if (line == null) {
							break;
						}

						getLogger().info(line);
						getLogger().info("\n");
					} while (true);

					input.close();
			    } catch (Exception e) {
			    	getLogger().log(Level.WARNING, "Encounter exception while running process: " + cmd[0] + cmd[1], e);
			    }
			}
		};

		mainThread.start();
	}

	@Override
	public void stop() throws IOException {
		input.close();
		mainProcess.destroy();

		try {
			Thread.sleep(TIMEOUT_MS);
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted while waiting for process to terminate", e);
		}

		mainProcess.destroyForcibly();
	}

	@Override
	public boolean isRunning() {
		return mainThread != null && mainThread.isAlive();
	}

	@Override
	public String getName() {
		return "Python IPC client";
	}

	public void setCompiler(DynamicPythonCompiler compiler) {
		this.compiler = compiler;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonIPCClientService.class.getName());
	}
}
