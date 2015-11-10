package core.ipcc;

import java.io.IOException;

public interface IIPCService {

	public void start() throws IOException;
	public void stop() throws IOException;

	public boolean isRunning();

	public void setPort(int port);
	public int getPort();
	public String getName();
}
