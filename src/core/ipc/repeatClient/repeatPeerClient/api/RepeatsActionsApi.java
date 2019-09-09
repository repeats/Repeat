package core.ipc.repeatClient.repeatPeerClient.api;

import java.util.Base64;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatServer.ClientTask;
import core.ipc.repeatServer.processors.IpcMessageType;
import core.ipc.repeatServer.processors.TaskProcessor;
import core.keyChain.TaskActivation;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import utilities.json.IJsonable;
import utilities.json.ImmediateJsonable;

public class RepeatsActionsApi extends AbstractRepeatsClientApi {

	private static final Logger LOGGER = Logger.getLogger(RepeatsActionsApi.class.getName());

	protected RepeatsActionsApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		super(repeatPeerServiceClientWriter);
	}

	public UserDefinedAction createTask(String source, Language language, RepeatsRemoteCompilationHints hints) {
		String encodedSource = Base64.getEncoder().encodeToString(source.getBytes(TaskProcessor.SOURCE_ENCODING));
		IJsonable message = message(TaskProcessor.CREATE_TASK_ACTION, JsonNodeFactories.object(
				JsonNodeFactories.field("language", JsonNodeFactories.string(language.toString())),
				JsonNodeFactories.field("source", JsonNodeFactories.string(encodedSource)),
				JsonNodeFactories.field("previously_compiled_id", JsonNodeFactories.string(hints.previouslyCompiledActionId))
				));

		JsonNode response = waitAndGetJsonResponseIfSuccess(IpcMessageType.TASK, message);
		if (response == null) {
			LOGGER.info("Unable to create remote Repeats task.");
			return null;
		}
		ClientTask createdTask = ClientTask.parseJSON(response);
		UserDefinedAction action = new RepeatsRemoteUserDefinedAction(createdTask.getId(), createdTask.getFileName(), language);
		return action;
	}

	public void runTask(String id) {
		IJsonable message = message(TaskProcessor.RUN_TASK_ACTION, JsonNodeFactories.array(JsonNodeFactories.string(id)));
		waitAndGetResponseIfSuccess(IpcMessageType.TASK, message);
	}

	public void runTask(String id, TaskActivation activation) {
		IJsonable message = message(TaskProcessor.RUN_TASK_ACTION, JsonNodeFactories.array(
				JsonNodeFactories.string(id),
				activation.jsonize()
				));
		waitAndGetResponseIfSuccess(IpcMessageType.TASK, message);
	}

	public void removeTask(String id) {
		IJsonable message = message(TaskProcessor.REMOVE_TASK_ACTION, JsonNodeFactories.array(JsonNodeFactories.string(id)));
		waitAndGetResponseIfSuccess(IpcMessageType.TASK, message);
	}

	private IJsonable message(String action, JsonNode node) {
		return ImmediateJsonable.of(JsonNodeFactories.object(
				JsonNodeFactories.field("task_action", JsonNodeFactories.string(action)),
				JsonNodeFactories.field("parameters", node)));
	}

	public static class RepeatsRemoteCompilationHints {

		private String previouslyCompiledActionId;

		public static RepeatsRemoteCompilationHints of(String previouslyCompiledActionId) {
			return new RepeatsRemoteCompilationHints(previouslyCompiledActionId);
		}

		private RepeatsRemoteCompilationHints(String previouslyCompiledActionId) {
			this.previouslyCompiledActionId = previouslyCompiledActionId == null ? "" : previouslyCompiledActionId;
		}
	}

	private class RepeatsRemoteUserDefinedAction extends UserDefinedAction {
		protected RepeatsRemoteUserDefinedAction(String id, String sourcePath, Language language) {
			super(id);
			this.sourcePath = sourcePath;
			this.compiler = language;
		}

		@Override
		public void action(Core controller) throws InterruptedException {
			runTask(getActionId(), invoker);
		}
	}
}
