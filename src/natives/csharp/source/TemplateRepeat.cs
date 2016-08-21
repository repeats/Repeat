using Repeat.IPC;
using Repeat.userDefinedAction;
using System;
using System.Diagnostics;
using System.Collections.Generic;
namespace Repeat.userDefinedAction {
    public class CustomAction : UserDefinedAction {
        public override void Action() {
            SharedMemoryRequest mem = controller.mem;
            MouseRequest mouse = controller.mouse;
            KeyboardRequest key = controller.key;
            ToolRequest tool = controller.tool;
            List<int> invoker = this.invoker;
            //Begin generated code

        }
    }
}