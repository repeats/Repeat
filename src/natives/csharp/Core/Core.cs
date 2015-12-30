using Repeat.core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat {
    public class Core {
        public KeyboardCore Key {
            get; private set;
        }

        public MouseCore Mouse {
            get; private set;
        }

        public void blockingWait(int milliSeconds) {
            System.Threading.Thread.Sleep(milliSeconds);
        }
    }
}
