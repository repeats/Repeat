package frontEnd;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import core.KeyChain;

public class HotkeySetting extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -4612578004508862160L;
	private final JPanel contentPane;
	private final JTextField tfRecord;
	private final JTextField tfReplay;
	private final JTextField tfCompiledReplay;

	/**
	 * Create the frame.
	 */
	public HotkeySetting(final BackEndHolder backEnd) {
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
		tfRecord.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				backEnd.keysManager.reRegisterKey(new KeyChain(e.getKeyCode()),
												  backEnd.config.getRECORD(), null);
				backEnd.config.setRECORD(e.getKeyCode());

				tfRecord.setText(KeyEvent.getKeyText(e.getKeyCode()));
			}
		});

		tfReplay = new JTextField(backEnd.config.getREPLAY().toString());
		tfReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfReplay.setEditable(false);
		tfReplay.setColumns(10);
		tfReplay.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				backEnd.keysManager.reRegisterKey(new KeyChain(e.getKeyCode()),
												  backEnd.config.getREPLAY(), null);
				backEnd.config.setREPLAY(e.getKeyCode());

				tfReplay.setText(KeyEvent.getKeyText(e.getKeyCode()));
			}
		});


		tfCompiledReplay = new JTextField(backEnd.config.getCOMPILED_REPLAY().toString());
		tfCompiledReplay.setHorizontalAlignment(SwingConstants.CENTER);
		tfCompiledReplay.setEditable(false);
		tfCompiledReplay.setColumns(10);
		tfCompiledReplay.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				backEnd.keysManager.reRegisterKey(new KeyChain(e.getKeyCode()),
												  backEnd.config.getCOMPILED_REPLAY(), null);
				backEnd.config.setCOMPILED_REPLAY(e.getKeyCode());

				tfCompiledReplay.setText(KeyEvent.getKeyText(e.getKeyCode()));
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
