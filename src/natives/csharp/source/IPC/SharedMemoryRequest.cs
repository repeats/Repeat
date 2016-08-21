using Newtonsoft.Json.Linq;
using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class SharedMemoryRequest : RequestGenerator {

        public SharedMemoryRequest(RepeatClient client) : base(client) {
            this.Type = "shared_memory";
            this.Device = "shared_memory";
        }

        public string GetVar(string nameSpace, string name) {
            Action = "get";
            ParamStrings.Clear();
            ParamStrings.Add(nameSpace);
            ParamStrings.Add(name);

            JToken result = SendRequest();
            if (result == null) {
                return null;
            }

            return result.Value<string>();
        }

        public string SetVar(string nameSpace, string name, string value) {
            Action = "del";
            ParamStrings.Clear();
            ParamStrings.Add(nameSpace);
            ParamStrings.Add(name);
            ParamStrings.Add(value);

            JToken result = SendRequest();
            if (result == null) {
                return null;
            }

            return result.Value<string>();
        }

        public string DelVar(string nameSpace, string name) {
            Action = "del";
            ParamStrings.Clear();
            ParamStrings.Add(nameSpace);
            ParamStrings.Add(name);

            JToken result = SendRequest();
            if (result == null) {
                return null;
            }

            return result.Value<string>();
        }
    }
}
