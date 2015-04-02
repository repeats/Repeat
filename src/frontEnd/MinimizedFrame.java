package frontEnd;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinimizedFrame extends TrayIcon {

	private final BackEndHolder backEnd;

	public MinimizedFrame(Image image, final BackEndHolder backEnd) {
		super(image);

		this.backEnd = backEnd;

		PopupMenu trayPopupMenu = new PopupMenu();

		MenuItem miShow = new MenuItem("Show");
		miShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMainFrame();
			}
		});

		MenuItem miClose = new MenuItem("Exit");
		miClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.exit();
			}
		});

		trayPopupMenu.add(miShow);
		trayPopupMenu.add(miClose);

		setToolTip("Repeat");
		setPopupMenu(trayPopupMenu);
		setImageAutoSize(true);

		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					showMainFrame();
				}
			}
		});
	}

	private void showMainFrame() {
		SystemTray.getSystemTray().remove(MinimizedFrame.this);
		backEnd.main.setState(Frame.NORMAL);
		backEnd.main.toFront();
		backEnd.main.setVisible(true);
	}
}
