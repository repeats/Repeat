package core.webui.server.handlers.internals.tasks;

import java.util.HashMap;
import java.util.Map;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.manualBuild.ManuallyBuildAction;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionBuilderBody;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class TaskSourceCodeFragmentHandler {

	private ObjectRenderer objectRenderer;
	private ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public TaskSourceCodeFragmentHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		this.objectRenderer = objectRenderer;
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	public JsonNode render(Language language, String source, UserDefinedAction action) throws RenderException {
		if (language == Language.MANUAL_BUILD) {
			if (!(action instanceof ManuallyBuildAction)) {
				throw new RenderException("Action is not a manually build action but is " + action.getClass() + ".");
			}

			String id = manuallyBuildActionConstructorManager.addNew((ManuallyBuildAction) action);
			Map<String, Object> data = new HashMap<>();
			data.put("displayManualBuild", true);
			Map<String, Object> manuallyBuildActionBuilderBodyData = ManuallyBuildActionBuilderBody.bodyData(manuallyBuildActionConstructorManager, id);
			data.putAll(manuallyBuildActionBuilderBodyData);

			String page = objectRenderer.render("fragments/source_code", data);
			if (page == null) {
				throw new RenderException("Failed to render page with manual build.");
			}

			return JsonNodeFactories.object(
					JsonNodeFactories.field("taskType", JsonNodeFactories.string("manually_build")),
					JsonNodeFactories.field("page", JsonNodeFactories.string(page)));
		}

		Map<String, Object> data = new HashMap<>();
		data.put("displayManualBuild", false);
		String page = objectRenderer.render("fragments/source_code", data);
		if (page == null) {
			throw new RenderException("Failed to render page.");
		}
		return JsonNodeFactories.object(
					JsonNodeFactories.field("taskType", JsonNodeFactories.string("source")),
					JsonNodeFactories.field("page", JsonNodeFactories.string(page)),
					JsonNodeFactories.field("source", JsonNodeFactories.string(source)));
	}

	@SuppressWarnings("serial")
	public static class RenderException extends Exception {
		public RenderException(String message){
	        super(message);
	    }
	}
}
