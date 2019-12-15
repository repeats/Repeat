package utilities.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

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
import core.keyChain.ActivationPhrase;
import core.keyChain.KeyChain;
import core.keyChain.KeySequence;
import core.keyChain.KeyStroke;
import core.keyChain.MouseGesture;
import core.keyChain.TaskActivation;

@SuppressWarnings("serial")
public class KeyChainInputPanel extends JPanel {

	private static final int MAX_KEY_CHAIN = 15;

	private static final ReentrantLock inUse = new ReentrantLock();

	private JTextField tfPhrase;
	private List<KeyStroke> keyStrokes;
	private final DefaultListModel<KeyChain> modelKeyChain;
	private final DefaultListModel<KeySequence> modelKeySequence;
	private final DefaultListModel<ActivationPhrase> modelPhrases;
	private final JList<MouseGesture> mouseGestureList;

	public static void main(String[] args) {
		TaskActivation.Builder ac = TaskActivation.newBuilder();
		HashSet<MouseGesture> gs = new HashSet<MouseGesture>();
		gs.add(MouseGesture.ALPHA);
		gs.add(MouseGesture.HORIZONTAL);
		ac.withMouseGestures(gs);

		TaskActivation x = getInputKeyChains(null, 1, ac.build());
		if (x != null) {
			for (MouseGesture g : x.getMouseGestures()) {
				System.out.println(g);
			}
		}
	}

	public static KeyChain getInputKeyChain(JFrame parent, KeyChain prepopulated) {
		Set<KeyChain> prepopulatedSet = new HashSet<>();
		prepopulatedSet.add(prepopulated);
		return getInputKeyChain(parent, prepopulatedSet);
	}

	public static KeyChain getInputKeyChain(JFrame parent, Set<KeyChain> prepopulated) {
		TaskActivation task = getInputKeyChains(
								parent, 1,
								TaskActivation.newBuilder().withHotKeys(prepopulated).build(),
								Mode.KEYCHAIN_ONLY);
		if (task == null) {
			return null;
		}

		Set<KeyChain> keys = task.getHotkeys();
		if (keys != null && keys.size() == 1) {
			return keys.iterator().next();
		}
		return null;
	}

	public static TaskActivation getInputActivation(JFrame parent, TaskActivation prepopulated) {
		inUse.lock();
		try {
			return getInputKeyChains(parent, MAX_KEY_CHAIN, prepopulated);
		} finally {
			inUse.unlock();
		}
	}

	private static TaskActivation getInputKeyChains(JFrame parent, int limit, TaskActivation prepopulated) {
		return getInputKeyChains(parent, limit, prepopulated, Mode.ALL_ACTIVATION);
	}

	/**
	 * Show a panel to prompt user to select an input task activation.
	 *
	 * @param parent parent frame, or null if there is none.
	 * @param limit maximum number of activation entities allowed.
	 * @param prepopulated populate the panel with a set of activation (e.g. existing activation).
	 * @return a new {@link TaskActivation} object representing the user selection.
	 */
	private static TaskActivation getInputKeyChains(JFrame parent, int limit, TaskActivation prepopulated, Mode mode) {
		KeyChainInputPanel input = new KeyChainInputPanel(prepopulated, limit, mode);
		final JOptionPane optionPane = new JOptionPane(input, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

		final JDialog dialog = new JDialog (parent, "Activation input", true);
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
				Set<KeyChain> keyChains = new HashSet<>();
				Enumeration<KeyChain> allKeys = input.modelKeyChain.elements();
				while (allKeys.hasMoreElements()) {
					KeyChain next = allKeys.nextElement();
					if (!next.isEmpty()) {
						keyChains.add(next);
					}
				}

				Set<KeySequence> keySequences = new HashSet<>();
				Enumeration<KeySequence> allSequences = input.modelKeySequence.elements();
				while (allSequences.hasMoreElements()) {
					KeySequence next = allSequences.nextElement();
					if (!next.isEmpty()) {
						keySequences.add(next);
					}
				}

				Set<ActivationPhrase> phrases = new HashSet<>();
				Enumeration<ActivationPhrase> allPhrases = input.modelPhrases.elements();
				while (allPhrases.hasMoreElements()) {
					ActivationPhrase next = allPhrases.nextElement();
					if (!next.isEmpty()) {
						phrases.add(next);
					}
				}

				Set<MouseGesture> gestures = new HashSet<>();
				for (MouseGesture gesture : input.mouseGestureList.getSelectedValuesList()) {
					gestures.add(gesture);
				}

				// Add pending inputs.
				if (!input.keyStrokes.isEmpty()) {
					keyChains.add(new KeyChain(input.keyStrokes));
				}
				if (!input.tfPhrase.getText().isEmpty()) {
					phrases.add(ActivationPhrase.of(input.tfPhrase.getText()));
				}
				return TaskActivation.newBuilder()
						.withHotKeys(keyChains)
						.withKeySequence(keySequences)
						.withPhrases(phrases)
						.withMouseGestures(gestures).build();
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private KeyChainInputPanel(TaskActivation prepopulated, final int limit, Mode mode) {
		keyStrokes = new ArrayList<>();

		final JLabel instruction = new JLabel("Start pressing key chain.");
		final JTextField tfKeySeries = new JTextField();
		tfKeySeries.setEditable(false);

		tfKeySeries.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();
				if (code == Config.HALT_TASK) {
					keyStrokes.clear();
				}

				tfKeySeries.setText(new KeyChain(keyStrokes).toString());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				int location = e.getKeyLocation();
				KeyStroke.Modifier m = KeyStroke.Modifier.KEY_MODIFIER_UNKNOWN;
				if (location == KeyEvent.KEY_LOCATION_LEFT) {
					m = KeyStroke.Modifier.KEY_MODIFIER_LEFT;
				} else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
					m = KeyStroke.Modifier.KEY_MODIFIER_RIGHT;
				}

				keyStrokes.add(KeyStroke.of(code, m));
			}
		});

		/******************************************************************************************/
		modelKeyChain = new DefaultListModel<>();
		for (KeyChain key : prepopulated.getHotkeys()) {
			modelKeyChain.addElement(key);
		}

		final JList<KeyChain> listKeyChain = new JList<>(modelKeyChain);
		listKeyChain.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listKeyChain.setLayoutOrientation(JList.VERTICAL);
		listKeyChain.setVisibleRowCount(-1);

		final JScrollPane scrollPaneKeyChain = new JScrollPane(listKeyChain);
		scrollPaneKeyChain.setPreferredSize(new Dimension(150, 80));

		final JButton bAddKeyChain = new JButton("Add key chain");
		bAddKeyChain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				KeyChain keyChain = new KeyChain(keyStrokes);
				if (!keyChain.isEmpty()) {
					if (modelKeyChain.getSize() >= limit) {
						return;
					}

					modelKeyChain.addElement(keyChain);
					keyStrokes.clear();
					tfKeySeries.setText("");
				}
			}
		});

		listKeyChain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int selected = listKeyChain.getSelectedIndex();
					if (selected >= 0) {
						modelKeyChain.remove(listKeyChain.getSelectedIndex());
					}
				}
			}
		});

		/******************************************************************************************/
		modelKeySequence = new DefaultListModel<>();
		for (KeySequence key : prepopulated.getKeySequences()) {
			modelKeySequence.addElement(key);
		}

		final JList<KeySequence> listKeySequence = new JList<>(modelKeySequence);
		listKeySequence.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listKeySequence.setLayoutOrientation(JList.VERTICAL);
		listKeySequence.setVisibleRowCount(-1);

		final JScrollPane scrollPaneKeySequence = new JScrollPane(listKeySequence);
		scrollPaneKeySequence.setPreferredSize(new Dimension(150, 80));

		final JButton bAddKeySequence = new JButton("Add key sequence");
		bAddKeySequence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				KeySequence keySequence = new KeySequence(keyStrokes);
				if (!keySequence.isEmpty()) {
					if (modelKeySequence.getSize() >= limit) {
						return;
					}

					modelKeySequence.addElement(keySequence);
					keyStrokes.clear();
					tfKeySeries.setText("");
				}
			}
		});

		listKeySequence.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int selected = listKeySequence.getSelectedIndex();
					if (selected >= 0) {
						modelKeySequence.remove(listKeySequence.getSelectedIndex());
					}
				}
			}
		});

		/******************************************************************************************/
		tfPhrase = new JTextField();
		modelPhrases = new DefaultListModel<>();
		for (ActivationPhrase phrase : prepopulated.getPhrases()) {
			modelPhrases.addElement(phrase);
		}

		final JList<ActivationPhrase> listPhrases = new JList<>(modelPhrases);
		listPhrases.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listPhrases.setLayoutOrientation(JList.VERTICAL);
		listPhrases.setVisibleRowCount(-1);

		final JScrollPane scrollPanePhrases = new JScrollPane(listPhrases);
		scrollPanePhrases.setPreferredSize(new Dimension(150, 80));

		final JButton bAddPhrase = new JButton("Add phrase");
		bAddPhrase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String phrase = tfPhrase.getText();
				if (!phrase.isEmpty()) {
					if (modelPhrases.getSize() >= limit) {
						return;
					}

					modelPhrases.addElement(ActivationPhrase.of(phrase));
					tfPhrase.setText("");
				}
			}
		});

		listPhrases.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int selected = listPhrases.getSelectedIndex();
					if (selected >= 0) {
						modelPhrases.remove(listPhrases.getSelectedIndex());
					}
				}
			}
		});

		/******************************************************************************************/
		MouseGesture[] gestures = MouseGesture.enabledGestures().toArray(new MouseGesture[0]);
		mouseGestureList = new JList<>(gestures);
		mouseGestureList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mouseGestureList.setLayoutOrientation(JList.VERTICAL);
		mouseGestureList.setVisibleRowCount(-1);

		for (int i = 0; i < gestures.length; i++) {
			MouseGesture gesture = gestures[i];
			if (prepopulated.getMouseGestures().contains(gesture)) {
				mouseGestureList.addSelectionInterval(i, i);
			}
		}


		final JScrollPane scrollPaneMouseGesture = new JScrollPane(mouseGestureList);
		scrollPaneMouseGesture.setPreferredSize(new Dimension(150, 160));

		/******************************************************************************************/
		JPanel keyChainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel keyChainLabel = new JLabel("Key chain");
		keyChainPanel.add(keyChainLabel);
		final JPanel addKeyChainButtonPanel = new JPanel();
		addKeyChainButtonPanel.setLayout(new BoxLayout(addKeyChainButtonPanel, BoxLayout.X_AXIS));
		addKeyChainButtonPanel.add(bAddKeyChain);
		addKeyChainButtonPanel.add(javax.swing.Box.createHorizontalStrut(10));
		addKeyChainButtonPanel.add(new JLabel("(Select and right click to remove.)"));

		JPanel keySequencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel keySequenceLabel = new JLabel("Key sequence");
		keySequencePanel.add(keySequenceLabel);
		final JPanel addKeySequenceButtonPanel = new JPanel();
		addKeySequenceButtonPanel.setLayout(new BoxLayout(addKeySequenceButtonPanel, BoxLayout.X_AXIS));
		addKeySequenceButtonPanel.add(bAddKeySequence);
		addKeySequenceButtonPanel.add(javax.swing.Box.createHorizontalStrut(10));
		addKeySequenceButtonPanel.add(new JLabel("(Select and right click to remove.)"));

		JPanel phrasePanel = new JPanel();
		phrasePanel.setLayout(new BoxLayout(phrasePanel, BoxLayout.Y_AXIS));
		final JLabel phraseLabel = new JLabel("Phrase");
		phrasePanel.add(phraseLabel);
		phrasePanel.add(javax.swing.Box.createVerticalStrut(5));
		phrasePanel.add(tfPhrase);
		final JPanel addPhraseButtonPanel = new JPanel();
		addPhraseButtonPanel.setLayout(new BoxLayout(addPhraseButtonPanel, BoxLayout.X_AXIS));
		addPhraseButtonPanel.add(bAddPhrase);
		addPhraseButtonPanel.add(javax.swing.Box.createHorizontalStrut(10));
		addPhraseButtonPanel.add(new JLabel("(Select and right click to remove.)"));

		/******************************************************************************************/

		final JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.Y_AXIS));
		basicPanel.add(instruction);
		basicPanel.add(tfKeySeries);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(basicPanel);
		if (mode == Mode.ALL_ACTIVATION || mode == Mode.KEYCHAIN_ONLY) {
			add(keyChainPanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(addKeyChainButtonPanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(scrollPaneKeyChain);
		}
		if (mode == Mode.ALL_ACTIVATION || mode == Mode.KEY_SEQUENCE_ONLY) {
			add(keySequencePanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(addKeySequenceButtonPanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(scrollPaneKeySequence);
		}
		if (mode == Mode.ALL_ACTIVATION || mode == Mode.PHRASE_ONLY) {
			add(javax.swing.Box.createVerticalStrut(20));
			add(phrasePanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(addPhraseButtonPanel);
			add(javax.swing.Box.createVerticalStrut(5));
			add(scrollPanePhrases);
		}
		if (mode == Mode.ALL_ACTIVATION || mode == Mode.MOUSE_GESTURE_ONLY) {
			add(javax.swing.Box.createVerticalStrut(5));
			add(scrollPaneMouseGesture);
		}
	}

	/**
	 * @return whether a panel is being displayed.
	 */
	public static boolean isInUse() {
		return inUse.isLocked();
	}

	private static enum Mode {
		ALL_ACTIVATION,
		KEYCHAIN_ONLY,
		MOUSE_GESTURE_ONLY,
		KEY_SEQUENCE_ONLY,
		PHRASE_ONLY;
	}
}
