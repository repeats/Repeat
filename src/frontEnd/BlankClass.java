package frontEnd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

public class BlankClass {
	public static void main(String[] args) {
		String cmd = "cmd.exe /c echo hello";

		try {
			StringBuffer output = new StringBuffer();
			String line;
			ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();

	    } catch (Exception err) {
	    	System.out.println("Failed");
	    	err.printStackTrace();
	    }
	}
}