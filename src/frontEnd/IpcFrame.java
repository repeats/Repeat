package frontEnd;

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

import frontEnd.graphics.BootStrapResources;

@SuppressWarnings("serial")
public class IpcFrame extends JFrame {

	private final JPanel contentPane;
	private final JTable tIpc;

	/**
	 * Create the frame.
	 */
	public IpcFrame(final BackEndHolder mainFrame) {
		setTitle("IPC Modules");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton bStart = new JButton();
		bStart.setIcon(BootStrapResources.PLAY);
		bStart.setToolTipText("Start this ipc service");

		JButton bStop = new JButton();
		bStop.setIcon(BootStrapResources.STOP);
		bStop.setToolTipText("Stop this ipc service");

		JButton bRestart = new JButton();
		bRestart.setIcon(BootStrapResources.MOVE);
		bRestart.setToolTipText("Restart this ipc service");

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
							.addComponent(bStop)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bRestart)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(bStart, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(bStop, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(bRestart))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
					.addContainerGap())
		);

		tIpc = new JTable();
		tIpc.setModel(new DefaultTableModel(
			new Object[][] {
				{"Controller Server", "0", "Running"},
				{null, null, null},
			},
			new String[] {
				"Process", "Port", "Status"
			}
		){
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return column == 1;
		    }
		});
		DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i < tIpc.getColumnCount(); i++) {
			tIpc.getColumnModel().getColumn(i).setCellRenderer(centerRender);
		}

		tIpc.getColumnModel().getColumn(0).setPreferredWidth(103);
		scrollPane.setViewportView(tIpc);
		contentPane.setLayout(gl_contentPane);
	}
}
