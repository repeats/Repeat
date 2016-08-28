using Newtonsoft.Json.Linq;
using Repeat.ipc;
using Repeat.userDefinedAction;
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
            ParamInt.Clear();
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
            Action = "set";
            ParamInt.Clear();
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
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamStrings.Add(nameSpace);
            ParamStrings.Add(name);

            JToken result = SendRequest();
            if (result == null) {
                return null;
            }

            return result.Value<string>();
        }

        public SharedMemoryInstance GetInstance(string nameSpace) {
            return new SharedMemoryInstance(this, nameSpace);
        }
    }
}
