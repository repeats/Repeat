package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtility {

	private static final Logger LOGGER = Logger.getLogger(ZipUtility.class.getName());

	/**
	 * Zip a file
	 * @param zipFile
	 * @param outputFolder
	 */
	public static void unZipFile(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];

		try {
			// Create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// Get the zip file content
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			// Get the zipped file list entry
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {
				String fileName = zipEntry.getName();
				File newFile = new File(FileUtility.joinPath(outputFolder, fileName));

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fileOutputStream = new FileOutputStream(newFile);

				int length;
				while ((length = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, length);
				}

				fileOutputStream.close();
				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.closeEntry();
			zipInputStream.close();
		} catch (IOException ex) {
			LOGGER.log(Level.WARNING, "Unable to unzip file", ex);
		}
	}

	/**
	 * Zip a directory, output to a .zip file
	 * @param zipDirectory directory to be zipped
	 * @param output path to the output .zip file
	 */
	public static void zipDir(File zipDirectory, File output) {
		zipDir(zipDirectory.getAbsolutePath(), output.getAbsolutePath());
	}

	/**
	 * Zip a directory, output to a .zip file
	 * @param zipDirectory directory to be zipped
	 * @param outputFile path to the output .zip file
	 */
	public static void zipDir(String zipDirectory, String outputFile) {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try {
			fileWriter = new FileOutputStream(outputFile);

			zip = new ZipOutputStream(fileWriter);
			addFolderToZip("", zipDirectory, zip);
			zip.close();
			fileWriter.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to zip file", e);
		}
	}

	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);
		if (folder.list().length == 0) {
			addFileToZip(path, srcFolder, zip, true);
		} else {
			for (String fileName : folder.list()) {
				if (path.equals("")) {
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
				} else {
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
				}
			}
		}
	}

	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws IOException {
		File folder = new File(srcFile);
		if (flag) {
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
		} else {
			if (folder.isDirectory()) {
				addFolderToZip(path, srcFile, zip);
			} else {
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
				in.close();
			}
		}
	}

	private ZipUtility() {}
}