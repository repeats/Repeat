package staticResources;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import core.keyChain.KeyStroke;
import core.languageHandler.Language;
import utilities.FileUtility;


public class PythonResources extends AbstractNativeBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return name.endsWith(".py");
	}

	@Override
	protected boolean generateKeyCode() {
		StringBuilder sb = new StringBuilder();
		Field[] fields = KeyEvent.class.getFields();

		for (Field f : fields) {
			String name = f.getName();
			if (!name.startsWith("VK_")) {
				continue;
			}

			addCodeFromField(sb, f);
		}

		fields = InputEvent.class.getFields();
		for (Field f : fields) {
			String name = f.getName();
			if (!name.startsWith("BUTTON") || !name.endsWith("MASK")) {
				continue;
			}

			addCodeFromField(sb, f);
		}

		for (KeyStroke.Modifier modifier : KeyStroke.Modifier.values()) {
			addCode(sb, modifier.name(), modifier.getValue());
		}

		String keyCodeFilePath = FileUtility.joinPath(getExtractingDest().getAbsolutePath(), "key_code.py");
		return FileUtility.writeToFile(sb.toString().trim(), new File(keyCodeFilePath), false);
	}

	/**
	 * Add a new line of code that defines the field constant with field
	 * name and its integer value.
	 */
	private void addCode(StringBuilder sb, String name, int code) {
		sb.append(name + " = " + code);
		sb.append("\n");
	}

	/**
	 * Add a new line of code that defines the field constant.
	 * The new line of code will be appended to an input string builder.
	 *
	 * @param sb string builder to append the code to.
	 * @param f field containing the information about the constant to add.
	 */
	private void addCodeFromField(StringBuilder sb, Field f) {
		try {
			sb.append(f.getName() + " = " + f.getInt(KeyEvent.class));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		sb.append("\n");
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonResources.class.getName());
	}

	@Override
	protected String getRelativeSourcePath() {
		return "natives/python";
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "python"));
	}

	@Override
	protected Language getName() {
		return Language.PYTHON;
	}

	@Override
	public File getIPCClient() {
		return new File("resources/python/repeat_lib.py");
	}
}
