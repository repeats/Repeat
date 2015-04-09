package utilities.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.keyChain.KeyChain;

public class KeyChainInputPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -3725919897922335085L;

	private final KeyChain keyChain;

	public static void main(String[] args) {
		getInputKeyChain(null);
	}

	public static KeyChain getInputKeyChain(JFrame parent) {
		KeyChainInputPanel input = new KeyChainInputPanel();
		final JOptionPane optionPane = new JOptionPane(input, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

		final JDialog dialog = new JDialog (parent, "Key chain input", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setContentPane(optionPane);

		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
	        @Override
			public void propertyChange(PropertyChangeEvent e) {
	            String prop = e.getPropertyName();
	            if (dialog.isVisible() && (e.getSource() == optionPane) &&
	            	(prop.equals(JOptionPane.VALUE_PROPERTY))) {
	                dialog.setVisible(false);
	            }
	        }
	    });


		dialog.pack();
		dialog.setVisible(true);

		try {
			int value = ((Integer)optionPane.getValue()).intValue();
			if (value == JOptionPane.YES_OPTION) {
			    return input.keyChain;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private KeyChainInputPanel() {
		keyChain = new KeyChain();

		JLabel instruction = new JLabel("Start pressing key chain.");
		final JTextField tf = new JTextField();
		tf.setEditable(false);

		tf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();

				if (code == KeyEvent.VK_ESCAPE) {
					keyChain.getKeys().clear();
				}

				tf.setText(keyChain.toString());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				keyChain.getKeys().add(code);
			}
		});

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(instruction);
		add(tf);
	}
}
