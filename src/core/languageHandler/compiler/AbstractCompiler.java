package core.languageHandler.compiler;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import utilities.ILoggable;

public abstract class AbstractCompiler implements ILoggable {
	public abstract DynamicCompilationResult compile(String source, Language language);

	public abstract boolean parseCompilerSpecificArgs(JsonNode node);
	public abstract JsonNode getCompilerSpecificArgs();
}
