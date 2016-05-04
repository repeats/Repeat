using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class SystemClientRequest : RequestGenerator {

        public SystemClientRequest(RepeatClient client) : base(client) {
            this.Type = "system_client";
            this.Device = "system";
        }

        public bool Identify() {
            Action = "identify";
            ClearParams();

            ParamStrings.Add("C#");
            ParamStrings.Add(client.GetPort().ToString());
            return SendRequest(blockingWait: false) == null ? false : true;
        }
    }
}
