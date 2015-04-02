package core;

import java.io.File;

import utilities.FileUtility;
import utilities.RandomUtil;


public class TaskSourceManager {

	public boolean submitTask(UserDefinedAction task, String source) {
		String fileName = generateName();

		File sourceFile = new File(FileUtility.joinPath("data", "source", task.getCompiler(), fileName));

		if (!FileUtility.writeToFile(source, sourceFile, false)) {
			return false;
		}

		task.setSourcePath(sourceFile.getAbsolutePath());
		return true;
	}

	public boolean removeTask(UserDefinedAction task) {
		File sourceFile = new File(task.getSourcePath());
		return FileUtility.removeFile(sourceFile);
	}

	private String generateName() {
		return RandomUtil.randomID();
	}
}
