package utilities.swing;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

@SuppressWarnings("serial")
public class LinedTextArea extends JScrollPane {

	private final JTextArea lines;

	public LinedTextArea(final JTextArea textArea) {
		lines = new JTextArea("1");
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);

		textArea.getDocument().addDocumentListener(new DocumentListener(){
			private String getText() {
				int caretPosition = textArea.getDocument().getLength();
				Element root = textArea.getDocument().getDefaultRootElement();
				StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
				for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++){
					text.append(i + System.getProperty("line.separator"));
				}
				return text.toString();
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

		});

		getViewport().add(textArea);
		setRowHeaderView(lines);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}


}
