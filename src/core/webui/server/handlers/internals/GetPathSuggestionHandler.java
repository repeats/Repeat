package core.webui.server.handlers.internals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

public class GetPathSuggestionHandler extends AbstractSingleMethodHttpHandler {

	public GetPathSuggestionHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseGetParameters(request.getRequestLine().getUri());
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse GET parameters.");
		}

		String path = params.get("path");
		if (path == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path must be provided.");
		}

		if (path.isEmpty()) {
			path = ".";
		}

		Path p = Paths.get(path);
		if (!Files.exists(p)) {
			return paths(exchange);
		}
		if (Files.isRegularFile(p)) {
			return paths(exchange, p.toAbsolutePath().toString());
		}
		if (Files.isDirectory(p)) {
			File[] files = p.toFile().listFiles();
			List<String> suggested = Arrays.asList(files).stream().map(File::getAbsolutePath).collect(Collectors.toList());
			return paths(exchange, suggested);
		}

		return paths(exchange);
	}

	private Void paths(HttpAsyncExchange exchange, String... paths) throws IOException {
		return paths(exchange, Arrays.asList(paths));
	}

	private Void paths(HttpAsyncExchange exchange, Iterable<String> paths) throws IOException {
		String data = JSONUtility.jsonToString(Jsonizer.jsonize(SuggestedPaths.of(paths)).getRootNode());
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, data);
	}

	private static class SuggestedPaths {
		private List<String> paths;

		private static SuggestedPaths of(Iterable<String> paths) {
			SuggestedPaths output = new SuggestedPaths();
			output.paths = new ArrayList<>();
			paths.forEach(output.paths::add);
			return output;
		}
	}
}
