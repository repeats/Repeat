package cli.handlers;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

public interface CliActionProcessor {
	public void addArguments(ArgumentParser parser);
	public void handle(Namespace namespace);
}
