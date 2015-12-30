package staticResources;

import java.io.File;
import java.io.IOException;

import utilities.FileUtility;
import utilities.Function;


public class PythonResources {

	public static File PYTHON_IPC_CLIENT = new File("resources/python/repeat_lib.py");
	private static File EXTRACTING_DEST = new File(FileUtility.joinPath("resources", "python"));

	public static void extractResources() throws IOException {
		if (!FileUtility.createDirectory(EXTRACTING_DEST.getAbsolutePath())) {
			System.out.println("Failed to extract python resources");
			return;
		}

		final String path = "natives/python";
		FileUtility.extractFromCurrentJar(path, EXTRACTING_DEST, new Function<String, Boolean>() {
			@Override
			public Boolean apply(String name) {
				return correctExtension(name);
			}
		});
	}

	private static boolean correctExtension(String name) {
		return name.endsWith(".py");
	}
}
