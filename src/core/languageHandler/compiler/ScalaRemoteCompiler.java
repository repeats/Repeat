package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;

public class ScalaRemoteCompiler extends AbstractRemoteNativeCompiler {

	{
		getLogger().setLevel(Level.ALL);
	}

	public ScalaRemoteCompiler(File objectFileDirectory) {
		super(objectFileDirectory);
	}

	@Override
	protected boolean checkRemoteCompilerSettings() {
		return true;
	}

	@Override
	public Language getName() {
		return Language.SCALA;
	}

	@Override
	public String getExtension() {
		return ".scala";
	}

	@Override
	public String getObjectExtension() {
		return ".class";
	}

	@Override
	public File getPath() {
		return new File(".");
	}

	@Override
	public boolean canSetPath() {
		return false;
	}

	@Override
	public boolean setPath(File path) {
		// Intentionally left blank
		return false;
	}

	@Override
	public boolean parseCompilerSpecificArgs(JsonNode node) {
		return true;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		return JsonNodeFactories.object();
	}

	@Override
	protected String getDummyPrefix() {
		return "SCALA_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass().getName());
	}
}
