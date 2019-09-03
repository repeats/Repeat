package core.languageHandler.compiler;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import utilities.ILoggable;
import utilities.Pair;

public abstract class AbstractCompiler implements ILoggable {
	public abstract Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, Language language);

	public abstract boolean parseCompilerSpecificArgs(JsonNode node);
	public abstract JsonNode getCompilerSpecificArgs();
}
