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
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.languageHandler.Language;
import utilities.FileUtility;
import utilities.json.IJsonable;

public class DynamicCompilerManager implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(DynamicCompilerManager.class.getName());
	private final Map<Language, AbstractNativeCompiler> compilers;
	private RemoteRepeatsCompilerConfig remoteRepeatsCompilerConfig;

	public DynamicCompilerManager() {
		compilers = new HashMap<>();
		compilers.put(Language.JAVA, new JavaNativeCompiler("CustomAction", new String[]{"core"}, new String[]{}));
		compilers.put(Language.PYTHON, new PythonRemoteCompiler(new File("core")));
		compilers.put(Language.CSHARP, new CSharpRemoteCompiler(new File("core")));
		compilers.put(Language.MANUAL_BUILD, new ManualBuildNativeCompiler(new File("core")));
		compilers.put(Language.SCALA, new ScalaRemoteCompiler(new File("core")));

		remoteRepeatsCompilerConfig = new RemoteRepeatsCompilerConfig(new ArrayList<>());
	}

	public AbstractNativeCompiler getNativeCompiler(Language name) {
		return compilers.get(name);
	}

	public AbstractNativeCompiler getNativeCompiler(String name) {
		return getNativeCompiler(Language.identify(name));
	}

	public boolean hasCompiler(Language name) {
		return compilers.containsKey(name);
	}

	public RemoteRepeatsCompiler getRemoteRepeatsCompiler(RepeatsPeerServiceClientManager peerServiceClientManager) {
		return new RemoteRepeatsCompiler(remoteRepeatsCompilerConfig, peerServiceClientManager);
	}

	public RemoteRepeatsCompilerConfig getRemoteRepeatsCompilerConfig() {
		return remoteRepeatsCompilerConfig;
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

		return JsonNodeFactories.object(
				JsonNodeFactories.field("local_compilers", JsonNodeFactories.array(compilerList)),
				JsonNodeFactories.field("remote_repeats_compilers", remoteRepeatsCompilerConfig.jsonize()));
	}

	public boolean parseJSON(JsonNode compilerSettings) {
		JsonNode remoteRepeatsCompilers = compilerSettings.getNode("remote_repeats_compilers");
		RemoteRepeatsCompilerConfig remoteCompilers = RemoteRepeatsCompilerConfig.parseJSON(remoteRepeatsCompilers);
		remoteRepeatsCompilerConfig.setClients(remoteCompilers.getClients());

		List<JsonNode> localCompilers = compilerSettings.getArrayNode("local_compilers");
		for (JsonNode compilerNode : localCompilers) {
			String name = compilerNode.getStringValue("name");
			String path = compilerNode.getStringValue("path");
			JsonNode compilerSpecificArgs = compilerNode.getNode("compiler_specific_args");

			AbstractNativeCompiler compiler = getNativeCompiler(name);
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
