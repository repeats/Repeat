using Repeat.compiler;
using System.Runtime.InteropServices;
using System.Threading;
using System;
using log4net;
using log4net.Config;
using Repeat.ipc;
using Newtonsoft.Json;
using System.Text;
using System.IO;
using Repeat.utilities;
using Repeat.userDefinedAction;
using Newtonsoft.Json.Linq;

namespace Repeat
{
    class Program {

        private static readonly ILog logger = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
        /**************************************************************************************************************************************/

        public static void Main(String[] args) {
            var x = new RepeatClient();
            x.StartRunning();
            Console.ReadKey();
            Console.WriteLine("Stopping");
            x.StopRunning();

            Console.ReadKey();
        }

        private static void test() {
            string code2 = 
                "using System;\n" +
                "using System.Diagnostics;\n" +
                "" + 
                "namespace Repeat.userDefinedAction {" + 
                    "public class CustomAction : UserDefinedAction {" +
                        "public override void Action() {" +
                            "Console.WriteLine(\"DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD\");" +
                        "}" +
                    "}" + 
                "}";

            string code3 =
                "using System;\n" +
                "using System.Diagnostics;\n" +
                "" +
                "namespace Repeat.userDefinedAction {" +
                    "public class CustomAction : UserDefinedAction {" +
                        "public override void Action() {" +
                            "Console.WriteLine(\"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE\");" +
                        "}" +
                    "}" +
                "}";

            CSCompiler compiler = new CSCompiler(".");
            UserDefinedAction action1 = compiler.Compile(code2);
            action1.Action();

            UserDefinedAction action2 = compiler.Compile(code3);
            action2.Action();

            action1.Action();
            BasicConfigurator.Configure();
            
            logger.Info("INFO");
            logger.Debug("Debug");
            logger.Warn("WARNING");
            logger.Error("ERROR");

            for (int i = 0; i < 2147483647; i++) {
                logger.Info(System.Windows.Forms.Control.MousePosition.X + ", " + System.Windows.Forms.Control.MousePosition.Y);
                Thread.Sleep(200);
            }

            Console.ReadKey();
        }
    }
}
