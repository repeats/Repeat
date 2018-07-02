package utilities;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HttpClient {

	private static final Logger LOGGER = Logger.getLogger(HttpClient.class.getName());

	private final String serverAddress;
	private final String protocol;
	private final int timeout;

	public HttpClient(String serverAddress, Config config) {
		this.serverAddress = serverAddress;
		this.protocol = config.protocol;
		this.timeout = config.timeout;
	}

	public static class Config {
		private static final String DEFAULT_PROTOCOL = "http";
		private static final int DEFAULT_TIMEOUT_MS = 5000;

		private String protocol = DEFAULT_PROTOCOL;
		private int timeout = DEFAULT_TIMEOUT_MS;

		public static Config of() {
			return new Config();
		}

		public Config setTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public Config setProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}
	}

	public byte[] sendPost(String path, byte[] data) throws IOException {
		HttpURLConnection connection = null;

		try {
			String urlPath = String.format("%s://%s%s", protocol, serverAddress, path);
			LOGGER.info("Sending POST request to " + urlPath);

			// Create connection.
			URL url = new URL(urlPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", Integer.toString(data.length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setReadTimeout(timeout);

			// Send request.
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data);
			wr.close();

			InputStream is = null;
			int code = connection.getResponseCode();
			if (code != 200) {
				LOGGER.warning("Server responded with non OK code " + code);
				is = connection.getErrorStream();
			} else { // Get response.
				is = connection.getInputStream();
			}
			byte[] output = IoUtil.streamToBytes(is);
			is.close();
			return output;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
