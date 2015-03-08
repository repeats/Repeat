package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaHandler extends Handler {

	private final JTextArea area;

	public TextAreaHandler(JTextArea area) {
		this.area = area;
	}

	@Override
	public void publish(final LogRecord record) {
		System.out.println("here");
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StringWriter text = new StringWriter();
                PrintWriter out = new PrintWriter(text);
                out.println(area.getText());
                out.printf(getFormatter().format(record));
                area.setText(text.toString());
            }

        });
	}

	@Override
	public void flush() {

	}

	@Override
	public void close() throws SecurityException {

	}
}
