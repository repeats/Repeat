package core.userDefinedTask.internals;

import java.io.File;

public interface ITools {
	public String getClipboard();
	public boolean setClipboard(String data);
	public String execute(String command);
	public String execute(String command, File cwd);
}
