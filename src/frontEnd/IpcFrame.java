package frontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import staticResources.BootStrapResources;

@SuppressWarnings("serial")
public class IpcFrame extends JFrame {

	protected static final int COLUMN_NAME = 0;
	protected static final int COLUMN_PORT = 1;
	protected static final int COLUMN_STATUS = 2;
	protected static final int COLUMN_LAUCNH_AT_STARTUP = 3;

	private final JPanel contentPane;
	protected final JTable tIpc;
	protected final MainBackEndHolder mainFrame;

	protected IpcBackendHolder ipcBackEndHolder;

	/**
	 * Create the frame.
	 */
	public IpcFrame(final MainBackEndHolder mainFrame) {
		this.mainFrame = mainFrame;
		ipcBackEndHolder = new IpcBackendHolder(this);
		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				ipcBackEndHolder.periodicRefresh();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				ipcBackEndHolder.stopPeriodicRefresh();
			}
		});

		setTitle("IPC Modules");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton bStart = new JButton();
		bStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ipcBackEndHolder.startProcess();
			}
		});
		bStart.setIcon(BootStrapResources.PLAY);
		bStart.setToolTipText("Start this ipc service");

		JButton bStop = new JButton();
		bStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ipcBackEndHolder.stopProcess();
			}
		});
		bStop.setIcon(BootStrapResources.STOP);
		bStop.setToolTipText("Stop this ipc service");

		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(bStart)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bStop)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(bStart, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(bStop, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
					.addContainerGap())
		);

		tIpc = new JTable();
		tIpc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				ipcBackEndHolder.mouseReleasedIPCTable(e);
			}
		});
		tIpc.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null},
			},
			new String[] {
				"Process", "Port", "Running", "Launch at startup"
			}
		));
		tIpc.getColumnModel().getColumn(0).setPreferredWidth(103);
		tIpc.getColumnModel().getColumn(3).setPreferredWidth(115);

		DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i < tIpc.getColumnCount(); i++) {
			tIpc.getColumnModel().getColumn(i).setCellRenderer(centerRender);
		}
		ipcBackEndHolder.renderServices();
		scrollPane.setViewportView(tIpc);
		contentPane.setLayout(gl_contentPane);
	}
}
