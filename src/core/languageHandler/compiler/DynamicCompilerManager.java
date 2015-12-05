package core.languageHandler.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.FileUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;
import core.languageHandler.Language;

public class DynamicCompilerManager implements IJsonable {

	private final Map<Language, AbstractNativeDynamicCompiler> compilers;

	public DynamicCompilerManager() {
		compilers = new HashMap<>();
		compilers.put(Language.JAVA, new DynamicJavaCompiler("CustomAction", new String[]{"core"}, new String[]{}));
		compilers.put(Language.PYTHON, new DynamicPythonCompiler(new File("core")));
	}

	public AbstractNativeDynamicCompiler getCompiler(Language name) {
		return compilers.get(name);
	}

	public AbstractNativeDynamicCompiler getCompiler(String name) {
		return getCompiler(Language.identify(name));
	}

	public boolean hasCompiler(String name) {
		return compilers.containsKey(name);
	}

	public AbstractNativeDynamicCompiler removeCompiler(String name) {
		return compilers.remove(name);
	}

	@Override
	public JsonRootNode jsonize() {
		List<JsonNode> compilerList = new ArrayList<>();
		for (AbstractNativeDynamicCompiler compiler :  compilers.values()) {
			compilerList.add(JsonNodeFactories.object(
					JsonNodeFactories.field("name", JsonNodeFactories.string(compiler.getName().toString())),
					JsonNodeFactories.field("path", JsonNodeFactories.string(FileUtility.getRelativePwdPath(compiler.getPath()))),
					JsonNodeFactories.field("compiler_specific_args", compiler.getCompilerSpecificArgs())
					));
		}

		return JsonNodeFactories.array(compilerList);
	}
}
