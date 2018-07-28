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
import core.keyChain.TaskActivationConstructorManager;
import core.webcommon.HttpHandlerWithBackend;
import core.webcommon.StaticFileServingHandler;
import core.webcommon.UpAndRunningHandler;
import core.webui.server.handlers.AboutPageHandler;
import core.webui.server.handlers.ApiPageHandler;
import core.webui.server.handlers.IndexPageHandler;
import core.webui.server.handlers.internals.GetLogsHandler;
import core.webui.server.handlers.internals.GetMousePositionHandler;
import core.webui.server.handlers.internals.GetRenderedTaskGroupsDropdown;
import core.webui.server.handlers.internals.ipcs.ActionRunIPCServiceHandler;
import core.webui.server.handlers.internals.ipcs.ActionStopIPCServiceHandler;
import core.webui.server.handlers.internals.ipcs.IPCPageHandler;
import core.webui.server.handlers.internals.ipcs.ModifyIPCServicePortHandler;
import core.webui.server.handlers.internals.ipcs.ToggleIPCServiceLaunchAtStartupHandler;
import core.webui.server.handlers.internals.menu.MenuCleanUnusedSourcesActionHandler;
import core.webui.server.handlers.internals.menu.MenuExecuteOnReleaseActionHandler;
import core.webui.server.handlers.internals.menu.MenuExitActionHandler;
import core.webui.server.handlers.internals.menu.MenuExportTaskActionHandler;
import core.webui.server.handlers.internals.menu.MenuForceExitActionHandler;
import core.webui.server.handlers.internals.menu.MenuGetCompilerConfigOptionActionHandler;
import core.webui.server.handlers.internals.menu.MenuGetCompilerPathActionHandler;
import core.webui.server.handlers.internals.menu.MenuGetCompilingLanguagesActionHandler;
import core.webui.server.handlers.internals.menu.MenuGetDebugLevelOptionsActionHandler;
import core.webui.server.handlers.internals.menu.MenuGetGeneratedSourceHandler;
import core.webui.server.handlers.internals.menu.MenuHaltAllTasksActionHandler;
import core.webui.server.handlers.internals.menu.MenuHaltTaskByEscapeActionHandler;
import core.webui.server.handlers.internals.menu.MenuImportTaskActionHandler;
import core.webui.server.handlers.internals.menu.MenuRecordMouseClickOnlyActionHandler;
import core.webui.server.handlers.internals.menu.MenuSaveConfigActionHandler;
import core.webui.server.handlers.internals.menu.MenuSetCompilerConfigActionHandler;
import core.webui.server.handlers.internals.menu.MenuSetCompilerPathActionHandler;
import core.webui.server.handlers.internals.menu.MenuSetCompilingLanguagesActionHandler;
import core.webui.server.handlers.internals.menu.MenuSetDebugLevelActionHandler;
import core.webui.server.handlers.internals.menu.MenuUseTrayIconActionHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionChangeReplayConfigHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStartRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStartReplayHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStopRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.ActionStopReplayHandler;
import core.webui.server.handlers.internals.recordsreplays.GetIsRecordingHandler;
import core.webui.server.handlers.internals.recordsreplays.GetIsReplayingHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationAddPhraseHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationAddStrokesAsKeyChainHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationAddStrokesAsKeySequenceHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationGetStrokesHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationRemoveKeyChainHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationRemoveKeySequenceHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationRemovePhraseHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationSetMouseGesturesHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationStartListeningHandler;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationStopListeningHandler;
import core.webui.server.handlers.internals.taskactivation.TaskActivationPageHandler;
import core.webui.server.handlers.internals.taskcreation.ActionCompileTaskHandler;
import core.webui.server.handlers.internals.taskcreation.ActionEditSourceHandler;
import core.webui.server.handlers.internals.taskcreation.ActionRunCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.ActionStopRunningCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.GetEdittedSourceHandler;
import core.webui.server.handlers.internals.taskcreation.GetIsRunningCompiledTaskHandler;
import core.webui.server.handlers.internals.taskcreation.GetSourceTemplateHandler;
import core.webui.server.handlers.internals.taskgroups.ActionAddTaskGroupHandler;
import core.webui.server.handlers.internals.taskgroups.ActionChangeTaskGroupNameHandler;
import core.webui.server.handlers.internals.taskgroups.ActionDeleteTaskGroupHandler;
import core.webui.server.handlers.internals.taskgroups.ActionMoveTaskGroupDownHandler;
import core.webui.server.handlers.internals.taskgroups.ActionMoveTaskGroupUpHandler;
import core.webui.server.handlers.internals.taskgroups.ActionSwitchTaskGroupHandler;
import core.webui.server.handlers.internals.taskgroups.TaskGroupsPageHandler;
import core.webui.server.handlers.internals.taskgroups.ToggleTaskGroupEnabledHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionAddTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionChangeTaskGroupForTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionDeleteTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionMoveTaskDownHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionMoveTaskUpHandler;
import core.webui.server.handlers.internals.taskmanagement.ActionOverwriteTaskHandler;
import core.webui.server.handlers.internals.taskmanagement.GetRenderedTaskGroupsSelectModalHandler;
import core.webui.server.handlers.internals.tasks.ActionSaveTaskActivationHandler;
import core.webui.server.handlers.internals.tasks.GetSourceForTaskHandler;
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
	private TaskActivationConstructorManager taskActivationConstructorManager;
	private final ObjectRenderer objectRenderer;
	private Thread mainThread;
	private HttpServer server;

	public UIServer() {
		setPort(DEFAULT_PORT);

		taskActivationConstructorManager = new TaskActivationConstructorManager();
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
		output.put("/task-activation", new TaskActivationPageHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/api", new ApiPageHandler());
		output.put("/about", new AboutPageHandler(objectRenderer));

		output.put("/internals/menu/file/save-config", new MenuSaveConfigActionHandler());
		output.put("/internals/menu/file/import-tasks", new MenuImportTaskActionHandler());
		output.put("/internals/menu/file/export-tasks", new MenuExportTaskActionHandler());
		output.put("/internals/menu/file/clean-unused-sources", new MenuCleanUnusedSourcesActionHandler());
		output.put("/internals/menu/file/force-exit", new MenuForceExitActionHandler());
		output.put("/internals/menu/file/exit", new MenuExitActionHandler());

		output.put("/internals/menu/tools/halt-all-tasks", new MenuHaltAllTasksActionHandler());
		output.put("/internals/menu/tools/generate-source", new MenuGetGeneratedSourceHandler());
		output.put("/internals/menu/tools/get-compiling-languages-options", new MenuGetCompilingLanguagesActionHandler(objectRenderer));
		output.put("/internals/menu/tools/set-compiling-language", new MenuSetCompilingLanguagesActionHandler(objectRenderer));

		output.put("/internals/menu/settings/get-compiler-path", new MenuGetCompilerPathActionHandler());
		output.put("/internals/menu/settings/set-compiler-path", new MenuSetCompilerPathActionHandler());
		output.put("/internals/menu/settings/compiler-config-options", new MenuGetCompilerConfigOptionActionHandler(objectRenderer));
		output.put("/internals/menu/settings/set-compiler-config", new MenuSetCompilerConfigActionHandler());
		output.put("/internals/menu/settings/record-mouse-click-only", new MenuRecordMouseClickOnlyActionHandler());
		output.put("/internals/menu/settings/halt-task-by-escape", new MenuHaltTaskByEscapeActionHandler());
		output.put("/internals/menu/settings/debug-level-options", new MenuGetDebugLevelOptionsActionHandler(objectRenderer));
		output.put("/internals/menu/settings/set-debug-level", new MenuSetDebugLevelActionHandler());
		output.put("/internals/menu/settings/execute-on-release", new MenuExecuteOnReleaseActionHandler());
		output.put("/internals/menu/settings/use-tray-icon", new MenuUseTrayIconActionHandler());

		output.put("/internals/action/task-activation/save", new ActionSaveTaskActivationHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/start-listening", new ActionTaskActivationStartListeningHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/stop-listening", new ActionTaskActivationStopListeningHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/key-chain/remove", new ActionTaskActivationRemoveKeyChainHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/key-sequence/remove", new ActionTaskActivationRemoveKeySequenceHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/mouse-gestures/set", new ActionTaskActivationSetMouseGesturesHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/phrase/add", new ActionTaskActivationAddPhraseHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/phrase/remove", new ActionTaskActivationRemovePhraseHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/strokes/add-as-key-chain", new ActionTaskActivationAddStrokesAsKeyChainHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/strokes/add-as-key-sequence", new ActionTaskActivationAddStrokesAsKeySequenceHandler(objectRenderer, taskActivationConstructorManager));
		output.put("/internals/action/task-activation/strokes/get", new ActionTaskActivationGetStrokesHandler(objectRenderer, taskActivationConstructorManager));

		output.put("/internals/action/add-task", new ActionAddTaskHandler(objectRenderer));
		output.put("/internals/action/add-task-group", new ActionAddTaskGroupHandler(objectRenderer));
		output.put("/internals/action/change-task-group-name", new ActionChangeTaskGroupNameHandler(objectRenderer));
		output.put("/internals/action/change-task-group-for-task", new ActionChangeTaskGroupForTaskHandler(objectRenderer));
		output.put("/internals/action/change-replay-config", new ActionChangeReplayConfigHandler());
		output.put("/internals/action/compile-task", new ActionCompileTaskHandler());
		output.put("/internals/action/delete-task", new ActionDeleteTaskHandler(objectRenderer));
		output.put("/internals/action/delete-task-group", new ActionDeleteTaskGroupHandler(objectRenderer));
		output.put("/internals/action/edit-source", new ActionEditSourceHandler());
		output.put("/internals/action/move-task-up", new ActionMoveTaskUpHandler(objectRenderer));
		output.put("/internals/action/move-task-group-up", new ActionMoveTaskGroupUpHandler(objectRenderer));
		output.put("/internals/action/move-task-down", new ActionMoveTaskDownHandler(objectRenderer));
		output.put("/internals/action/move-task-group-down", new ActionMoveTaskGroupDownHandler(objectRenderer));
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
		output.put("/internals/get/rendered-task-groups-dropdown", new GetRenderedTaskGroupsDropdown(objectRenderer));
		output.put("/internals/get/rendered-task-groups-select-modal", new GetRenderedTaskGroupsSelectModalHandler(objectRenderer));

		output.put("/internals/modify/ipc-service-port", new ModifyIPCServicePortHandler(objectRenderer));
		output.put("/internals/modify/task-name", new ModifyTaskNameHandler(objectRenderer));

		output.put("/internals/toggle/ipc-service-launch-at-startup", new ToggleIPCServiceLaunchAtStartupHandler(objectRenderer));
		output.put("/internals/toggle/task-group-enabled", new ToggleTaskGroupEnabledHandler(objectRenderer));
		output.put("/internals/toggle/task-enabled", new ToggleTaskEnabledHandler(objectRenderer));

		return output;
	}

	@Override
	protected void start() throws IOException {
		handlers = createHandlers();
		taskActivationConstructorManager.start();
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
		taskActivationConstructorManager.stop();
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
