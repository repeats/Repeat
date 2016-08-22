package utilities;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import sun.misc.Launcher;
import frontEnd.BlankClass;

/**
 * Provide file reading and writing utilities
 * This is a static class. No instance should be created
 * @author HP Truong
 *
 */
public class FileUtility {

	/**
	 * Private constructor so that no instance is created
	 */
	private FileUtility() {
		throw new IllegalStateException("Cannot create an instance of static class FileUtility");
	}

	/**
	 * Check if file exists and is not a directory
	 * @param file file to check
	 * @return if file exists
	 */
	public static boolean fileExists(File file) {
		return file.exists() && !file.isDirectory();
	}

	/**
	 * Split a path into a list of directories ending with the file name
	 * @param path path to split
	 * @return list of directories representing the file path, ending with the file name
	 */
	public static List<String> splitPath(String path) {
		return splitPath(new File(path));
	}

	/**
	 * Split a file into a list of directories ending with the file name
	 * @param file file to split path
	 * @return list of directories representing the file path, ending with the file name
	 */
	public static List<String> splitPath(File file) {
		List<String> output = new ArrayList<>();
		File current = file;
		while (current != null) {
			output.add(current.getName());
			current = current.getParentFile();
		}
		Collections.reverse(output);
		return output;
	}

	/**
	 * Get the file name given a relative or absolute path to the file
	 * Path does not have to exist
	 * @param path path to the file
	 * @return file name (omitting the parent directories)
	 */
	public static String getFileName(String path) {
		List<String> split = splitPath(path);
		return split.get(split.size() - 1);
	}

	/**
	 * Get relative path of a file with respect to a working directory
	 * @param workDirectory the directory that will be reference
	 * @param target file whose path will be compared to that of the directory
	 * @return relative path to the file from the directory. If no relative path exists, provide absolute path to file
	 */
	public static String getRelativePath(File workDirectory, File target) {
		if (target.getAbsolutePath().startsWith(workDirectory.getAbsolutePath())) {
			String relativePath = target.getAbsolutePath().substring(workDirectory.getAbsolutePath().length() + 1);
			relativePath = relativePath.replaceAll(Pattern.quote(File.separator), "/");
			return relativePath;
		} else {
			return target.getAbsolutePath();
		}
	}

	/**
	 * Get relative path of a file with respect to current working directory
	 * @param target file whose path will be compared to that of current working directory
	 * @return relative path to the file from current working directory. If no relative path exists, provide absolute path to file
	 */
	public static String getRelativePwdPath(File target) {
		return getRelativePath(new File(""), target);
	}

	/**
	 * Change the name of the file
	 * @param file file to rename
	 * @param newName new name for the file
	 * @return file with name renamed to newName
	 */
	public static File renameFile(File file, String newName) {
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String newAbsolutePath = absolutePath.substring(0, absolutePath.lastIndexOf(fileName)) + newName;

		return new File(newAbsolutePath);
	}

	/**
	 * Remove the last extension of file. If no extension found then return the input file
	 * E.g. a.out.log --> a.out
	 * a.diff --> a
	 * @param file file to remove extension
	 * @return file with last extension removed
	 */
	public static File removeExtension(File file) {
		String absolutePath = file.getAbsolutePath();
		if (absolutePath.contains(".")) {
			return new File(absolutePath.substring(0, absolutePath.lastIndexOf('.')));
		} else {
			return file;
		}
	}

	/**
	 * Append an extension to a file. If extension does not contain a dot, it will be automatically added
	 * @param file file to add extension
	 * @param extension extension to be add
	 * @return file with extension: fileName.extension
	 */
	public static File addExtension(File file, String extension) {
		if (extension.startsWith(".")) {
			return new File(file.getAbsolutePath() + extension);
		} else {
			return new File(file.getAbsolutePath() + "." + extension);
		}
	}

	/**
	 * Recursively walk through the path and return all found files (not directory)
	 * @param path path to be walked
	 * @return list of files found. This does not include the directory names
	 */
	public static List<File> walk(String path) {
		LinkedList<File> output = new LinkedList<File>();

		File root = new File(path);
		File[] list = root.listFiles();

        if (list == null) {
			return output;
		}

        for (File f : list) {
            if (f.isDirectory()) {
                output.addAll(walk(f.getAbsolutePath()));
            }
            else {
            	output.addLast(f);
            }
        }

        return output;
	}

	/**
	 *
	 * @param sourceDirectory
	 * @param destDirectory
	 * @return
	 */
	public static boolean moveFiles(File sourceDirectory, File destDirectory) {
		if (!sourceDirectory.isDirectory() || !destDirectory.isDirectory()) {
			return false;
		}

		boolean result = true;
		for (File f : sourceDirectory.listFiles()) {
			String name = f.getName();
			result &= f.renameTo(new File(FileUtility.joinPath(destDirectory.getAbsolutePath(), name)));
		}
		return result;
	}

	/**
	 * Copy source file to destination file. Create directory if destination directory does not exist
	 * @param source source file
	 * @param dest destination file
	 * @return if copy succeeded
	 */
	public static boolean copyFile(File source, File dest) {
		File parentDest = dest.getParentFile();

		if (!parentDest.exists()) {
			if (!createDirectory(parentDest.getAbsolutePath())) {
				return false;
			}
		}

		try {
			Files.copy(source.toPath(), dest.toPath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Delete a file or directory. FOLLOW symlink
	 * @param toDelete file to delete
	 * @return if delete successful
	 */
	public static boolean deleteFile(File toDelete) {
		boolean result = true;
		if (toDelete.isDirectory()) {
			for (File c : toDelete.listFiles()) {
				result &= deleteFile(c);
			}
		}

		if (!toDelete.delete()) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, "Cannot delete file " + toDelete.getAbsolutePath());
			return false;
		}

		return result;
	}

	/**
	 * Create directory if not exists
	 * @param directory full path of the directory
	 * @return if creation succeed
	 */
	public static boolean createDirectory(String directory) {
		File theDir = new File(directory);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			boolean result = false;

			try {
				theDir.mkdirs();
				result = true;
			} catch (SecurityException se) {
				se.printStackTrace();
				// handle it
				return false;
			}

			return result;
		} else {
			return true;
		}
	}

	/**
	 * Read a plain text file and process it line by line
	 * @param file file that will be processed
	 * @return void
	 */
	public static void readFromFile(File file, Function<String, Boolean> lineProcessing) {
		FileInputStream fr = null;

		try {
			fr = new FileInputStream(file);
			InputStreamReader char_input = new InputStreamReader(fr, Charset.forName("UTF-8").newDecoder());
			BufferedReader br = new BufferedReader(char_input);

			while (true) {
	            String in = br.readLine();
	            if (in == null) {
	               break;
	            }

	            if (!lineProcessing.apply(in)) {
	            	break;
	            }
	        }

			br.close();
		} catch (IOException e) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, "IOException while reading file", e);
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, "IOException while closing file reader", e);
			}
		}
	}

	/**
	 * Read a plain text file.
	 * @param file file that will be read
	 * @return StringBuffer the read result.
	 */
	public static StringBuffer readFromFile(File file) {
		StringBuffer output = new StringBuffer("");
		FileInputStream fr = null;

		try {
			fr = new FileInputStream(file);

			InputStreamReader char_input = new InputStreamReader(fr, Charset.forName("UTF-8").newDecoder());

			BufferedReader br = new BufferedReader(char_input);

			while (true) {
	            String in = br.readLine();
	            if (in == null) {
	               break;
	            }
	            output.append(in).append("\n");
	        }

			br.close();

		} catch (IOException e) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
			return null;
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}
		return output;
	}

	/**
	 * Read a plain text file.
	 * @param filePath path to the file
	 * @return text content of the file
	 */
	public static StringBuffer readFromFile(String filePath) {
		return readFromFile(new File(filePath));
	}

	/**
	 * Read plain text data from an InputStream
	 * @param inputStream input stream to be read from
	 * @return text data retrieved from stream
	 */
	public static StringBuffer readFromStream(InputStream inputStream) {
		StringBuffer output = new StringBuffer();
		if (inputStream == null) {
			return output;
		}

		InputStreamReader char_input = new InputStreamReader(inputStream, Charset.forName("UTF-8").newDecoder());
		BufferedReader br = new BufferedReader(char_input);
		try {
			while (true) {
	            String in = br.readLine();

	            if (in == null) {
	               break;
	            }
	            output.append(in).append("\n");
	        }
			return output;
		} catch (IOException e) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				char_input.close();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
			}

			try {
				br.close();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		return output;
	}

	/**
	 * Write a content to a file. The file will be created if it does not exist
	 * @param content content that will be written to file using UTF-8 format
	 * @param file target file
	 * @param append will the content be appended to the file or overwritten old data in file (if exists)
	 * @return return if write successfully
	 */
	public static boolean writeToFile(String content, File file, boolean append) {
		if (!fileExists(file)) {
			if (file.getParentFile() != null) {
				if (!createDirectory(file.getParentFile().getAbsolutePath())) {
					return false;
				}
			}

			try {
				file.createNewFile();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
				return false;
			}
		}

		OutputStreamWriter fw = null;
		try {
			fw = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
			Writer bw = new BufferedWriter(fw);


			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true;
	}

	/**
	 * Write a content to a file
	 * @param content content that will be written to file using UTF-8 format
	 * @param file target file
	 * @param append will the content be appended to the file or overwritten old data in file (if exists)
	 * @return return if write successfully
	 */
	public static boolean writeToFile(StringBuffer content, File file, boolean append) {
		OutputStreamWriter fw = null;
		try {
			fw = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
			Writer bw = new BufferedWriter(fw);

			bw.write(content.toString());
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true;
	}

	/**
	 * Remove a file
	 * @param file file to be remove
	 * @return if removal is successful. Throw IOException if encounters error
	 */
	public static boolean removeFile(File file) {
		if (fileExists(file)) {
			return file.delete();
		} else {
			return true;
		}
	}

	/**
	 *
	 * @param destination
	 * @throws IOException
	 */
	public static void extractFromCurrentJar(String path, File destination, Function<String, Boolean> filteringFunction) throws IOException {
		final File jarFile = new File(BlankClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {// Run with JAR file
		    final JarFile jar = new JarFile(jarFile);
		    final Enumeration<JarEntry> entries = jar.entries(); // Gives ALL entries in jar
		    while(entries.hasMoreElements()) {
		    	JarEntry entry = entries.nextElement();
		        String name = entry.getName();
		        if (!name.startsWith(path + "/")) { // Filter according to the path
		        	continue;
		        }

		        if (!filteringFunction.apply(name)) {
		        	continue;
		        }

		        InputStream inputStream = jar.getInputStream(entry);
		        Path destinationPath = Paths.get(FileUtility.joinPath(destination.getAbsolutePath(), FileUtility.getFileName(name)));
		        Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		    }
		    jar.close();
		} else { // Run with IDE
		    final URL url = Launcher.class.getResource("/" + path);
		    if (url != null) {
		        try {
		            final File apps = new File(url.toURI());
		            for (File app : apps.listFiles()) {
		            	if (!filteringFunction.apply(app.getAbsolutePath())) {
				        	continue;
				        }
		                Path destinationPath = Paths.get(FileUtility.joinPath(destination.getAbsolutePath(), app.getName()));
		                Files.copy(app.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		            }
		        } catch (URISyntaxException ex) {
		            // never happens
		        }
		    }
		}
	}

	/**
	 * IO combining two paths
	 * @param path1 first path
	 * @param path2 second path
	 * @return a path created by joining first path and second path
	 */
	private static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	/**
	 * IO combining paths
	 * @param paths array of paths
	 * @return a path created by joining all the paths
	 */
	public static String joinPath(String... paths) {
		if (paths.length == 0) {
			return "";
		} else {
			String output = paths[0];
			for (int i = 1; i < paths.length; i++) {
				output = joinPath(output, paths[i]);
			}
			return output;
		}
	}
}