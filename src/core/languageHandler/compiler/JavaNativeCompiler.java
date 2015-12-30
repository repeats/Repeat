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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import utilities.FileUtility;
import utilities.Pair;
import utilities.RandomUtil;
import utilities.StringUtilities;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;
import core.userDefinedTask.DormantUserDefinedTask;
import core.userDefinedTask.UserDefinedAction;

public class JavaNativeCompiler extends AbstractNativeCompiler {

	private static URLClassLoader classLoader;

	private final String[] packageTree;
	private final String defaultClassName;
	private String className;
	private final String[] classPaths;

	private File home;

	public JavaNativeCompiler(String className, String[] packageTree, String[] classPaths) {
		this.packageTree = packageTree;
		this.defaultClassName = className;
		this.classPaths = classPaths;

		home = new File(System.getProperty("java.home"));
	}

	@Override
	public Pair<DynamicCompilerOutput, UserDefinedAction> compile(String sourceCode, File classFile) {
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
			Pair<DynamicCompilerOutput, UserDefinedAction> output = loadClass(className);
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
	public Pair<DynamicCompilerOutput, UserDefinedAction> compile(String sourceCode) {
		String originalPath = System.getProperty("java.home");
		System.setProperty("java.home", home.getAbsolutePath());

		if (!sourceCode.contains("class " + defaultClassName)) {
			getLogger().warning("Cannot find class " + defaultClassName + " in source code.");
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.SOURCE_MISSING_PREFORMAT_ELEMENTS, null);
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
	                	return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, new DormantUserDefinedTask(sourceCode));
	                }

	                /** Compilation Requirements *********************************************************************************************/
	                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	                if (compiler == null) {
	                	getLogger().warning("No java compiler found. Set class path points to JDK in setting?");
	                	return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILER_MISSING, new DormantUserDefinedTask(sourceCode));
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
	                	Pair<DynamicCompilerOutput, UserDefinedAction> output = loadClass(newClassName);
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
	        return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_ERROR, null);
		} finally {
			className = null;
			System.setProperty("java.home", originalPath);
		}
	}

	private Pair<DynamicCompilerOutput, UserDefinedAction> loadClass(String loadClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});

        Class<?> loadedClass = classLoader.loadClass(StringUtilities.join(packageTree, ".") + "." + loadClassName);
        Object object = loadedClass.newInstance();

        getLogger().log(Level.FINE, "Successfully loaded class " + loadClassName);
        classLoader.close();
        UserDefinedAction output = (UserDefinedAction) object;
        output.setSourcePath(getSourceFile(loadClassName).getAbsolutePath());
        return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_SUCCESS, output);
	}

	@Override
	protected File getSourceFile(String compileClass) {
		return new File(FileUtility.joinPath(FileUtility.joinPath(packageTree), compileClass + ".java"));
	}

	@Override
	public Language getName() {
		return Language.JAVA;
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
		return Logger.getLogger(JavaNativeCompiler.class.getName());
	}

	/*******************************************************************/
	/************************Swing components***************************/
	/*******************************************************************/

	@Override
	public void promptChangePath(JFrame parent) {
		JFileChooser chooser = new JFileChooser(getPath());

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showDialog(parent, "Set Java home") == JFileChooser.APPROVE_OPTION) {
			setPath(chooser.getSelectedFile());
		}
	}

	@Override
	public void changeCompilationButton(JButton bCompile) {
		bCompile.setText("Compile source");
	}
}
