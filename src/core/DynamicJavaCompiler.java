package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

import utilities.ExceptionUtility;
import utilities.FileUtility;
import utilities.StringUtilities;

public class DynamicJavaCompiler implements DynamicCompiler {

	private static final Logger LOGGER = Logger.getLogger(DynamicJavaCompiler.class.getName());
	private static URLClassLoader classLoader;

	private final String[] packageTree;
	private final String className;
	private final String[] classPaths;

	static {
		LOGGER.setLevel(Level.ALL);
	}

	public DynamicJavaCompiler(String className, String[] packageTree, String[] classPaths) {
		this.packageTree = packageTree;
		this.className = className;
		this.classPaths = classPaths;
	}

	@Override
	public UserDefinedAction compile(String sourceCode) {
		File compiling = new File(FileUtility.joinPath(FileUtility.joinPath(packageTree), className + ".java"));
        if (compiling.getParentFile().exists() || compiling.getParentFile().mkdirs()) {
            try {
                Writer writer = null;
                try {
                    writer = new FileWriter(compiling);
                    writer.write(sourceCode);
                    writer.flush();
                } finally {
                    try {
                        writer.close();
                    } catch (Exception e) {
                    }
                }

                /** Compilation Requirements *********************************************************************************************/
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8);

                // This sets up the class path that the compiler will use.
                // I've added the .jar file that contains the DoStuff interface within in it...
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
                    Class<?> loadedClass = classLoader.loadClass(StringUtilities.join(packageTree, ".") + "." + className);
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
	}
}
