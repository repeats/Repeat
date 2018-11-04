package core.cli.client.handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.cli.CliExitCodes;
import core.cli.server.CliRpcCodec;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.HttpClient;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public abstract class CliActionProcessor {

	private static final Logger LOGGER = Logger.getLogger(CliActionProcessor.class.getName());

	protected HttpClient httpClient;

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public abstract void addArguments(Subparsers parser);

	public abstract void handle(Namespace namespace);

	protected final void sendRequest(String path, IJsonable message) {
		byte[] data = CliRpcCodec.encode(JSONUtility.jsonToString(message.jsonize()).getBytes(CliRpcCodec.ENCODING));
		sendRequest(path, data);
	}

	private void sendRequest(String path, byte[] data) {
		try {
			byte[] responseData = httpClient.sendPost(path, data);
			String responseString = CliRpcCodec.decode(responseData);
			LOGGER.info(responseString);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Encountered IOException when talking to server.", e);
			CliExitCodes.IO_EXCEPTION.exit();
		}
	}
}
