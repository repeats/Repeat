using Microsoft.CSharp;
using Repeat.utilities;
using Repeat.userDefinedAction;
using System;
using System.CodeDom.Compiler;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.compiler {

    public class CSCompiler {
        private const string NameSpace = "Repeat.userDefinedAction";
        private const string MethodName = "Action";
        private const string DefaultOptions = "/optimize /unsafe";

        private string _options;
        private string binaryDirectory;

        public string Options {
            get { return _options; }
            set { _options = value; }
        }

        public CSCompiler(string binaryDirectory) {
            _options = DefaultOptions;
            this.binaryDirectory = binaryDirectory;
        }

        public UserDefinedAction Compile(string sourceCode, string assemblyPath) {
            if (File.Exists(assemblyPath)) {

            }

            throw new NotSupportedException("Not supported yet");
        }

        public UserDefinedAction Compile(string sourceCode) {
            string fullClassName = String.Join(".", NameSpace, "CustomAction");

            CompilerParameters CompilerParams = new CompilerParameters();
            //CompilerParams.GenerateInMemory = true; //Why enabling this does not allow us to do the cast at the end? Different assembly?
            CompilerParams.TreatWarningsAsErrors = false;
            CompilerParams.GenerateExecutable = false;
            CompilerParams.CompilerOptions = this.Options;

            string[] references = { "System.dll", "mscorlib.dll", Assembly.GetExecutingAssembly().Location };
            CompilerParams.ReferencedAssemblies.AddRange(references);

            CSharpCodeProvider provider = new CSharpCodeProvider();
            CompilerResults compileResult = provider.CompileAssemblyFromSource(CompilerParams, sourceCode);

            if (compileResult.Errors.HasErrors) {
                StringBuilder text = new StringBuilder("Compile error: ");
                foreach (CompilerError ce in compileResult.Errors) {
                    text.Append("rn" + ce.ToString());
                }
                throw new ArgumentException(text.ToString());
            }

            Module module = compileResult.CompiledAssembly.GetModules()[0];
            Type methodType = null;
            MethodInfo methodInfo = null;
            if (module != null) {
                methodType = module.GetType(fullClassName);
            }

            if (methodType == null) {
                throw new ArgumentException("No such type " + fullClassName);
            }
            methodInfo = methodType.GetMethod(MethodName);

            if (methodInfo == null) {
                throw new ArgumentException("No such method " + MethodName + " for type " + fullClassName);
            }

            try {
                UserDefinedAction instance = (UserDefinedAction)Activator.CreateInstance(methodType);
                return instance;
            } catch (Exception e) {
                throw e;
            }
        }

    }
}
