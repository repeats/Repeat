package frontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import utilities.GeneralUtility;

@SuppressWarnings("serial")
public class LogPopupMenu extends JPopupMenu {
	protected final JMenuItem miClear, miCopy;
	private final JTextArea output;

	protected LogPopupMenu(JTextArea output) {
		this.output = output;
		miClear = new JMenuItem("Clear");
		miClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});

		miCopy = new JMenuItem("Copy");
		miCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});

		add(miCopy);
		add(miClear);
	}

	private void clear() {
		output.setText("");
	}

	private void copy() {
		String text = output.getSelectedText();
		if (text == null) {
			text = output.getText();
		}

		GeneralUtility.copyToClipboard(text);
	}
}
