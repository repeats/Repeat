package frontEnd;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class BlankClass {
	public static void main(String[] args) throws IOException {
		Path p  = Paths.get("data\\source\\java\\CC_1429150892613.java");

		System.out.println(p.toFile().getPath());
	}
}