package frontEnd;

import utilities.JSONUtility;
import argo.jdom.JsonRootNode;

public class BlankClass {
	public static void main(String[] args) {

		String x = "{\"status\": {\"file_name\": \"D\", \"id\": 1}}";

		JsonRootNode y = JSONUtility.jsonFromString(x);
		System.out.println(y.getStringValue("status"));
	}
}