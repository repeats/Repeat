package frontEnd;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class BlankClass {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		Field[] fields = KeyEvent.class.getFields();
		for (Field f : fields) {
			String name = f.getName();
			if (!name.startsWith("VK_")) {
				continue;
			}

			sb.append(name + " = " + f.getInt(KeyEvent.class));
			sb.append("\n");
		}

		System.out.println(sb.toString().trim());
	}
}