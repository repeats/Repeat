package cli;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cli.handlers.CliActionProcessor;
import cli.handlers.TaskActionHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import utilities.StringUtilities;

public class MainCli {

	private static final Logger LOGGER = Logger.getLogger(MainCli.class.getName());

	private Map<String, CliActionProcessor> processors;

	public MainCli() {
		processors = new HashMap<>();
		processors.put("task", new TaskActionHandler());
	}

	private ArgumentParser setupParser() {
		ArgumentParser parser = ArgumentParsers.newFor("Checksum").build()
                .defaultHelp(true)
                .description("Calculate checksum of given files.");
        parser.addArgument("module")
        		.required(true)
        		.type(String.class)
                .help("Main module for action. Pick one from " + StringUtilities.join(processors.keySet(), ", ") + ".");
        for (CliActionProcessor processor : processors.values()) {
        	processor.addArguments(parser);
        }

        return parser;
	}

	public void process(String[] args) {
		ArgumentParser parser = setupParser();
        Namespace namespace = null;
        try {
            namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            CliExitCodes.INVALID_ARGUMENTS.exit();;
        }
        String action = namespace.get("module");
        CliActionProcessor processor = processors.get(action);
        if (processor == null) {
        	LOGGER.log(Level.SEVERE, "Unknown action " + action);
        	CliExitCodes.UNKNOWN_ACTION.exit();
        }
        processor.handle(namespace);
	}
}
