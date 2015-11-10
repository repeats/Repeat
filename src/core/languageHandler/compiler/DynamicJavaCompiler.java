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
import java.util.logging.Level;
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
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Languages;
import core.userDefinedTask.UserDefinedAction;

public class DynamicJavaCompiler extends AbstractNativeDynamicCompiler {

	private static URLClassLoader classLoader;

	private final String[] packageTree;
	private final String defaultClassName;
	private String className;
	private final String[] classPaths;

	private File home;

	public DynamicJavaCompiler(String className, String[] packageTree, String[] classPaths) {
		this.packageTree = packageTree;
		this.defaultClassName = className;
		this.classPaths = classPaths;

		home = new File(System.getProperty("java.home"));
	}

	@Override
	public UserDefinedAction compile(String sourceCode, File classFile) {
		className = FileUtility.removeExtension(classFile).getName();

		if (!classFile.getParentFile().getAbsolutePath().equals(new File(FileUtility.joinPath(packageTree)).getAbsolutePath())) {
			getLogger().warning("Class file " + classFile.getAbsolutePath() + "is not consistent with packageTree");
		} else if (!classFile.getName().endsWith(".class")) {
			getLogger().warning("Java class file " + classFile.getAbsolutePath() + " does not end with .class. Compiling using source code");
			return compile(sourceCode);
		} else if (!FileUtility.fileExists(classFile)) {
			getLogger().warning("Cannot find file " + classFile.getAbsolutePath() + ". Compiling using source code");
			return compile(sourceCode);
		}

		try {
			UserDefinedAction output =  loadClass(className);
			getLogger().info("Skipped compilation and loaded object file.");
			className = null;
			return output;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			getLogger().log(Level.WARNING, "Cannot load class file " + classFile.getAbsolutePath(), e);
			getLogger().info("Compiling using source code");
			return compile(sourceCode);
		}
	}

	@Override
	public UserDefinedAction compile(String sourceCode) {
		String originalPath = System.getProperty("java.home");
		System.setProperty("java.home", home.getAbsolutePath());

		if (!sourceCode.contains("class " + defaultClassName)) {
			getLogger().warning("Cannot find class " + defaultClassName + " in source code.");
			return null;
		}


		String newClassName = className;
		if (newClassName == null) {
			newClassName = getDummyPrefix() + RandomUtil.randomID();
		}
		sourceCode = sourceCode.replaceFirst("class " + defaultClassName, "class " + newClassName);

		try {
			File compiling = getSourceFile(newClassName);
	        if (compiling.getParentFile().exists() || compiling.getParentFile().mkdirs()) {
	            try {
	                if (!FileUtility.writeToFile(sourceCode, compiling, false)) {
	                	getLogger().warning("Cannot write source code to file.");
	                	return null;
	                }

	                /** Compilation Requirements *********************************************************************************************/
	                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	                if (compiler == null) {
	                	getLogger().warning("No java compiler found. Set class path points to JDK in setting?");
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
	                	UserDefinedAction output = loadClass(newClassName);
	                	getLogger().info("Successfully compiled class " + defaultClassName);
                		return output;
	                } else {
	                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
	                    	getLogger().warning("Error on line " + diagnostic.getLineNumber() +" in " + diagnostic.getSource().toUri() + "\n");
	                    	getLogger().warning(diagnostic.getMessage(Locale.US));
	                    }
	                }
	                fileManager.close();
	            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
	            	getLogger().log(Level.WARNING, "Error during compilation...", exp);
	            }
	        }
	        getLogger().warning("Cannot compile class " + defaultClassName);
	        return null;
		} finally {
			className = null;
			System.setProperty("java.home", originalPath);
		}
	}

	private UserDefinedAction loadClass(String loadClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});

        Class<?> loadedClass = classLoader.loadClass(StringUtilities.join(packageTree, ".") + "." + loadClassName);
        Object object = loadedClass.newInstance();

//        LOGGER.info("Successfully loaded class " + loadClassName);
        classLoader.close();
        UserDefinedAction output = (UserDefinedAction) object;
        output.setSourcePath(getSourceFile(loadClassName).getAbsolutePath());
        return output;
	}

	@Override
	protected File getSourceFile(String compileClass) {
		return new File(FileUtility.joinPath(FileUtility.joinPath(packageTree), compileClass + ".java"));
	}

	@Override
	public String getName() {
		return Languages.JAVA.toString();
	}

	@Override
	public String getExtension() {
		return ".java";
	}

	@Override
	public String getObjectExtension() {
		return ".class";
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
	public boolean parseCompilerSpecificArgs(JsonNode node) {
		return true;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		return JsonNodeFactories.object();
	}

	@Override
	protected String getDummyPrefix() {
		return "CC_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(DynamicJavaCompiler.class.getName());
	}
}
