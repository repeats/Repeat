package cli.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import cli.server.handlers.HttpHandlerWithBackend;
import cli.server.handlers.TaskAddActionHandler;
import cli.server.handlers.TaskExecuteActionHandler;
import cli.server.handlers.TaskRemoveActionHandler;
import cli.server.handlers.UpAndRunningHandler;
import core.config.CliConfig;
import core.ipc.IPCServiceWithModifablePort;
import frontEnd.MainBackEndHolder;

public class CliServer extends IPCServiceWithModifablePort {

	private static final int SYSTEM_DEFAULT_CONNECTION_BACKLOG = 0;
	private static final int TERMINATION_DELAY_SECOND = 1;
	private static final int DEFAULT_PORT = CliConfig.DEFAULT_SERVER_PORT;

	private Map<String, HttpHandlerWithBackend> handlers;

	private Thread mainThread;
	private HttpServer server;

	public CliServer() {
		setPort(DEFAULT_PORT);
	}

	public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
		for (HttpHandlerWithBackend handler : handlers.values()) {
			handler.setMainBackEndHolder(backEndHolder);
		}
	}

	private Map<String, HttpHandlerWithBackend> createHandlers() {
		Map<String, HttpHandlerWithBackend> output = new HashMap<>();
		output.put("/task/add", new TaskAddActionHandler());
		output.put("/task/remove", new TaskRemoveActionHandler());
		output.put("/task/execute", new TaskExecuteActionHandler());
		return output;
	}

	@Override
	protected void start() throws IOException {
		handlers = createHandlers();

		HttpServer server = HttpServer.create(new InetSocketAddress(port), SYSTEM_DEFAULT_CONNECTION_BACKLOG);
		server.createContext("/test", new UpAndRunningHandler());
		for (Entry<String, HttpHandlerWithBackend> entry : handlers.entrySet()) {
			server.createContext(entry.getKey(), entry.getValue());
		}
        server.setExecutor(null); // Creates a default executor.
        this.server = server;
        mainThread = new Thread() {
        	@Override
        	public void run() {
        		server.start();
        	}
        };
        mainThread.start();
        getLogger().info("CLI server up and running...");
	}

	@Override
	protected void stop() throws IOException {
		server.stop(TERMINATION_DELAY_SECOND);
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
