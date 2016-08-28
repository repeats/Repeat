using Repeat.IPC;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.userDefinedAction {
    public class SharedMemoryInstance {

        private SharedMemoryRequest requestGenerator { get; set; }
        private string nameSpace { get; set; }

        public SharedMemoryInstance(SharedMemoryRequest requestGenerator, string nameSpace) {
            this.requestGenerator = requestGenerator;
            this.nameSpace = nameSpace;
        }

        public string GetVar(string name) {
            return requestGenerator.GetVar(nameSpace, name);
        }

        public string SetVar(string name, string value) {
            return requestGenerator.SetVar(nameSpace, name, value);
        }

        public string DelVar(string name) {
            return requestGenerator.DelVar(nameSpace, name);
        }
    }
}
