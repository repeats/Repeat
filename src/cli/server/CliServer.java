package cli.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import core.ipc.IPCServiceWithModifablePort;
import frontEnd.MainBackEndHolder;

public class CliServer extends IPCServiceWithModifablePort {

	protected static final Charset ENCODING = StandardCharsets.UTF_8;

	private static final int SYSTEM_DEFAULT_CONNECTION_BACKLOG = 0;
	private static final int TERMINATION_DELAY_SECOND = 1;
	private static final int DEFAULT_PORT = 65432;

	private List<HttpHandlerWithBackend> handlers;

	private Thread mainThread;
	private HttpServer server;

	public CliServer() {
		setPort(DEFAULT_PORT);
	}

	public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
		for (HttpHandlerWithBackend handler : handlers) {
			handler.setMainBackEndHolder(backEndHolder);
		}
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(CliServer.class.getName());
	}

	@Override
	protected void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), SYSTEM_DEFAULT_CONNECTION_BACKLOG);
		TaskActionHandler taskActionHandler = new TaskActionHandler();
		handlers.add(taskActionHandler);

        server.createContext("/task", taskActionHandler);
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
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted when waiting for server to terminate.", e);
		}
		server = null;
		mainThread = null;
	}

	@Override
	public boolean isRunning() {
		return mainThread != null && server != null;
	}

	@Override
	public String getName() {
		return "CLI server";
	}
}
