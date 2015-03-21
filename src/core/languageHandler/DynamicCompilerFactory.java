package core.languageHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;

public class DynamicCompilerFactory implements IJsonable {

	private final Map<String, DynamicCompiler> compilers;

	public DynamicCompilerFactory() {
		compilers = new HashMap<>();
		compilers.put("java", new DynamicJavaCompiler("CustomAction", new String[]{"core"}, new String[]{}));
		compilers.put("python", new DynamicPythonCompiler());
	}

	public DynamicCompiler getCompiler(String name) {
		return compilers.get(name);
	}

	public void addCompiler(String name, DynamicCompiler compiler) {
		compilers.put(name,  compiler);
	}

	public boolean hasCompiler(String name) {
		return compilers.containsKey(name);
	}

	public DynamicCompiler removeCompiler(String name) {
		return compilers.remove(name);
	}

	@Override
	public JsonRootNode jsonize() {
		List<JsonNode> compilerList = new ArrayList<>();
		for (DynamicCompiler compiler :  compilers.values()) {
			compilerList.add(JsonNodeFactories.object(
					JsonNodeFactories.field("name", JsonNodeFactories.string(compiler.getName())),
					JsonNodeFactories.field("path", JsonNodeFactories.string(compiler.getPath().getAbsolutePath())),
					JsonNodeFactories.field("run_args", JsonNodeFactories.string(compiler.getRunArgs()))
					));
		}

		return JsonNodeFactories.array(compilerList);
	}
}
