package frontEnd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utilities.swing.KeyChainInputPanel;
import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;

@SuppressWarnings("serial")
public class HotkeySetting extends JFrame {

	private final JPanel contentPane;
	private final JTextField tfRecord;
	private final JTextField tfReplay;
	private final JTextField tfCompiledReplay;

	/**
	 * Create the frame.
	 */
	public HotkeySetting(final MainBackEndHolder backEnd) {
		setResizable(false);
		setTitle("Hotkey Setting");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 344, 140);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblNewLabel = new JLabel("Start/Stop Record");
		JLabel lblNewLabel_1 = new JLabel("Start/Stop Replay");
		JLabel lblNewLabel_2 = new JLabel("Start/Stop Compiled Replay");

		tfRecord = new JTextField(backEnd.config.getRECORD().toString());
		tfRecord.setHorizontalAlignment(SwingConstants.CENTER);
		tfRecord.setEditable(false);
		tfRecord.setColumns(10);
		tfRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(HotkeySetting.this);

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchRecord,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setRECORD(newKeyChain);

					tfRecord.setText(newKeyChain.toString());
				}
			}
		});

		tfReplay = new JTextField(backEnd.config.getREPLAY().toString());
		tfReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfReplay.setEditable(false);
		tfReplay.setColumns(10);
		tfReplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(HotkeySetting.this);

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchReplay,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setREPLAY(newKeyChain);

					tfReplay.setText(newKeyChain.toString());
				}
			}
		});


		tfCompiledReplay = new JTextField(backEnd.config.getCOMPILED_REPLAY().toString());
		tfCompiledReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfCompiledReplay.setEditable(false);
		tfCompiledReplay.setColumns(10);
		tfCompiledReplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(HotkeySetting.this);

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchReplayCompiled,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setCOMPILED_REPLAY(newKeyChain);

					tfCompiledReplay.setText(newKeyChain.toString());
				}
			}
		});


		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_2))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(tfCompiledReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
								.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tfReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfRecord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(tfRecord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(tfReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(tfCompiledReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
