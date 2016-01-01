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
        // Declare the SetConsoleCtrlHandler function
        // as external and receiving a delegate.

        [DllImport("Kernel32")]
        public static extern bool SetConsoleCtrlHandler(HandlerRoutine Handler, bool Add);

        // A delegate type to be used as the handler routine
        // for SetConsoleCtrlHandler.
        public delegate bool HandlerRoutine(CtrlTypes CtrlType);

        // An enumerated type for the control messages
        // sent to the handler routine.
        public enum CtrlTypes {
            CTRL_C_EVENT = 0,
            CTRL_BREAK_EVENT,
            CTRL_CLOSE_EVENT,
            CTRL_LOGOFF_EVENT = 5,
            CTRL_SHUTDOWN_EVENT
        }
        /**************************************************************************************************************************************/
        private static RepeatClient client;

        private static bool ConsoleCtrlCheck(CtrlTypes ctrlType)
        {
            if (client != null) {
                logger.Info("Caught signal. Terminating IPC client...");
                client.StopRunning();
            }
            return true;
        }

        public static void Main(String[] args) {
            SetConsoleCtrlHandler(new HandlerRoutine(ConsoleCtrlCheck), true);
            BasicConfigurator.Configure();
            client = new RepeatClient();
            client.StartRunning();
            logger.Info("Successfully started C# IPC client.");
        }
    }
}
