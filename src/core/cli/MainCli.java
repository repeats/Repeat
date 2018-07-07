package core.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.cli.client.handlers.CliActionProcessor;
import core.cli.client.handlers.TaskActionHandler;
import core.config.CliConfig;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.HttpClient;

public class MainCli {

	private static final Logger LOGGER = Logger.getLogger(MainCli.class.getName());

	private Map<String, CliActionProcessor> processors;

	public MainCli() {
		processors = new HashMap<>();
		processors.put("task", new TaskActionHandler());
	}

	private ArgumentParser setupParser() {
		ArgumentParser parser = ArgumentParsers.newFor("Repeat").build()
                .defaultHelp(true)
                .description("Execute Repeat operations in the terminal.");
		parser.addArgument("-s", "--host").type(String.class)
				.setDefault("localhost")
				.help("Specify a custom host at which the Repeat server is running.");
		parser.addArgument("-p", "--port").type(Integer.class)
				.help("Specify a custom port at which the Repeat server is running."
						+ "If not specified, port value is read from config file.");

		Subparsers subParsers = parser.addSubparsers().help("Help for each individual command.");
        for (CliActionProcessor processor : processors.values()) {
        	processor.addArguments(subParsers);
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
            CliExitCodes.INVALID_ARGUMENTS.exit();
        }
        CliConfig config = new CliConfig();
		config.loadConfig(null);
		// Override port if provided.
		Integer customPort = namespace.getInt("port");
		if (customPort != null) {
			config.setServerPort(customPort);
		}

		String serverAddress = String.format("%s:%s", namespace.getString("host"), config.getServerPort());
        HttpClient client = new HttpClient(serverAddress, HttpClient.Config.of());
        for (CliActionProcessor processor : processors.values()) {
        	processor.setHttpClient(client);
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
