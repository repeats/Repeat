package utilities.logging;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class extends from OutputStream to redirect output to a
 * text area and a {@link StringBuffer}.
 */
public class OutStream extends OutputStream {
    private final LogHolder contentHolder;

    public OutStream(LogHolder contentHolder) {
        this.contentHolder = contentHolder;
    }

    @Override
    public void write(int b) throws IOException {
    	contentHolder.write(b);
	}
}
