package core.cli.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;

import core.cli.server.handlers.SharedVariablesGetActionHandler;
import core.cli.server.handlers.SharedVariablesSetActionHandler;
import core.cli.server.handlers.TaskAddActionHandler;
import core.cli.server.handlers.TaskExecuteActionHandler;
import core.cli.server.handlers.TaskListActionHandler;
import core.cli.server.handlers.TaskRemoveActionHandler;
import core.config.CliConfig;
import core.ipc.IPCServiceWithModifablePort;
import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.UpAndRunningHandler;
import frontEnd.MainBackEndHolder;

public class CliServer extends IPCServiceWithModifablePort {

	private static final int TERMINATION_DELAY_SECOND = 1;
	private static final int DEFAULT_PORT = CliConfig.DEFAULT_SERVER_PORT;

	private Map<String, HttpHandlerWithBackend> handlers;

	private MainBackEndHolder backEndHolder;
	private Thread mainThread;
	private HttpServer server;

	public CliServer() {
		setPort(DEFAULT_PORT);
	}

	public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
		this.backEndHolder = backEndHolder;
		if (handlers == null) {
			return;
		}

		for (HttpHandlerWithBackend handler : handlers.values()) {
			handler.setMainBackEndHolder(backEndHolder);
		}
	}

	private Map<String, HttpHandlerWithBackend> createHandlers() {
		Map<String, HttpHandlerWithBackend> output = new HashMap<>();
		output.put("/var/get", new SharedVariablesGetActionHandler());
		output.put("/var/set", new SharedVariablesSetActionHandler());

		output.put("/task/add", new TaskAddActionHandler());
		output.put("/task/remove", new TaskRemoveActionHandler());
		output.put("/task/execute", new TaskExecuteActionHandler());
		output.put("/task/list", new TaskListActionHandler());
		return output;
	}

	@Override
	protected void start() throws IOException {
		handlers = createHandlers();
		setMainBackEndHolder(backEndHolder);

		ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap()
				.setLocalAddress(InetAddress.getByName("localhost"))
                .setListenerPort(port)
                .setServerInfo("RepeatCli")
				.setExceptionLogger(ExceptionLogger.STD_ERR)
				.registerHandler("/test", new UpAndRunningHandler());
		for (Entry<String, HttpHandlerWithBackend> entry : handlers.entrySet()) {
			serverBootstrap.registerHandler(entry.getKey(), entry.getValue());
		}
		server = serverBootstrap.create();

		mainThread = new Thread() {
        	@Override
        	public void run() {
        		try {
					server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					getLogger().log(Level.SEVERE, "Interrupted when waiting for CLI server.", e);
				}
        		getLogger().info("Finished waiting for CLI server termination...");
        	}
        };
        server.start();
        mainThread.start();
        getLogger().info("CLI server up and running...");
	}

	@Override
	protected void stop() throws IOException {
		server.shutdown(TERMINATION_DELAY_SECOND, TimeUnit.SECONDS);
		try {
			mainThread.join();
			getLogger().info("CLI server terminated...");
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted when waiting for server to terminate.", e);
		}
		server = null;
		mainThread = null;
		handlers.clear();
	}

	@Override
	public boolean isRunning() {
		return mainThread != null && server != null;
	}

	@Override
	public String getName() {
		return "CLI server";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(CliServer.class.getName());
	}
}
