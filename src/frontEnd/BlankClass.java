package frontEnd;

import java.io.IOException;
import java.io.InputStream;

import utilities.FileUtility;
import frontEnd.graphics.BootStrapResources;


public class BlankClass {
	public static void main(String[] args) throws IOException {
		InputStream t = BootStrapResources.class.getResourceAsStream("/core/languageHandler/API/JavaAPI.txt");
		System.out.println(t);

		String a = FileUtility.readFromStream(t).toString();
		System.out.println(a);
	}
}