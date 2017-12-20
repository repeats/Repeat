package frontEnd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;
import utilities.swing.KeyChainInputPanel;

@SuppressWarnings("serial")
public class HotkeySetting extends JFrame {

	private final JPanel contentPane;
	private final JTextField tfRecord;
	private final JTextField tfReplay;
	private final JTextField tfCompiledReplay;
	private final JTextField tfMouseGestureActivation;

	/**
	 * Create the frame.
	 */
	public HotkeySetting(final MainBackEndHolder backEnd) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				tfRecord.setText(backEnd.config.getRECORD().toString());
				tfReplay.setText(backEnd.config.getREPLAY().toString());
				tfCompiledReplay.setText(backEnd.config.getCOMPILED_REPLAY().toString());
				tfMouseGestureActivation.setText(new KeyChain(backEnd.config.getMouseGestureActivationKey()).toString());
			}
		});
		setResizable(false);
		setTitle("Hotkey Setting");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 344, 185);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblNewLabel = new JLabel("Start/Stop Record");
		JLabel lblNewLabel_1 = new JLabel("Start/Stop Replay");
		JLabel lblNewLabel_2 = new JLabel("Start/Stop Compiled Replay");

		tfRecord = new JTextField("Record");
		tfRecord.setHorizontalAlignment(SwingConstants.CENTER);
		tfRecord.setEditable(false);
		tfRecord.setColumns(10);
		tfRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(
															HotkeySetting.this,
															backEnd.config.getRECORD());

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchRecord,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setRECORD(newKeyChain);

					tfRecord.setText(newKeyChain.toString());
				}
			}
		});

		tfReplay = new JTextField("Replay");
		tfReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfReplay.setEditable(false);
		tfReplay.setColumns(10);
		tfReplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(
															HotkeySetting.this,
															backEnd.config.getREPLAY());

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchReplay,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setREPLAY(newKeyChain);

					tfReplay.setText(newKeyChain.toString());
				}
			}
		});


		tfCompiledReplay = new JTextField("Compiled replay");
		tfCompiledReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfCompiledReplay.setEditable(false);
		tfCompiledReplay.setColumns(10);
		tfCompiledReplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(
															HotkeySetting.this,
															backEnd.config.getCOMPILED_REPLAY());

				if (newKeyChain != null) {
					backEnd.keysManager.reRegisterTask(
							backEnd.switchReplayCompiled,
							TaskActivation.newBuilder().withHotKey(newKeyChain).build());
					backEnd.config.setCOMPILED_REPLAY(newKeyChain);

					tfCompiledReplay.setText(newKeyChain.toString());
				}
			}
		});

		JLabel lblNewLabel_3 = new JLabel("Mouse gesture activation");

		tfMouseGestureActivation = new JTextField("Mouse gesture activation");
		tfMouseGestureActivation.setHorizontalAlignment(SwingConstants.CENTER);
		tfMouseGestureActivation.setEditable(false);
		tfMouseGestureActivation.setColumns(10);
		tfMouseGestureActivation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				KeyChain newKeyChain = KeyChainInputPanel.getInputKeyChain(
						HotkeySetting.this,
						new KeyChain(backEnd.config.getMouseGestureActivationKey()));
				if (newKeyChain == null) {
					return;
				}
				if (newKeyChain.getSize() > 1) {
					JOptionPane.showMessageDialog(HotkeySetting.this,
													"You can choose only 1 key to activate mouse gesture recognition.",
													"Title", JOptionPane.WARNING_MESSAGE);
					return;
				}

				backEnd.config.setMouseGestureActivationKey(newKeyChain.getKeyStrokes().get(0).getKey());
				tfMouseGestureActivation.setText(newKeyChain.toString());
			}
		});


		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(lblNewLabel_1)
								.addComponent(lblNewLabel_2))
							.addPreferredGap(ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(tfReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfRecord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfCompiledReplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel_3)
							.addPreferredGap(ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
							.addComponent(tfMouseGestureActivation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
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
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfMouseGestureActivation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
