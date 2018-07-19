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
import core.webui.server.handlers.internals.ActionSwitchTaskGroupHandler;
import core.webui.server.handlers.internals.GetLogsHandler;
import core.webui.server.handlers.internals.GetMousePositionHandler;
import core.webui.server.handlers.internals.GetRenderedTaskGroupsDropdown;
import core.webui.server.handlers.internals.ipcs.ActionRunIPCServiceHandler;
import core.webui.server.handlers.internals.ipcs.ActionStopIPCServiceHandler;
import core.webui.server.handlers.internals.ipcs.GetRenderedIPCServicesHandler;
import core.webui.server.handlers.internals.ipcs.ModifyIPCServicePortHandler;
import core.webui.server.handlers.internals.ipcs.ToggleIPCServiceLaunchAtStartupHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStartRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStartReplayHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStopRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStopReplayHandler;
import core.webui.server.handlers.internals.recordsreplays.GetIsRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.GetIsReplayingHandler;
import core.webui.server.handlers.internals.taskcreation.ActionCompileTaskHandler;
import core.webui.server.handlers.internals.taskcreation.ActionEditSourceHandler;
import core.webui.server.handlers.internals.taskcreation.ActionRunCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.ActionStopRunningCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.GetEdittedSourceHandler;
import core.webui.server.handlers.internals.taskcreation.GetIsRunningCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.GetSourceTemplateHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionAddTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionChangeTaskGroupForTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionDeleteTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionMoveTaskDownHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionMoveTaskUpHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionOverwriteTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.GetRenderedTaskGroupsSelectModalHandler;
import core.webui.server.handlers.internals.taskmanagement.GetRenderedTasks;
import core.webui.server.handlers.internals.tasks.GetSourceForTaskHandler;
import core.webui.server.handlers.internals.tasks.ModifyTaskActivationHandler;
import core.webui.server.handlers.internals.tasks.ModifyTaskNameHandler;
import core.webui.server.handlers.internals.tasks.ToggleTaskEnabledHandler;
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

		output.put("/internals/action/add-task", new ActionAddTaskHandler(objectRenderer));
		output.put("/internals/action/change-task-group-for-task", new ActionChangeTaskGroupForTaskHandler(objectRenderer));
		output.put("/internals/action/compile-task", new ActionCompileTaskHandler());
		output.put("/internals/action/delete-task", new ActionDeleteTaskHandler(objectRenderer));
		output.put("/internals/action/edit-source", new ActionEditSourceHandler());
		output.put("/internals/action/move-task-up", new ActionMoveTaskUpHandler(objectRenderer));
		output.put("/internals/action/move-task-down", new ActionMoveTaskDownHandler(objectRenderer));
		output.put("/internals/action/overwrite-task", new ActionOverwriteTaskHandler(objectRenderer));
		output.put("/internals/action/run-compiled-task", new ActionRunCompiledTaskHandler());
		output.put("/internals/action/run-ipc-service", new ActionRunIPCServiceHandler(objectRenderer));
		output.put("/internals/action/start-record", new ActionStartRecordingHandler());
		output.put("/internals/action/start-replay", new ActionStartReplayHandler());
		output.put("/internals/action/stop-record", new ActionStopRecordingHandler());
		output.put("/internals/action/stop-replay", new ActionStopReplayHandler());
		output.put("/internals/action/stop-ipc-service", new ActionStopIPCServiceHandler(objectRenderer));
		output.put("/internals/action/stop-running-compiled-task", new ActionStopRunningCompiledTaskHandler());
		output.put("/internals/action/switch-task-group", new ActionSwitchTaskGroupHandler(objectRenderer));

		output.put("/internals/get/editted-source", new GetEdittedSourceHandler());
		output.put("/internals/get/is-running-compiled-task", new GetIsRunningCompiledTaskHandler());
		output.put("/internals/get/is-recording", new GetIsRecordingHandler());
		output.put("/internals/get/is-replaying", new GetIsReplayingHandler());
		output.put("/internals/get/mouse-position", new GetMousePositionHandler());
		output.put("/internals/get/logs", new GetLogsHandler());
		output.put("/internals/get/source-for-task", new GetSourceForTaskHandler());
		output.put("/internals/get/source-templates", new GetSourceTemplateHandler());
		output.put("/internals/get/rendered-ipcs", new GetRenderedIPCServicesHandler(objectRenderer));
		output.put("/internals/get/rendered-tasks", new GetRenderedTasks(objectRenderer));
		output.put("/internals/get/rendered-task-groups-dropdown", new GetRenderedTaskGroupsDropdown(objectRenderer));
		output.put("/internals/get/rendered-task-groups-select-modal", new GetRenderedTaskGroupsSelectModalHandler(objectRenderer));

		output.put("/internals/modify/ipc-service-port", new ModifyIPCServicePortHandler(objectRenderer));
		output.put("/internals/modify/task-activation", new ModifyTaskActivationHandler());
		output.put("/internals/modify/task-name", new ModifyTaskNameHandler(objectRenderer));

		output.put("/internals/toggle/ipc-service-launch-at-startup", new ToggleIPCServiceLaunchAtStartupHandler(objectRenderer));
		output.put("/internals/toggle/task-enabled", new ToggleTaskEnabledHandler(objectRenderer));

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
