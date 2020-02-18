using Repeat.IPC;
using Repeat.userDefinedAction;
using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Threading;

namespace Repeat.userDefinedAction {
    public class CustomAction : UserDefinedAction {
        public override void Action() {
            SharedMemoryInstance mem = controller.mem.GetInstance("global"); // Change the string to change namespace
            MouseRequest mouse = controller.mouse;
            KeyboardRequest key = controller.key;
            ToolRequest tool = controller.tool;
            List<int> invoker = this.activation.hotkeys;
            List<int> keySequence = this.activation.keySequence;
            ActivationVariable var = this.activation.activationVariable;
            string phrase = this.activation.phrase;
            string mouseGesture = this.activation.mouseGesture;
            //Begin generated code

        }
    }
}
