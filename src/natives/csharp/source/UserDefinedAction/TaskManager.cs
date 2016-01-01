using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Repeat.utilities;
using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Repeat.compiler;
using Repeat.ipc;
using log4net;

namespace Repeat.userDefinedAction {
    class TaskManager {

        private static readonly ILog logger = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private const string SUCCESS = "Success";
        private const string FAILURE = "Failure";

        private int idCount;
        private RepeatClient client;
        private CSCompiler compiler;
        private UserDefinedAction emptyAction;
        private Dictionary<int, UserDefinedAction> actions;

        public TaskManager(RepeatClient client) {
            this.client = client;
            this.idCount = 0;
            this.compiler = new CSCompiler(".");
            actions = new Dictionary<int, UserDefinedAction>();
            emptyAction = new EmptyAction("");
        }

        public JObject ProcessMessage(JObject message) {
            string action = "";
            JToken parametersJSON = null;
            foreach (JProperty property in message.Properties()) {
                if (property.Name == "task_action") {
                    action = property.Value.ToString();
                } else if (property.Name == "parameters") {
                    parametersJSON = property.Value;
                }
            }
            JArray parameters = parametersJSON.Value<JArray>();

            if (action == "create_task") {
                string fileName = parameters.Children().First().Value<string>();
                JObject result = CreateTask(fileName);
                return result;
            } else if (action == "run_task") {
                JEnumerable<JToken> parameterList = parameters.Children();
                int taskID = parameterList.First().Value<int>();
                JArray hotKeysJSON = parameterList.Skip(1).First().Value<JArray>();
                List<int> hotkeys = new List<int>();
                foreach (JToken token in hotKeysJSON.Children()) {
                    hotkeys.Add(token.Value<int>());
                }

                return RunTask(taskID, hotkeys);
            } else if (action == "remove_task") {
                int taskID = parameters.Children().First().Value<int>();
                return RemoveTask(taskID);
            } else {
                return GenerateReply(FAILURE, "Unknown action " + action);
            }
        }

        private JObject RunTask(int id, List<int> invoker) {
            UserDefinedAction toDo;
            if (actions.TryGetValue(id, out toDo)) {
                toDo.controller = this.client;
                toDo.invoker = invoker;

                try {
                    toDo.Action();
                } catch (Exception e) {
                    return GenerateReply(FAILURE, "Encountered exception while executing task\n" + e.StackTrace);
                }
                
                return GenerateReply(SUCCESS, GenerateTaskReply(id, toDo));
            } else {
                return GenerateReply(FAILURE, "Unknown action with id " + id);
            }
        }

        private JObject CreateTask(string filePath) {
            if (!File.Exists(filePath)) {
                return GenerateReply(FAILURE, "File " + filePath + " does not exist.");
            } else {
                string sourceCode = FileUtility.ReadFile(filePath);
                if (sourceCode == null) {
                    return GenerateReply(FAILURE, "Unreadable file " + filePath);
                }

                UserDefinedAction action = null;
                try {
                    action = compiler.Compile(sourceCode);
                } catch (Exception e) {
                    logger.Warn("Unable to compile source code\n" + e.StackTrace);
                }
                if (action == null) {
                    return GenerateReply(FAILURE, "Cannot compile file " + filePath);
                } else {
                    idCount++;
                    actions[idCount] = action;
                    action.FileName = filePath;
                    logger.Info("Successfully compiled source code.");
                    return GenerateReply(SUCCESS, GenerateTaskReply(idCount, action));
                }
            }
        }

        private JObject RemoveTask(int id) {
            UserDefinedAction toRemove;
            if (actions.TryGetValue(id, out toRemove)) {
                actions.Remove(id);
                return GenerateReply(SUCCESS, GenerateTaskReply(id, toRemove));
            } else {
                return GenerateReply(SUCCESS, GenerateTaskReply(id, emptyAction));
            }
        }

        private JObject GenerateReply(string status, object message) {
            return new JObject(
                new JProperty("status", status),
                new JProperty("message", message)
                );
        }

        private JObject GenerateTaskReply(int id, UserDefinedAction task) {
            return new JObject(
                new JProperty("id", id), 
                new JProperty("file_name", task.FileName)
                );
        }
    }
}
