package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.HashMap;
import java.util.Map;

import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.server.handlers.renderedobjects.RenderedPossibleManuallyBuildActions;

public class ManuallyBuildActionBuilderBody {

	public static Map<String, Object> bodyData(ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager, String constructorId) {
		Map<String, Object> data = new HashMap<>();
		ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(constructorId);

		data.put("constructorId", constructorId);
		data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
		data.put("possibleActions", RenderedPossibleManuallyBuildActions.of(ManuallyBuildActionFeModel.of().noAction()));
		return data;
	}

	private ManuallyBuildActionBuilderBody() {}
}
