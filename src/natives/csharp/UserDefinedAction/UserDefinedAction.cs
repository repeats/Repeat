using System;

namespace Repeat.userDefinedAction {
    public abstract class UserDefinedAction {
        protected Core Controller { get; set; }
        public string FileName { get; set; }

        abstract public void Action();
    }
}
