package utilities.logging;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * This class extends from OutputStream to redirect output to a
 * text area and a {@link StringBuffer}.
 */
public class OutStream extends OutputStream {
    private final JTextArea textArea;
    private final StringBuffer contentHolder;

    public OutStream(JTextArea textArea, StringBuffer contentHolder) {
        this.textArea = textArea;
        this.contentHolder = contentHolder;
    }

    @Override
    public void write(int b) throws IOException {
    	String s = String.valueOf((char)b);
    	contentHolder.append(s);

        textArea.append(s);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
