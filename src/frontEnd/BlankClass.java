package frontEnd;

import java.io.File;


public class BlankClass {
	public static void main(String[] args) {
		File f = new File("data/source/test.java");
		System.out.println(f.getAbsolutePath());
		System.out.println(f.getName());
	}
}