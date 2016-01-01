using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class MouseRequest : RequestGenerator {

        public MouseRequest(RepeatClient client) : base(client) {
            this.Type = "action";
            this.Device = "mouse";
        }

        public bool LeftClick() {
            Action = "left_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            return SendRequest();
        }

        public bool RightClick() {
            Action = "right_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            return SendRequest();
        }

        public bool LeftClick(int x, int y) {
            Action = "left_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest();
        }

        public bool RightClick(int x, int y) {
            Action = "right_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest();
        }

        public bool Move(int x, int y) {
            Action = "move";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest();
        }

        public bool MoveBy(int x, int y) {
            Action = "move_by";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest();
        }
    }
}
