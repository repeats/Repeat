package core.languageHandler.compiler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import utilities.FileUtility;
import utilities.RandomUtil;
import utilities.StringUtilities;
import utilities.logging.ExceptionUtility;
import core.UserDefinedAction;

public class DynamicJavaCompiler implements DynamicCompiler {

	private static final Logger LOGGER = Logger.getLogger(DynamicJavaCompiler.class.getName());
	private static final String DUMMY_CLASS_NAME_PREFIX = "CC_";
	private static URLClassLoader classLoader;

	private final String[] packageTree;
	private final String className;
	private final String[] classPaths;

	private File home;

	public DynamicJavaCompiler(String className, String[] packageTree, String[] classPaths) {
		this.packageTree = packageTree;
		this.className = className;
		this.classPaths = classPaths;

		home = new File(System.getProperty("java.home"));
	}

	@Override
	public UserDefinedAction compile(String sourceCode) {
		String originalPath = System.getProperty("java.home");
		System.setProperty("java.home", home.getAbsolutePath());

		if (!sourceCode.contains("class " + className)) {
			LOGGER.warning("Cannot find class " + className + " in source code.");
			return null;
		}
		String newClassName = DUMMY_CLASS_NAME_PREFIX + RandomUtil.randomID();
		sourceCode = sourceCode.replaceFirst("class " + className, "class " + newClassName);

		try {
			File compiling = new File(FileUtility.joinPath(FileUtility.joinPath(packageTree), newClassName + ".java"));
	        if (compiling.getParentFile().exists() || compiling.getParentFile().mkdirs()) {
	            try {
	                if (!FileUtility.writeToFile(sourceCode, compiling, false)) {
	                	LOGGER.warning("Cannot write source code to file.");
	                	return null;
	                }

	                /** Compilation Requirements *********************************************************************************************/
	                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	                if (compiler == null) {
	                	LOGGER.warning("No java compiler found. Set class path points to JDK in setting?");
	                	return null;
	                }
	                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8);

	                // This sets up the class path that the compiler will use.
	                // Added the .jar file that contains the [className] interface within in it...
	                List<String> optionList = new ArrayList<String>();
	                optionList.add("-classpath");
	                String paths = System.getProperty("java.class.path");
	                if (classPaths.length > 0) {
	                	 paths += ";" + StringUtilities.join(classPaths, ";");
	                }
	                optionList.add(paths);

	                Iterable<? extends JavaFileObject> compilationUnit
	                        = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(compiling));
	                JavaCompiler.CompilationTask task = compiler.getTask(
	                    null,
	                    fileManager,
	                    diagnostics,
	                    optionList,
	                    null,
	                    compilationUnit);
	                /********************************************************************************************* Compilation Requirements **/
	                if (task.call()) {
	                	if (classLoader != null) {
	                		classLoader.close();
	                	}
	                    classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
	                    System.out.println(new File("./").getAbsolutePath());

	                    Class<?> loadedClass = classLoader.loadClass(StringUtilities.join(packageTree, ".") + "." + newClassName);
	                    Object object = loadedClass.newInstance();

	                    LOGGER.info("Successfully compiled class " + className);
	                    return (UserDefinedAction) object;
	                } else {
	                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
	                    	LOGGER.warning("Error on line " + diagnostic.getLineNumber() +" in " + diagnostic.getSource().toUri() + "\n");
	                    	LOGGER.warning(diagnostic.getMessage(Locale.US));
	                    }
	                }
	                fileManager.close();
	            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
	            	LOGGER.warning(ExceptionUtility.getStackTrace(exp));
	            }
	        }
	        LOGGER.info("Cannot compile class " + className);
	        return null;
		} finally {
			System.setProperty("java.home", originalPath);
		}
	}

	@Override
	public String getName() {
		return "java";
	}

	@Override
	public File getPath() {
		return home.getAbsoluteFile();
	}

	@Override
	public void setPath(File path) {
		home = path;
	}

	@Override
	public String getRunArgs() {
		return "";
	}

	@Override
	public void setRunArgs(String args) {

	}
}
