package utilities.swing;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class LinedTextArea extends JScrollPane {

	private static final Logger LOGGER = Logger.getLogger(LinedTextArea.class.getName());
	private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	private final JTextArea lines;

	public LinedTextArea(final JTextArea textArea) {
		lines = new JTextArea("1");
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);

		final UndoManager undoManager = new UndoManager();
		Document document = textArea.getDocument();
		document.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        textArea.getActionMap().put("Undo", new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException e) {
                	LOGGER.log(Level.WARNING, "Unable to undo", e);
                }
            }
        });
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, MASK), "Undo");
        textArea.getActionMap().put("Redo", new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException e) {
                	LOGGER.log(Level.WARNING, "Unable to redo", e);
                }
            }
        });
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,MASK), "Redo");

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

		AbstractAction tabAction = new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				// Use four spaces instead of tab
				textArea.replaceSelection("    ");
			}
		};
		KeyStroke tabKey = KeyStroke.getKeyStroke("TAB");
		textArea.getInputMap().put(tabKey, tabAction);

		getViewport().add(textArea);
		setRowHeaderView(lines);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}


}
