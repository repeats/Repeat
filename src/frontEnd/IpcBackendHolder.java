package frontEnd;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import utilities.swing.SwingUtil;
import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;

public class IpcBackendHolder implements ILoggable {
	private final IpcFrame frame;

	protected IpcBackendHolder(IpcFrame frame) {
		this.frame = frame;
	}

	protected void startProcess() {
		try {
			IIPCService selected = getSelectedService();
			if (selected != null) {
				selected.startRunning();
			}
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Unable to start service...", e);
		}
		renderServices();
	}

	protected void stopProcess() {
		try {
			IIPCService selected = getSelectedService();
			if (selected != null) {
				selected.stopRunning();
			}
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Unable to stop service...", e);
		}
		renderServices();
	}

	protected void renderServices() {
		SwingUtil.TableUtil.clearTable(frame.tIpc);
		SwingUtil.TableUtil.setRowNumber(frame.tIpc, IPCServiceManager.IPC_SERVICE_COUNT);
		for (int i = 0; i < IPCServiceManager.IPC_SERVICE_COUNT; i++) {
			IIPCService service = IPCServiceManager.getIPCService(i);
			frame.tIpc.setValueAt(service.getName(), i, IpcFrame.COLUMN_NAME);
			frame.tIpc.setValueAt(service.getPort(), i, IpcFrame.COLUMN_PORT);
			frame.tIpc.setValueAt(service.isRunning(), i, IpcFrame.COLUMN_STATUS);
		}
	}

	private IIPCService getSelectedService() {
		int selected = frame.tIpc.getSelectedRow();
		if (selected < 0 || selected >= IPCServiceManager.IPC_SERVICE_COUNT) {
			return null;
		}

		IIPCService output = IPCServiceManager.getIPCService(selected);
		return output;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(IpcBackendHolder.class.getName());
	}
}
