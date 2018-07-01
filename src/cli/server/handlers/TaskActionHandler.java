package cli.server.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import cli.CliExitCodes;
import cli.MainCli;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

public class TaskActionHandler implements CliActionProcessor {

	private static final Logger LOGGER = Logger.getLogger(MainCli.class.getName());

	@Override
	public void addArguments(ArgumentParser parser) {
		parser.addArgument("-a", "--action").required(true)
        		.choices("add", "remove", "execute")
        		.help("Specify action on task.");
		parser.addArgument("-n", "--name").required(true)
				.help("Name of the task, or its index (zero based) in the group if the task exists. "
						+ "This tries to interpret this as an integer index first, then as a task name. "
						+ "For remove action, if multiple tasks share the same name, "
						+ "only the first one in the list will be removed.");
		parser.addArgument("-g", "--group").setDefault("")
				.help("Name of the group,  or its index (zero based). "
						+ "This tries to interpret this as an integer index first, then as a group name."
						+ "If not specified then the first group will be used.");
		parser.addArgument("-s", "--source_file").setDefault("")
				.help("Path to the source file. Required when adding new task.");
	}

	@Override
	public void handle(Namespace namespace) {
		String action = namespace.getString("action");
		if (action.equals("add")) {
			System.out.println("Adding action.");
		} else if (action.equals("remove")) {
			System.out.println("Removing action.");
		} else if (action.equals("execute")) {
			System.out.println("Execute action.");
		} else {
			LOGGER.log(Level.SEVERE, "Unknown task action " + action);
			CliExitCodes.UNKNOWN_ACTION.exit();
		}
	}
}
