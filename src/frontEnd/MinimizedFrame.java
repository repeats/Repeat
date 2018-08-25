package frontEnd;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;

public class MinimizedFrame extends TrayIcon {

	private static final Logger LOGGER = Logger.getLogger(MinimizedFrame.class.getName());

	private final MainBackEndHolder backEnd;

	public MinimizedFrame(Image image, final MainBackEndHolder backEnd) {
		super(image);

		this.backEnd = backEnd;

		PopupMenu trayPopupMenu = new PopupMenu();

		MenuItem miInterface = new MenuItem("Show UI");
		miInterface.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				show();
			}
		});

		MenuItem miClose = new MenuItem("Exit");
		miClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		trayPopupMenu.add(miInterface);
		trayPopupMenu.add(miClose);

		setToolTip("Repeat");
		setPopupMenu(trayPopupMenu);
		setImageAutoSize(true);

		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
					show();
				}
			}
		});
	}

	private void show() {
		if (!Desktop.isDesktopSupported()) {
			LOGGER.warning("Cannot open browser to UI since Desktop module is not supported.");
			return;
		}

		IIPCService server = IPCServiceManager.getIPCService(IPCServiceName.WEB_UI_SERVER);
		try {
			Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort()));
		} catch (IOException | URISyntaxException ex) {
			LOGGER.log(Level.WARNING, "Failed to show UI in browser.", ex);
		}
	}

	protected void add() throws AWTException {
		SystemTray.getSystemTray().add(this);
	}

	protected void remove() {
		SystemTray.getSystemTray().remove(this);
	}

	private void exit() {
		backEnd.exit();
	}
}
