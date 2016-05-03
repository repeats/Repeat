package frontEnd;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import utilities.swing.SwingUtil;
import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;

public class IpcBackendHolder implements ILoggable {
	private final IpcFrame frame;
	private ScheduledFuture<?> scheduled;

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

	protected void periodicRefresh() {
		if (scheduled != null) {
			return;
		}

		scheduled = frame.mainFrame.executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				renderServices();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	protected void stopPeriodicRefresh() {
		scheduled.cancel(false);
		scheduled = null;
	}

	protected void renderServices() {
		SwingUtil.TableUtil.clearTable(frame.tIpc);
		SwingUtil.TableUtil.setRowNumber(frame.tIpc, IPCServiceManager.IPC_SERVICE_COUNT);
		for (int i = 0; i < IPCServiceManager.IPC_SERVICE_COUNT; i++) {
			IIPCService service = IPCServiceManager.getIPCService(i);
			frame.tIpc.setValueAt(service.getName(), i, IpcFrame.COLUMN_NAME);
			frame.tIpc.setValueAt(service.getPort(), i, IpcFrame.COLUMN_PORT);
			frame.tIpc.setValueAt(service.isRunning(), i, IpcFrame.COLUMN_STATUS);
			frame.tIpc.setValueAt(service.isLaunchAtStartup(), i, IpcFrame.COLUMN_LAUCNH_AT_STARTUP);
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

	protected void mouseReleasedIPCTable(MouseEvent e) {
		int col = frame.tIpc.getSelectedColumn();

		if (col != IpcFrame.COLUMN_LAUCNH_AT_STARTUP) {
			return;
		}

		IIPCService service = getSelectedService();
		if (service == null) {
			return;
		}

		service.setLaunchAtStartup(!service.isLaunchAtStartup());
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(IpcBackendHolder.class.getName());
	}
}
