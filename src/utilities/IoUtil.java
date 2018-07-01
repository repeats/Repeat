package utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class IoUtil {

	private static final int INPUT_STREAM_BUFFER_SIZE = 64;

	private IoUtil() {}

	/**
	 * Read all content from an input stream to a byte array.
	 */
	public static byte[] streamToBytes(InputStream in) throws IOException {
	    ReadableByteChannel channel = Channels.newChannel(in);
	    ByteBuffer byteBuffer = ByteBuffer.allocate(INPUT_STREAM_BUFFER_SIZE);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    WritableByteChannel outChannel = Channels.newChannel(bout);
	    while (channel.read(byteBuffer) > 0 || byteBuffer.position() > 0) {
	        byteBuffer.flip();  // Make buffer ready for write.
	        outChannel.write(byteBuffer);
	        byteBuffer.compact(); // Make buffer ready for reading.
	    }
	    channel.close();
	    outChannel.close();
	    return bout.toByteArray();
	}
}
