package core.webui.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;

import core.config.WebUIConfig;
import core.ipc.IPCServiceWithModifablePort;
import core.webcommon.HttpHandlerWithBackend;
import core.webcommon.StaticFileServingHandler;
import core.webcommon.UpAndRunningHandler;
import core.webui.server.handlers.IPCPageHandler;
import core.webui.server.handlers.IndexPageHandler;
import core.webui.server.handlers.TaskActivationPageHandler;
import core.webui.server.handlers.TaskGroupsPageHandler;
import core.webui.server.handlers.internals.GetLogsHandler;
import core.webui.server.handlers.internals.GetSourceTemplateHandler;
import core.webui.server.handlers.internals.ModifyTaskNameHandler;
import core.webui.server.handlers.internals.ToggleTaskEnabledHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import frontEnd.MainBackEndHolder;
import staticResources.BootStrapResources;

public class UIServer extends IPCServiceWithModifablePort {
	private static final int TERMINATION_DELAY_SECOND = 1;
	private static final int DEFAULT_PORT = WebUIConfig.DEFAULT_SERVER_PORT;

	private Map<String, HttpHandlerWithBackend> handlers;

	private MainBackEndHolder backEndHolder;
	private final ObjectRenderer objectRenderer;
	private Thread mainThread;
	private HttpServer server;

	public UIServer() {
		setPort(DEFAULT_PORT);
		objectRenderer = new ObjectRenderer(BootStrapResources.getWebUIResource().getTemplateDir());
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
		output.put("/", new IndexPageHandler(objectRenderer));
		output.put("/ipcs", new IPCPageHandler(objectRenderer));
		output.put("/task-groups", new TaskGroupsPageHandler(objectRenderer));
		output.put("/task-activation", new TaskActivationPageHandler(objectRenderer));

		output.put("/internals/get/logs", new GetLogsHandler());
		output.put("/internals/get/source-templates", new GetSourceTemplateHandler());
		output.put("/internals/modify/task-name", new ModifyTaskNameHandler());
		output.put("/internals/modify/task-activation", new ModifyTaskNameHandler());
		output.put("/internals/toggle/task-enabled", new ToggleTaskEnabledHandler());
		return output;
	}

	@Override
	protected void start() throws IOException {
		handlers = createHandlers();
		setMainBackEndHolder(backEndHolder);

		ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Repeat")
				.setExceptionLogger(ExceptionLogger.STD_ERR)
				.registerHandler("/test", new UpAndRunningHandler())
				.registerHandler("/static/*", new StaticFileServingHandler(BootStrapResources.getWebUIResource().getStaticDir().getAbsolutePath()));
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
					getLogger().log(Level.SEVERE, "Interrupted when waiting for UI server.", e);
				}
        		getLogger().info("Finished waiting for UI server termination...");
        	}
        };
        server.start();
        mainThread.start();
        getLogger().info("UI server up and running...");
	}

	@Override
	protected void stop() throws IOException {
		server.shutdown(TERMINATION_DELAY_SECOND, TimeUnit.SECONDS);
		try {
			mainThread.join();
			getLogger().info("UI server terminated...");
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
		return "UI server";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(UIServer.class.getName());
	}
}
