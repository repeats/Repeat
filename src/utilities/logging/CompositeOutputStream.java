package utilities.logging;

import java.io.IOException;
import java.io.OutputStream;

public class CompositeOutputStream extends OutputStream {

	private OutputStream out1, out2;

	public static CompositeOutputStream of(OutputStream out1, OutputStream out2) {
		return new CompositeOutputStream(out1, out2);
	}

	private CompositeOutputStream(OutputStream out1, OutputStream out2) {
		this.out1 = out1;
		this.out2 = out2;
	}

	@Override
	public void write(int b) throws IOException {
		out1.write(b);
		out2.write(b);
	}
}
