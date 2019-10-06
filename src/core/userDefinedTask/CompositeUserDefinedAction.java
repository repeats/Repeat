package core.userDefinedTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.controller.Core;
import core.keyChain.TaskActivation;
import core.languageHandler.compiler.DynamicCompilerManager;
import core.languageHandler.compiler.DynamicCompilerOutput;
import core.languageHandler.compiler.RemoteRepeatsCompiler;
import core.languageHandler.compiler.RemoteRepeatsCompilerConfig;
import core.languageHandler.compiler.RemoteRepeatsDyanmicCompilationResult;
import utilities.json.JSONUtility;

public class CompositeUserDefinedAction extends UserDefinedAction {

	private static final Logger LOGGER = Logger.getLogger(CompositeUserDefinedAction.class.getName());

	private final UserDefinedAction localAction;
	private final RemoteRepeatsCompilerConfig clients;
	private final Map<String, String> clientIdToActionId;
	private UserDefinedAction remoteRepeatsAction;

	private CompositeUserDefinedAction(UserDefinedAction localAction, RemoteRepeatsCompilerConfig clients, Map<String, String> clientIdToActionId, UserDefinedAction remoteRepeatsAction) {
		syncContent(localAction);
		this.localAction = localAction;
		this.clients = clients.clone();
		this.clientIdToActionId = clientIdToActionId;
		this.remoteRepeatsAction = remoteRepeatsAction;
	}

	public static CompositeUserDefinedAction of(UserDefinedAction localAction, RemoteRepeatsCompilerConfig clients, Map<String, String> clientIdToActionId, UserDefinedAction remoteRepeatsAction) {
		return new CompositeUserDefinedAction(localAction, clients, clientIdToActionId, remoteRepeatsAction);
	}

	@Override
	public final void action(Core controller) throws InterruptedException {
		List<UserDefinedAction> actions = new ArrayList<>(2);
		if (clients.hasLocal()) {
			actions.add(localAction);
		}
		actions.add(remoteRepeatsAction);


		List<Thread> executions = new ArrayList<>(actions.size());
		for (final UserDefinedAction action : actions) {
			executions.add(new Thread() {
				@Override
				public void run() {
					try {
						action.action(controller);
					} catch (InterruptedException ie) {
						LOGGER.log(Level.WARNING, "Interrupted when executing action.", ie);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception when executing action.", e);
					}
				}
			});
		}

		for (Thread t : executions) {
			t.start();
		}
		for (Thread t : executions) {
			t.join();
		}
	}

	@Override
	public UserDefinedAction recompileRemote(RemoteRepeatsCompiler compiler) {
		UserDefinedAction remote = remoteRepeatsAction.recompileRemote(compiler);
		if (remote == null) {
			return null;
		}
		remoteRepeatsAction = remote;
		return this;
	}

	@Override
	public final void setTaskInvoker(TaskInvoker taskInvoker) {
		super.setTaskInvoker(taskInvoker);
		localAction.setTaskInvoker(taskInvoker);
		remoteRepeatsAction.setTaskInvoker(taskInvoker);
	}

	@Override
	public final void setInvoker(TaskActivation invoker) {
		super.setInvoker(invoker);
		localAction.setInvoker(invoker);
		remoteRepeatsAction.setInvoker(invoker);
	}

	@Override
	public JsonRootNode jsonize() {
		JsonRootNode node = super.jsonize();
		JsonNode actionIds = JsonNodeFactories.object();
		if (clientIdToActionId != null) {
			actionIds = JSONUtility.stringMapToJson(clientIdToActionId);
		}

		return JSONUtility.addChild(node, "composite_action",
				JsonNodeFactories.object(
						JsonNodeFactories.field("compilation_info", actionIds),
						JsonNodeFactories.field("compiler_config", clients.jsonize()))).getRootNode();
	}

	public static CompositeUserDefinedAction parseJSON(DynamicCompilerManager factory, JsonNode node) {
		if (!node.isNode("composite_action")) {
			LOGGER.warning("Missing required 'composite_action' node when parsing composite user defined action.");
			return null;
		}
		JsonNode compositeActionData = node.getNode("composite_action");

		RemoteRepeatsCompilerConfig clients = RemoteRepeatsCompilerConfig.parseJSON(compositeActionData.getNode("compiler_config"));
		if (clients == null) {
			LOGGER.warning("Failed to parse remote clients config from JSON.");
			return null;
		}

		final UserDefinedAction local = UserDefinedAction.parsePureJSON(factory, node);
		if (local == null) {
			LOGGER.warning("Failed to parse local action from JSON.");
			return null;
		}

		JsonNode compilationInfo = compositeActionData.getNode("compilation_info");
		Map<String, String> clientIdToActionId = JSONUtility.jsonToStringMap(compilationInfo);
		UserDefinedAction remote = new UserDefinedAction() {
			@Override
			public UserDefinedAction recompileRemote(RemoteRepeatsCompiler compiler) {
				compiler = compiler.cloneWithConfig(clients);
				compiler.setRemoteCompilationInfo(clientIdToActionId);

				RemoteRepeatsDyanmicCompilationResult result = compiler.compile(local.getSource(), local.getCompiler());
				DynamicCompilerOutput compilerStatus = result.output();
				UserDefinedAction output = result.action();
				output.actionId = getActionId();

				if (compilerStatus != DynamicCompilerOutput.COMPILATION_SUCCESS) {
					getLogger().warning("Unable to recompile Repeats remote task " + getName() + ". Error " + compilerStatus);
					return null;
				}
				getLogger().info("Successfully recompiled Repeats remote task " + getName() + ".");
				output.syncContent(local);
				output.compiler = getCompiler();

				if (result.clientIdToActionId() != null) {
					clientIdToActionId.clear();
					clientIdToActionId.putAll(result.clientIdToActionId());
				}
				return output;
			}

			@Override
			public void action(Core controller) throws InterruptedException {
				LOGGER.warning("Remote action not yet compiled.");
			}
		};

		return of(local, clients, clientIdToActionId, remote);
	}
}
