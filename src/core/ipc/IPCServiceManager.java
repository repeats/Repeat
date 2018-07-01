package core.ipc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import cli.server.CliServer;
import core.ipc.repeatClient.CSharpIPCClientService;
import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatClient.ScalaIPCClientService;
import core.ipc.repeatServer.ControllerServer;
import core.languageHandler.Language;
import frontEnd.MainBackEndHolder;
import utilities.Function;

public final class IPCServiceManager {

	private static final Logger LOGGER = Logger.getLogger(IPCServiceManager.class.getName());

	public static final int IPC_SERVICE_COUNT = IPCServiceName.values().length;
	private static final long INTER_SERVICE_BOOT_TIME_MS = 2000;
	private static final IIPCService[] ipcServices;
	private static final Map<Language, Integer> ipcByLanugage;

	static {
		ipcServices = new IIPCService[IPC_SERVICE_COUNT];
		ipcServices[IPCServiceName.CONTROLLER_SERVER.value()] =  new ControllerServer();
		ipcServices[IPCServiceName.CLI_SERVER.value()] =  new CliServer();
		ipcServices[IPCServiceName.PYTHON.value()] = new PythonIPCClientService();
		ipcServices[IPCServiceName.CSHARP.value()] = new CSharpIPCClientService();
		ipcServices[IPCServiceName.SCALA.value()] = new ScalaIPCClientService();

		ipcByLanugage = new HashMap<>();
		ipcByLanugage.put(Language.JAVA, -1);
		ipcByLanugage.put(Language.PYTHON, IPCServiceName.PYTHON.value());
		ipcByLanugage.put(Language.CSHARP, IPCServiceName.CSHARP.value());
		ipcByLanugage.put(Language.SCALA, IPCServiceName.SCALA.value());
	}

	public static IIPCService getIPCService(Language name) {
		int index = ipcByLanugage.get(name);
		if (index >= 0) {
			return ipcServices[index];
		} else {
			return null;
		}
	}

	public static IIPCService getIPCService(IPCServiceName name) {
		if (name == null) {
			return null;
		}
		return ipcServices[name.value()];
	}

	public static IIPCService getIPCService(int index) {
		if (index >= IPC_SERVICE_COUNT) {
			return null;
		}

		return ipcServices[index];
	}

	public static void initiateServices(MainBackEndHolder backEndHolder) throws IOException {
		for (IPCServiceName name : IPCServiceName.values()) {
			IIPCService service = IPCServiceManager.getIPCService(name);
			if (!service.isLaunchAtStartup()) {
				continue;
			}
			service.startRunning();
			LOGGER.info("Starting ipc service " + service.getName());
			if (name == IPCServiceName.CLI_SERVER) {
				CliServer server = (CliServer) service;
				server.setMainBackEndHolder(backEndHolder);
			}

			try {
				Thread.sleep(INTER_SERVICE_BOOT_TIME_MS);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void stopServices() throws IOException {
		for (int i = IPCServiceName.values().length - 1; i >= 0; i--) {
			IPCServiceName name = IPCServiceName.values()[i];
			IIPCService service = IPCServiceManager.getIPCService(name);
			do {
				service.stopRunning();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (service.isRunning());
		}
	}

	public static boolean parseJSON(List<JsonNode> ipcSettings) {
		boolean result = true;

		for (JsonNode language : ipcSettings) {
			String name = language.getStringValue("name");
			IIPCService service = IPCServiceManager.getIPCService(IPCServiceName.identifyService(name));

			if (service != null) {
				boolean newResult = service.extractSpecificConfig(language.getNode("config"));
				if (!newResult) {
					LOGGER.warning("Unable to parse config for ipc service " + name);
				}
				result &= newResult;
			}
		}

		return result;
	}

	public static JsonNode jsonize() {
		return JsonNodeFactories.array(
				new Function<IPCServiceName, JsonNode>() {
					@Override
					public JsonNode apply(IPCServiceName n) {
						IIPCService service = getIPCService(n);

						return JsonNodeFactories.object(
								JsonNodeFactories.field("name", JsonNodeFactories.string(n.toString())),
								JsonNodeFactories.field("config", service == null ? JsonNodeFactories.nullNode(): service.getSpecificConfig())
								);
					}
				}.map(IPCServiceName.values())
			);
	}

	private IPCServiceManager() {}
}
