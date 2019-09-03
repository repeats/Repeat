package core.languageHandler.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.config.AbstractRemoteRepeatsClientsConfig;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.languageHandler.Language;
import core.userDefinedTask.AggregateUserDefinedAction;
import core.userDefinedTask.UserDefinedAction;
import utilities.Pair;

public class RemoteRepeatsCompiler extends AbstractCompiler {

	private static final Logger LOGGER = Logger.getLogger(RemoteRepeatsCompiler.class.getName());

	private RemoteRepeatsCompilerConfig config;
	private RepeatsPeerServiceClientManager clients;

	public RemoteRepeatsCompiler(RemoteRepeatsCompilerConfig config, RepeatsPeerServiceClientManager clients) {
		this.config = config;
		this.clients = clients;
	}

	public RemoteRepeatsCompiler cloneWithConfig(RemoteRepeatsCompilerConfig config) {
		return new RemoteRepeatsCompiler(config, clients);
	}

	@Override
	public Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, Language language) {
		List<Thread> executions = new ArrayList<>();
		Lock mutex = new ReentrantLock(true);
		List<UserDefinedAction> actions = new ArrayList<>();

		for (String clientId : config.getClients()) {
			if (clientId.equals(AbstractRemoteRepeatsClientsConfig.LOCAL_CLIENT)) {
				continue;
			}

			executions.add(new Thread() {
				@Override
				public void run() {
					RepeatsPeerServiceClient client = clients.getClient(clientId);
					if (client == null) {
						LOGGER.warning("Cannot compile action with client " + clientId + " since it does not exist.");
						return;
					}

					UserDefinedAction action = client.api().actions().createTask(source, language);
					if (action == null) {
						LOGGER.warning("Failed to compile remote Repeats action.");
						return;
					}

					mutex.lock();
					try {
						actions.add(action);
					} finally {
						mutex.unlock();
					}
				}
			});
		}

		for (Thread t : executions) {
			t.start();
		}
		for (Thread t : executions) {
			try {
				t.join();
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Interrupted while waiting for compilaiton thread.", e);
			}
		}
		if (actions.size() != executions.size()) {
			return Pair.of(DynamicCompilerOutput.COMPILATION_ERROR, null);
		}

		return Pair.of(DynamicCompilerOutput.COMPILATION_SUCCESS, AggregateUserDefinedAction.of(actions));
	}

	@Override
	public boolean parseCompilerSpecificArgs(JsonNode node) {
		return true;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		return JsonNodeFactories.object();
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(RemoteRepeatsCompiler.class.getName());
	}
}
