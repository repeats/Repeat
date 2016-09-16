package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.Function;
import core.languageHandler.Language;

public class ScalaSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(ScalaSourceGenerator.class.getName());

	public ScalaSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return "Thread.sleep(" + r + ")";
			}
		});
	}

	@Override
	protected boolean internalSubmitTask(long time, String device, String action, int[] param) {
		// TODO add implementation
		return false;
	}

	@Override
	public String getSource() {
		String mainSource = sourceScheduler.getSource();
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(BootStrapResources.getNativeLanguageTemplate(Language.SCALA));
		sb.append(mainSource);

		return sb.toString();
	}

}
