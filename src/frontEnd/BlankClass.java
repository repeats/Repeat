package frontEnd;

import java.io.File;

import utilities.FileUtility;

public class BlankClass {

	public static void main(String[] args) {
		File x = new File(FileUtility.joinPath(System.getProperty("user.dir"), "test"));
		System.out.println(x.getParent());

	}
}
