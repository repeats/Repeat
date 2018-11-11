package utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class IoUtil {

	private static final int INPUT_STREAM_BUFFER_SIZE = 64;

	private IoUtil() {}

	/**
	 * Read all content from an input stream to a byte array.
	 * This does not close the input channel after reading. The caller
	 * is responsible for doing that.
	 */
	public static byte[] streamToBytes(InputStream in) throws IOException {
	    ReadableByteChannel channel = Channels.newChannel(in);
	    ByteBuffer byteBuffer = ByteBuffer.allocate(INPUT_STREAM_BUFFER_SIZE);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    WritableByteChannel outChannel = Channels.newChannel(bout);
	    while (channel.read(byteBuffer) > 0 || byteBuffer.position() > 0) {
            // Casting is necessary here.
            // Similar fix in https://jira.mongodb.org/browse/JAVA-2559
            // https://community.blynk.cc/t/java-error-on-remote-server-startup/17957/7
	    	((Buffer) byteBuffer).flip();  // Make buffer ready for write.
	        outChannel.write(byteBuffer);
	        byteBuffer.compact(); // Make buffer ready for reading.
	    }
	    outChannel.close();
	    return bout.toByteArray();
	}
}
