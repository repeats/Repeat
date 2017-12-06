package core.languageHandler.compiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;
import core.userDefinedTask.DormantUserDefinedTask;
import core.userDefinedTask.UserDefinedAction;
import utilities.FileUtility;
import utilities.Function;
import utilities.JSONUtility;
import utilities.Pair;
import utilities.RandomUtil;
import utilities.StringUtilities;
import utilities.swing.SwingUtil;

public class JavaNativeCompiler extends AbstractNativeCompiler {

	private DynamicClassLoader classLoader;

	private final String[] packageTree;
	private final String defaultClassName;
	private String className;
	private String[] classPaths;

	private File home;

	public JavaNativeCompiler(String className, String[] packageTree, String[] classPaths) {
		this.packageTree = packageTree;
		this.defaultClassName = className;
		this.classPaths = classPaths;

		classLoader = new DynamicClassLoader(getClassPaths(), ClassLoader.getSystemClassLoader());
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
		// This no longer works in JDK 9 (but why?).
		// We are forced to run the program in JDK in order to be
		// able to retrieve the compiler.
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
		classLoader.addURL(new File("./").toURI().toURL());
        Class<?> loadedClass = classLoader.loadClass(StringUtilities.join(packageTree, ".") + "." + loadClassName);
        Object object = null;
		try {
			object = loadedClass.getDeclaredConstructor().newInstance();
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			getLogger().log(Level.WARNING, "Unable to create a new instance...", e);
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.CONSTRUCTOR_ERROR, null);
		}

        getLogger().log(Level.FINE, "Successfully loaded class " + loadClassName);
        UserDefinedAction output = (UserDefinedAction) object;
        output.setSourcePath(getSourceFile(loadClassName).getAbsolutePath());
        return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_SUCCESS, output);
	}

	/**
	 * Construct an array of URLs from array of string representing list of
	 * class paths.
	 *
	 * @return array of URLs representing the class paths.
	 */
	private URL[] getClassPaths() {
		List<URL> output = new ArrayList<>();
		for (String path : classPaths) {
			try {
				output.add(new File(path).toURI().toURL());
			} catch (MalformedURLException e) {
				getLogger().log(Level.WARNING, "Unable to construct URL for classpath " + path, e);
			}
		}

		return output.toArray(new URL[output.size()]);
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
		if (!node.isArrayNode("classpath")) {
			return false;
		}

		List<String> paths = new ArrayList<>();
		JSONUtility.addAllJson(node.getArrayNode("classpath"), new Function<JsonNode, String>(){
			@Override
			public String apply(JsonNode d) {
				return d.getStringValue().toString();
			}
		}, paths);
		// Override current class paths
		classPaths = paths.toArray(classPaths);
		try {
			applyClassPath();
		} catch (MalformedURLException | NoSuchMethodException | SecurityException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			getLogger().log(Level.WARNING, "Unable to apply class path.", e);
			return false;
		}

		return true;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		List<JsonNode> paths = new ArrayList<>(classPaths.length);
		for (String path : classPaths) {
			paths.add(JsonNodeFactories.string(path));
		}

		return JsonNodeFactories.object(JsonNodeFactories.field("classpath", JsonNodeFactories.array(paths)));
	}

	@Override
	protected String getDummyPrefix() {
		return "CC_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(JavaNativeCompiler.class.getName());
	}

	/**
	 * Add all {@link #classPaths} on the current list of classpath to this compiler class loader.
	 * All classes compiled by this compiler will therefore be able to load all classes in {@link #classpaths}.
	 * There is a limitation that this contaminate the classpath in the class loader used by this compiler
	 * since the compiler reuses the class loader for all compilation task.
	 *
	 * Alternatively, we could spawn a temporary class loader for each compilation task and not close it after loading
	 * so that the compiled task can load classes. However, it is not sure whether Java garbage collection can recycle
	 * this temporary class loader once the compiled task is discarded.
	 *
	 * @throws MalformedURLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void applyClassPath() throws MalformedURLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Hacky reflection solution to alter the global classpath.
		// This no longer works for JDK 9 since system class loader is no longer a URLClassLoader.
		// JDK 9 also emits warnings as reflection package tries to access addURL method.
//		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
//	    method.setAccessible(true);
//	    for (String path : classPaths) {
//	    	method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{new File(path).toURI().toURL()});
//	    }

		// Add all URL to existing class loader.
		for (URL url : getClassPaths()) {
			classLoader.addURL(url);
		}
	}

	/**
	 * Since code loaded by this class loader is user written (hopefully),
	 * exposing addURL should not be a concern.
	 */
	private static final class DynamicClassLoader extends URLClassLoader {
		public DynamicClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}

		/**
		 * Note that adding a path multiple times is fine since underlying
		 * implementation treats it as no-op.
		 */
		@Override
		protected void addURL(URL url) {
			super.addURL(url);
		}
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

	@Override
	public void configure() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("It is advisable to restart the program if you remove a path from the class path list."));
		panel.add(new JLabel("Class paths (1 path per row)"));
		panel.add(new JLabel("E.g. D:\\path\\to\\dir\\the_jar.jar"));

		JTextArea texts = new JTextArea(StringUtilities.join(classPaths, "\n"));
		JScrollPane scrollPane = new JScrollPane(texts);
		texts.setRows(10);
		texts.setColumns(80);
		panel.add(scrollPane);

		if (!SwingUtil.DialogUtil.genericInput("Configure Java compiler", panel)) {
			return;
		}

		String[] paths = texts.getText().split("\n");
		ArrayList<String> validPaths = new ArrayList<>();
		for (String path : paths) {
			if (path.trim().isEmpty()) {
				continue;
			}

			if (Files.isReadable(Paths.get(path))) {
				validPaths.add(path);
			} else {
				getLogger().log(Level.WARNING, "The path " + path + " is not readable.");
				return;
			}
		}

		classPaths = new String[validPaths.size()];
		for (int i = 0; i < validPaths.size(); i++) {
			classPaths[i] = validPaths.get(i);
		}

		try {
			applyClassPath();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Unable to configure the new classpath.", e);
		}
	}
}
