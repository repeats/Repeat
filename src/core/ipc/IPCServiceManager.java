package core.ipc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import utilities.Function;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.ipc.repeatClient.CSharpIPCClientService;
import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatServer.ControllerServer;
import core.languageHandler.Language;

public final class IPCServiceManager {

	private static final Logger LOGGER = Logger.getLogger(IPCServiceManager.class.getName());

	public static final int IPC_SERVICE_COUNT = 3;
	private static final long INTER_SERVICE_BOOT_TIME_MS = 2000;
	private static final IIPCService[] ipcServices;
	private static final Map<Language, Integer> ipcByLanugage;

	static {
		ipcServices = new IIPCService[IPC_SERVICE_COUNT];
		ipcServices[IPCServiceName.CONTROLLER_SERVER.value()] =  new ControllerServer();
		ipcServices[IPCServiceName.PYTHON.value()] = new PythonIPCClientService();
		ipcServices[IPCServiceName.CSHARP.value()] = new CSharpIPCClientService();

		ipcByLanugage = new HashMap<>();
		ipcByLanugage.put(Language.JAVA, -1);
		ipcByLanugage.put(Language.PYTHON, IPCServiceName.PYTHON.value());
		ipcByLanugage.put(Language.CSHARP, IPCServiceName.CSHARP.value());
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
		return ipcServices[name.value()];
	}

	public static IIPCService getIPCService(int index) {
		if (index >= IPC_SERVICE_COUNT) {
			return null;
		}

		return ipcServices[index];
	}

	public static void initiateServices() throws IOException {
		for (IPCServiceName name : IPCServiceName.ALL_SERVICE_NAMES) {
			IIPCService service = IPCServiceManager.getIPCService(name);
			if (!service.isLaunchAtStartup()) {
				continue;
			}
			service.startRunning();
			LOGGER.info("Starting ipc service " + service.getName());

			try {
				Thread.sleep(INTER_SERVICE_BOOT_TIME_MS);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void stopServices() throws IOException {
		for (int i = IPCServiceName.ALL_SERVICE_NAMES.length - 1; i >= 0; i--) {
			IPCServiceName name = IPCServiceName.ALL_SERVICE_NAMES[i];
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
		for (JsonNode language : ipcSettings) {
			String name = language.getStringValue("name");
			boolean launchAtStartup = language.getBooleanValue("launch_at_startup");
			Language currentLanguage = Language.identify(name);
			IIPCService service = IPCServiceManager.getIPCService(currentLanguage);
			if (service != null) {
				service.setLaunchAtStartup(launchAtStartup);
			}
		}
		return true;
	}

	public static JsonNode jsonize() {
		return JsonNodeFactories.array(
				new Function<Language, JsonNode>() {
					@Override
					public JsonNode apply(Language l) {
						IIPCService service = getIPCService(l);

						return JsonNodeFactories.object(
								JsonNodeFactories.field("name", JsonNodeFactories.string(l.toString())),
								JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(service == null ? false : service.isLaunchAtStartup()))
								);
					}
				}.map(Language.ALL_LANGUAGES)
			);
	}

	private IPCServiceManager() {}
}
