using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class SystemHostRequest : RequestGenerator {

        public SystemHostRequest(RepeatClient client) : base(client) {
            this.Type = "system_host";
            this.Device = "system";
        }

        public bool KeepAlive() {
            Action = "keep_alive";
            ClearParams();

            return SendRequest(blockingWait: false) == null ? false : true;
        }
    }
}
