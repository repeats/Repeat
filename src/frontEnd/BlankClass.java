package frontEnd;

import java.io.File;

import utilities.FileUtility;




public class BlankClass {

	public static void main(String[] args) {
		for (File f : FileUtility.walk("data/source")) {
			System.out.println(f.getAbsolutePath());
		}
	}

	public static abstract class Test<K,V extends Exception> {
		public abstract K go() throws V;
	}
}