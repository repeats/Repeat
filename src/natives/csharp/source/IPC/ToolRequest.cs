using Newtonsoft.Json.Linq;
using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class ToolRequest : RequestGenerator {
        public ToolRequest(RepeatClient client) : base(client) {
            this.Type = "action";
            this.Device = "tool";
        }

        public string GetClipboard() {
            Action = "get_clipboard";
            ClearParams();
            JToken result = SendRequest();
            if (result == null) {
                Console.WriteLine("Nope nope nope");
                return null;
            }
            return result.Value<string>();
        }

        public bool SetClipboard(string data) {
            Action = "set_clipboard";
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamStrings.Add(data);
            return SendRequest() == null ? false : true;
        }

        public string Execute(string cmd, string cwd = null) {
            Action = "execute";
            ClearParams();
            ParamStrings.Add(cmd);
            if (cwd != null) {
                ParamStrings.Add(cwd);    
            }

            JToken result = SendRequest();
            if (result == null) {
                Console.WriteLine("Nope nope nope");
                return null;
            }

            return result.Value<string>();
        }
    }
}
