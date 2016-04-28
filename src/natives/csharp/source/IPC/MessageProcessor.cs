using Newtonsoft.Json.Linq;
using Repeat.userDefinedAction;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Repeat.ipc {
    public class MessageProcessor {

        protected internal RepeatClient client;
        private StringBuilder currentMessage;
        private TaskManager taskManager;

        public MessageProcessor(RepeatClient client) {
            this.client = client;
            this.taskManager = new TaskManager(client);
            currentMessage = new StringBuilder();
        }

        private List<string> ExtractMessages(byte[] rawData, int size) {
            List<string> output = new List<string>();
            byte[] temp = new byte[1];

            if (size == 0) {
                return output;
            }

            for (int i = 0; i < size; i++) {
                if (rawData[i] == RepeatClient.REPEAT_DELIMITER) {
                    if (currentMessage.Length > 0) {
                        output.Add(currentMessage.ToString());
                        currentMessage.Clear();
                    }
                } else {
                    temp[0] = rawData[i];
                    string newMessage = System.Text.Encoding.ASCII.GetString(temp);
                    currentMessage.Append(newMessage);
                }
            }
            return output;
        }

        protected internal string internalProcess(string message) {
            JObject parsedMessage = JObject.Parse(message);

            string type = "";
            int id = -1;
            JToken content = null;
            JObject contentObject = null;
            foreach (JProperty property in parsedMessage.Properties()) {
                if (property.Name == "type") {
                    type = (string) property.Value;
                } else if (property.Name == "id") {
                    id = (int) System.Convert.ChangeType(property.Value.ToString(), typeof(int));
                } else if (property.Name == "content") {
                    content = property.Value;
                    contentObject = content.Value<JObject>();
                }
            }

            AutoResetEvent signalling;
            if (client.synchronizationEvents.TryGetValue(id, out signalling)) {
                JToken replyToken = contentObject.GetValue("message");
                //Place the reply object for the client to use
                client.returnedObjects.Add(id, replyToken);

                //After setting the signal client will remove the signal from the sync pool
                signalling.Set();
                return null;
            } else if (type == "task") {
                JObject result = taskManager.ProcessMessage(contentObject);
                JObject replyMessage = new JObject(
                    new JProperty("type", type),
                    new JProperty("id", id),
                    new JProperty("content", result)
                    );
                return replyMessage.ToString();
            } else {
                //Console.WriteLine("Uknown id {0}. Drop message...", id);
                return null;
            }
        }

        public void process(byte[] rawData, int size) {
            List<string> messages = ExtractMessages(rawData, size);
            foreach (string message in messages) {
                Thread worker = new Thread(() => {
                    string result = internalProcess(message);
                    if (result == null) {
                        return;
                    }

                    client.sendQueue.Enqueue(result);
                    client.sendSignal.Set();
                });
                worker.Start();
            }
        }
    }
}
