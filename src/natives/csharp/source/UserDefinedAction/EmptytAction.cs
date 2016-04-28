using Repeat.userDefinedAction;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.userDefinedAction {
    class EmptyAction : UserDefinedAction {

        public EmptyAction(string fileName) {
            this.FileName = FileName;
        }

        public override void Action() {
 	        //Intentionally left blank
        }
    }
}
