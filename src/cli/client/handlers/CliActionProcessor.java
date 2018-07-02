package cli.client.handlers;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.HttpClient;

public abstract class CliActionProcessor {

	protected HttpClient httpClient;

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public abstract void addArguments(Subparsers parser);

	public abstract void handle(Namespace namespace);
}
