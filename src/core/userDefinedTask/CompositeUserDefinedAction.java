package core.userDefinedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import utilities.Pair;
import utilities.json.JSONUtility;

public class CompositeUserDefinedAction extends UserDefinedAction {

	private static final Logger LOGGER = Logger.getLogger(CompositeUserDefinedAction.class.getName());

	private UserDefinedAction localAction;
	private RemoteRepeatsCompilerConfig clients;
	private UserDefinedAction remoteRepeatsAction;

	private CompositeUserDefinedAction(UserDefinedAction localAction, RemoteRepeatsCompilerConfig clients, UserDefinedAction remoteRepeatsAction) {
		syncContent(localAction);
		this.localAction = localAction;
		this.clients = clients.clone();
		this.remoteRepeatsAction = remoteRepeatsAction;
	}

	public static CompositeUserDefinedAction of(UserDefinedAction localAction, RemoteRepeatsCompilerConfig clients, UserDefinedAction remoteRepeatsAction) {
		return new CompositeUserDefinedAction(localAction, clients, remoteRepeatsAction);
	}

	@Override
	public final void action(Core controller) throws InterruptedException {
		List<UserDefinedAction> actions = Arrays.asList(localAction, remoteRepeatsAction);
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
		return JSONUtility.addChild(node, "composite_action",
				JsonNodeFactories.object(JsonNodeFactories.field("compiler_config", clients.jsonize()))).getRootNode();
	}

	public static CompositeUserDefinedAction parseJSON(DynamicCompilerManager factory, JsonNode node) {
		if (!node.isNode("composite_action")) {
			LOGGER.warning("Missing required 'composite_action' node when parsing composite user defined action.");
			return null;
		}

		RemoteRepeatsCompilerConfig clients = RemoteRepeatsCompilerConfig.parseJSON(node.getNode("composite_action").getNode("compiler_config"));
		if (clients == null) {
			LOGGER.warning("Failed to parse remote clients config from JSON.");
			return null;
		}

		final UserDefinedAction local = UserDefinedAction.parsePureJSON(factory, node);
		if (local == null) {
			LOGGER.warning("Failed to parse local action from JSON.");
			return null;
		}

		UserDefinedAction remote = new UserDefinedAction() {
			@Override
			public UserDefinedAction recompileRemote(RemoteRepeatsCompiler compiler) {
				compiler = compiler.cloneWithConfig(clients);

				Pair<DynamicCompilerOutput, UserDefinedAction> result = compiler.compile(local.getSource(), local.getCompiler());
				DynamicCompilerOutput compilerStatus = result.getA();
				UserDefinedAction output = result.getB();
				output.actionId = getActionId();

				if (compilerStatus != DynamicCompilerOutput.COMPILATION_SUCCESS) {
					getLogger().warning("Unable to recompile Repeats remote task " + getName() + ". Error " + compilerStatus);
					return null;
				}
				getLogger().info("Successfully recompiled Repeats remote task " + getName() + ".");
				output.syncContent(local);
				output.compiler = getCompiler();
				return output;
			}

			@Override
			public void action(Core controller) throws InterruptedException {
				LOGGER.warning("Remote action not yet compiled.");
			}
		};

		return of(local, clients, remote);
	}
}
