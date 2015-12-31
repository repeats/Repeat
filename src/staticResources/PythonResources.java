package staticResources;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.Function;


public class PythonResources {

	private static final Logger LOGGER = Logger.getLogger(PythonResources.class.getName());
	public static File PYTHON_IPC_CLIENT = new File("resources/python/repeat_lib.py");
	private static File EXTRACTING_DEST = new File(FileUtility.joinPath("resources", "python"));

	public static void extractResources() throws IOException {
		if (!FileUtility.createDirectory(EXTRACTING_DEST.getAbsolutePath())) {
			LOGGER.warning("Failed to extract python resources");
			return;
		}

		final String path = "natives/python";
		FileUtility.extractFromCurrentJar(path, EXTRACTING_DEST, new Function<String, Boolean>() {
			@Override
			public Boolean apply(String name) {
				return correctExtension(name);
			}
		});

		if (!generateKeyCode()) {
			LOGGER.warning("Unable to generate key code");
		}
	}

	private static boolean correctExtension(String name) {
		return name.endsWith(".py");
	}

	private static boolean generateKeyCode() {
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

		String keyCodeFilePath = FileUtility.joinPath(EXTRACTING_DEST.getAbsolutePath(), "key_code.py");
		return FileUtility.writeToFile(sb.toString().trim(), new File(keyCodeFilePath), false);
	}
}
