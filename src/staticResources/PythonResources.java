package staticResources;

import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import utilities.FileUtility;
import core.languageHandler.Language;


public class PythonResources extends AbstractNativeBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return name.endsWith(".py");
	}

	@Override
	protected boolean generateKeyCode() {
		StringBuilder sb = new StringBuilder();
		Field[] fields = KeyEvent.class.getFields();
		for (Field f : fields) {
			String name = f.getName();
			if (!name.startsWith("VK_")) {
				continue;
			}

			try {
				sb.append(name + " = " + f.getInt(KeyEvent.class));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append("\n");
		}

		String keyCodeFilePath = FileUtility.joinPath(getExtractingDest().getAbsolutePath(), "key_code.py");
		return FileUtility.writeToFile(sb.toString().trim(), new File(keyCodeFilePath), false);
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonResources.class.getName());
	}

	@Override
	protected String getRelativeSourcePath() {
		return "natives/python";
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "python"));
	}

	@Override
	protected Language getName() {
		return Language.PYTHON;
	}

	@Override
	public File getIPCClient() {
		return new File("resources/python/repeat_lib.py");
	}
}
