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
import core.ipc.client.IPCClientManager;

public class DynamicCompilerManager implements IJsonable {

	public static final String JAVA_LANGUAGE = "java";
	public static final String PYTHON_LANGUAGE = "python";

	private final Map<String, AbstractNativeDynamicCompiler> compilers;

	public DynamicCompilerManager(IPCClientManager ipcClientFactory) {
		compilers = new HashMap<>();
		compilers.put(JAVA_LANGUAGE, new DynamicJavaCompiler("CustomAction", new String[]{"core"}, new String[]{}));
		compilers.put(PYTHON_LANGUAGE, new DynamicPythonCompiler(ipcClientFactory.getClient(PYTHON_LANGUAGE), new File("core")));
	}

	public AbstractNativeDynamicCompiler getCompiler(String name) {
		return compilers.get(name);
	}

	public void addCompiler(String name, AbstractNativeDynamicCompiler compiler) {
		compilers.put(name,  compiler);
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
					JsonNodeFactories.field("name", JsonNodeFactories.string(compiler.getName())),
					JsonNodeFactories.field("path", JsonNodeFactories.string(FileUtility.getRelativePwdPath(compiler.getPath()))),
					JsonNodeFactories.field("compiler_specific_args", compiler.getCompilerSpecificArgs())
					));
		}

		return JsonNodeFactories.array(compilerList);
	}
}
