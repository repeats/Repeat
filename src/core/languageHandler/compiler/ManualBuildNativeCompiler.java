package core.languageHandler.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.simplenativehooks.utilities.FileUtility;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;
import core.userDefinedTask.manualBuild.ManuallyBuildAction;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import utilities.RandomUtil;
import utilities.json.JSONUtility;

public class ManualBuildNativeCompiler extends AbstractNativeCompiler {

	private static final Logger LOGGER = Logger.getLogger(ManualBuildNativeCompiler.class.getName());

	public static final String VERSION = "1.0";
	public static final String VERSION_PREFIX = "version=";

	private final File tempSourceDir;

	public ManualBuildNativeCompiler(File tempSourceDir) {
		this.tempSourceDir = tempSourceDir;
	}

	/**
	 * Start with a simple compiler:
	 *
	 * If a line is empty starts with "//", ignore it.
	 * The first parsable line must be "version=xxx".
	 * Every other line is a complete JSON object specifying a step.
	 *
	 * See {@link ManuallyBuildActionConstructor#generateSource()} for JSON format.
	 */
	@Override
	public DynamicCompilationResult compile(String source) {
		String[] lines = source.split("\n");
		boolean foundVersion = false;

		List<ManuallyBuildStep> steps = new ArrayList<>(lines.length);
		for (String line : lines) {
			String trimmed = line.trim();

			// If a line is empty or starts with "//", ignore it.
			if (trimmed.isEmpty() || trimmed.startsWith("//")) {
				continue;
			}

			if (!foundVersion) {
				if (!trimmed.startsWith(VERSION_PREFIX)) {
					LOGGER.warning("First parsable line must start with '" + VERSION_PREFIX + "' but got " + trimmed);
					return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_ERROR, null);
				}

				foundVersion = true;
				continue;
			}

			JsonNode node = JSONUtility.jsonFromString(trimmed);
			if (node == null) {
				LOGGER.warning("Cannot parse JSON string " + trimmed + ".");
				return DynamicCompilationResult.of(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, null);
			}

			ManuallyBuildStep s = ManuallyBuildStep.parseJSON(node);
			if (s == null) {
				getLogger().warning("Umable to parse step from JSON " + JSONUtility.jsonToString(node));
				return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_ERROR, null);
			}
			steps.add(s);
		}

		ManuallyBuildAction action = ManuallyBuildAction.of(steps);
		File sourceFile = getSourceFile(RandomUtil.randomID());
		if (!FileUtility.writeToFile(source, sourceFile, false)) {
			LOGGER.warning("Cannot write source code to file.");
			return DynamicCompilationResult.of(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, null);
		}

		action.setSourcePath(sourceFile.getAbsolutePath());
		LOGGER.info("Successfully compiled custom action.");
		return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_SUCCESS, action);
	}

	@Override
	public DynamicCompilationResult compile(String source, File objectFile) {
		return compile(source);
	}

	@Override
	public Language getName() {
		return Language.MANUAL_BUILD;
	}

	@Override
	public String getExtension() {
		return ".manualbuild";
	}

	@Override
	public String getObjectExtension() {
		return ".manualbuildobj";
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
		return true;
	}

	@Override
	protected File getSourceFile(String compilingAction) {
		return new File(FileUtility.joinPath(tempSourceDir.getAbsolutePath(), getDummyPrefix() + compilingAction + getExtension()));
	}

	@Override
	protected String getDummyPrefix() {
		return "MANUAL_";
	}

	@Override
	public boolean parseCompilerSpecificArgs(JsonNode node) {
		return false;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		return JsonNodeFactories.object();
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ManualBuildNativeCompiler.class.getName());
	}
}
