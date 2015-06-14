package utilities.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import core.config.Config;
import core.keyChain.KeyChain;

public class KeyChainInputPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -3725919897922335085L;
	private static final int MAX_KEY_CHAIN = 15;

	private KeyChain keyChain;
	private final DefaultListModel<KeyChain> model;

	public static void main(String[] args) {
		getInputKeyChains(null, 1, Collections.<KeyChain> emptyList());
	}

	public static KeyChain getInputKeyChain(JFrame parent) {
		Set<KeyChain> keys = getInputKeyChains(parent, 1, Collections.<KeyChain> emptyList());
		if (keys != null && keys.size() == 1) {
			return keys.iterator().next();
		}
		return null;
	}

	public static Set<KeyChain> getInputKeyChains(JFrame parent, Collection<KeyChain> prepopulated) {
		return getInputKeyChains(parent, MAX_KEY_CHAIN, prepopulated);
	}

	private static Set<KeyChain> getInputKeyChains(JFrame parent, int limit, Collection<KeyChain> prepopulated) {
		KeyChainInputPanel input = new KeyChainInputPanel(prepopulated);
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
				Set<KeyChain> output = new HashSet<KeyChain>();
				Enumeration<KeyChain> allKeys = input.model.elements();
				while (true) {
					try {
						KeyChain next = allKeys.nextElement();
						if (!next.getKeys().isEmpty()) {
							output.add(next);
						}
					} catch (NoSuchElementException e) {
						break;
					}
				}

				if (!input.keyChain.getKeys().isEmpty()) {
					output.add(input.keyChain);
				}

				return output;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private KeyChainInputPanel() {
		this(Collections.<KeyChain> emptyList());
	}

	private KeyChainInputPanel(Collection<KeyChain> prepopulated) {
		keyChain = new KeyChain();

		final JLabel instruction = new JLabel("Start pressing key chain.");
		final JTextField tf = new JTextField();
		tf.setEditable(false);

		tf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();

				if (code == Config.HALT_TASK) {
					keyChain.getKeys().clear();
				}

				tf.setText(keyChain.toString());
			}
//utsunomiya shion
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				keyChain.getKeys().add(code);
			}
		});

		model = new DefaultListModel<>();
		for (KeyChain key : prepopulated) {
			model.addElement(key);
		}

		final JList<KeyChain> list = new JList<>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		final JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(150, 80));

		final JButton bAdd = new JButton("Add");
		bAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!keyChain.getKeys().isEmpty()) {
					model.addElement(keyChain);
					keyChain = new KeyChain();
					tf.setText("");
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int selected = list.getSelectedIndex();
					if (selected >= 0) {
						model.remove(list.getSelectedIndex());
					}
				}
			}
		});

		final JPanel addButtonPanel = new JPanel();
		addButtonPanel.setLayout(new BoxLayout(addButtonPanel, BoxLayout.X_AXIS));
		addButtonPanel.add(bAdd);
		addButtonPanel.add(javax.swing.Box.createHorizontalStrut(10));
		addButtonPanel.add(new JLabel("(Select and right click to remove.)"));

		final JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
		basicPanel.add(instruction);
		basicPanel.add(tf);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(basicPanel);
		add(javax.swing.Box.createVerticalStrut(5));
		add(addButtonPanel);
		add(javax.swing.Box.createVerticalStrut(5));
		add(scrollPane);
	}
}
