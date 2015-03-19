package utilities;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provide file reading and writing utilities
 * This is a static class. No instance should be created
 * @author VDa
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
	 * @param f file to check
	 * @return if file exists
	 */
	public static boolean fileExists(File f) {
		return f.exists() && !f.isDirectory();
	}

	/**
	 * Copy source file to destination file
	 * @param source source file
	 * @param dest destination file
	 * @return if copy succeeded
	 */
	public static boolean copyFile(File source, File dest) {
		File parentDest = dest.getParentFile();
		System.out.println(parentDest.getAbsolutePath());
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
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
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
		} finally {
			try {
				fr.close();
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
			if (!createDirectory(file.getParentFile().getAbsolutePath())) {
				return false;
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
	 * IO combining two paths
	 * @param path1 first path
	 * @param path2 second path
	 * @return a path created by joining first path and second path
	 */
	public static String joinPath(String path1, String path2) {
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