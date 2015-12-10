package staticResources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import sun.misc.Launcher;
import utilities.FileUtility;
import frontEnd.BlankClass;


public class PythonResources {

	public static File PYTHON_IPC_CLIENT = new File("resources/python/repeat_lib.py");
	private static File EXTRACTING_DEST = new File(FileUtility.joinPath("resources", "python"));

	public static void extractResources() throws IOException {
		if (!FileUtility.createDirectory(EXTRACTING_DEST.getAbsolutePath())) {
			System.out.println("Failed to extract python resources");
			return;
		}

		final String path = "natives/python";
		final File jarFile = new File(BlankClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {// Run with JAR file
		    final JarFile jar = new JarFile(jarFile);
		    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
		    while(entries.hasMoreElements()) {
		    	JarEntry entry = entries.nextElement();
		        String name = entry.getName();
		        if (!name.startsWith(path + "/")) { //filter according to the path
		        	continue;
		        }
		        if (!correctExtension(name)) {
		        	continue;
		        }

		        InputStream inputStream = jar.getInputStream(entry);
		        Path destination = Paths.get(FileUtility.joinPath(EXTRACTING_DEST.getAbsolutePath(), FileUtility.getFileName(name)));
		        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
		    }
		    jar.close();
		} else { // Run with IDE
		    final URL url = Launcher.class.getResource("/" + path);
		    if (url != null) {
		        try {
		            final File apps = new File(url.toURI());
		            for (File app : apps.listFiles()) {
		            	if (!correctExtension(app.getAbsolutePath())) {
		            		continue;
		            	}
		                Path destination = Paths.get(FileUtility.joinPath(EXTRACTING_DEST.getAbsolutePath(), app.getName()));
		                Files.copy(app.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
		            }
		        } catch (URISyntaxException ex) {
		            // never happens
		        }
		    }
		}
	}

	private static boolean correctExtension(String name) {
		return name.endsWith(".py");
	}
}
