using Newtonsoft.Json;
using Repeat.ipc;
using Repeat.utilities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Repeat.IPC {
    abstract class RequestGenerator {
        private const int REQUEST_TIMEOUT = 1;

        private static int id = 1;
        protected RepeatClient client;

        public RequestGenerator(RepeatClient client) {
            this.client = client;
            ParamStrings = new List<string>();
            ParamInt = new List<int>();
        }

        public static int ID {
            get {
                id++;
                return id;
            }
        }

        public string Type { get; set; }
        public string Device { get; set; }
        public string Action { get; set; }
        public List<string> ParamStrings { get; set; }
        public List<int> ParamInt { get; set; }

        protected void ClearParams() {
            ParamInt.Clear();
            ParamStrings.Clear();
        }

        protected string getRequest(out int assignedID) {
            int newID = ID;
            assignedID = newID;

            Action<JsonWriter> addData = delegate(JsonWriter writer) {
                writer.WritePropertyName("type");
                writer.WriteValue(Type);
                writer.WritePropertyName("id");
                writer.WriteValue(newID);
                writer.WritePropertyName("content");
                writer.WriteStartObject();
                writer.WritePropertyName("device");
                writer.WriteValue(Device);
                writer.WritePropertyName("action");
                writer.WriteValue(Action);
                writer.WritePropertyName("parameters");
                writer.WriteStartArray();
                if (ParamStrings.Count > 0) {
                    foreach (string param in ParamStrings) {
                        writer.WriteValue(param);
                    }
                } else {
                    foreach (int param in ParamInt) {
                        writer.WriteValue(param);
                    }
                }
                writer.WriteEndArray();
                writer.WriteEndObject();
            };

            string result = JSONUtilities.getJSON(addData);
            return result;
        }

        protected bool SendRequest(bool blockingWait = true) {
            int assignedID;
            string request = getRequest(out assignedID);

            AutoResetEvent replySignal = null;
            if (blockingWait) {
                replySignal = new AutoResetEvent(false);
                client.synchronizationEvents.Add(assignedID, replySignal);
            }

            client.sendQueue.Enqueue(request);
            client.sendSignal.Set();

            if (blockingWait) {
                bool result = replySignal.WaitOne(REQUEST_TIMEOUT);
                client.synchronizationEvents.Remove(assignedID);
                return result;
            } else {
                return true;
            }
        }
    }
}
