package core.languageHandler.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.languageHandler.Language;
import utilities.FileUtility;
import utilities.IJsonable;

public class DynamicCompilerManager implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(DynamicCompilerManager.class.getName());
	private final Map<Language, AbstractNativeCompiler> compilers;

	public DynamicCompilerManager() {
		compilers = new HashMap<>();
		compilers.put(Language.JAVA, new JavaNativeCompiler("CustomAction", new String[]{"core"}, new String[]{}));
		compilers.put(Language.PYTHON, new PythonRemoteCompiler(new File("core")));
		compilers.put(Language.CSHARP, new CSharpRemoteCompiler(new File("core")));
		compilers.put(Language.SCALA, new ScalaRemoteCompiler(new File("core")));
	}

	public AbstractNativeCompiler getCompiler(Language name) {
		return compilers.get(name);
	}

	public AbstractNativeCompiler getCompiler(String name) {
		return getCompiler(Language.identify(name));
	}

	public boolean hasCompiler(Language name) {
		return compilers.containsKey(name);
	}

	public AbstractNativeCompiler removeCompiler(Language name) {
		return compilers.remove(name);
	}

	@Override
	public JsonRootNode jsonize() {
		List<JsonNode> compilerList = new ArrayList<>();
		for (AbstractNativeCompiler compiler :  compilers.values()) {
			compilerList.add(JsonNodeFactories.object(
					JsonNodeFactories.field("name", JsonNodeFactories.string(compiler.getName().toString())),
					JsonNodeFactories.field("path", JsonNodeFactories.string(FileUtility.getRelativePwdPath(compiler.getPath()))),
					JsonNodeFactories.field("compiler_specific_args", compiler.getCompilerSpecificArgs())
					));
		}

		return JsonNodeFactories.array(compilerList);
	}

	public boolean parseJSON(List<JsonNode> compilerSettings) {
		for (JsonNode compilerNode : compilerSettings) {
			String name = compilerNode.getStringValue("name");
			String path = compilerNode.getStringValue("path");
			JsonNode compilerSpecificArgs = compilerNode.getNode("compiler_specific_args");

			AbstractNativeCompiler compiler = getCompiler(name);
			if (compiler != null) {
				compiler.setPath(new File(path));
				if (!compiler.parseCompilerSpecificArgs(compilerSpecificArgs)) {
					LOGGER.log(Level.WARNING, "Compiler " + name + " was unable to parse its specific arguments.");
				}
			} else {
				throw new IllegalStateException("Unknown compiler " + name);
			}
		}
		return true;
	}
}
