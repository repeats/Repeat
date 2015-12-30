using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    class KeyboardRequest : RequestGenerator {

        public KeyboardRequest(RepeatClient client) : base(client) {
            this.Type = "action";
            this.Device = "keyboard";
        }

        public bool DoType(params int[] keyCodes) {
            Action = "type";
            ClearParams();
            ParamInt.AddRange(keyCodes);
            return SendRequest();
        }

        public bool DoType(params string[] strings) {
            Action = "type_string";
            ClearParams();
            ParamStrings.AddRange(strings);
            return SendRequest();
        }

        public bool Combination(params int[] keyCodes) {
            Action = "combination";
            ClearParams();
            ParamInt.AddRange(keyCodes);
            return SendRequest();
        }
    }
}
