using Repeat.ipc;
using System;
using System.Collections.Generic;

namespace Repeat.userDefinedAction {
    public abstract class UserDefinedAction {

        public RepeatClient controller { protected get; set; }

        public List<int> invoker { protected get; set; }

        public Activation activation { protected get; set; }

        public string FileName { get; set; }

        abstract public void Action();
    }
}
