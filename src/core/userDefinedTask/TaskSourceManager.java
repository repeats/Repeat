package core.userDefinedTask;

import java.io.File;
import java.util.logging.Logger;

import utilities.FileUtility;



public class TaskSourceManager {

	private static final Logger LOGGER = Logger.getLogger(TaskSourceManager.class.getName());

	public static boolean submitTask(UserDefinedAction task, String source) {
		String sourceFileName = null;
		if (task.getSourcePath() == null || task.getSourcePath().equals("")) {
			LOGGER.warning("Cannot submit task. No source file found...");
			return false;
		} else {
			sourceFileName = new File(task.getSourcePath()).getName();
		}
		File sourceFile = new File(FileUtility.joinPath("data", "source", task.getCompiler().toString(), sourceFileName));

		if (!FileUtility.writeToFile(source, sourceFile, false)) {
			LOGGER.warning("Unable to write source to file " + sourceFile.getAbsolutePath());
			return false;
		}

		String relativePath = FileUtility.getRelativePwdPath(sourceFile);
		task.setSourcePath(relativePath);
		task.recordSourceHistory(relativePath);
		return true;
	}

	public static boolean removeTask(UserDefinedAction task) {
		File sourceFile = new File(task.getSourcePath());
		return FileUtility.removeFile(sourceFile);
	}

	private TaskSourceManager() {}
}
