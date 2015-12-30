using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.core {
    public class KeyboardCore {
        public void type(string typing) {

        }

        public void type(char c) {

        }

        public void type(int key) {

        }

        public void type(params int[] keys) {

        }

        public void combination(params int[] keys) {

        }

        public void hold(int key, int duration) {

        }

        public void press(int key) {

        }

        public void press(params int[] keys) {
            foreach (int key in keys) {
                press(key);
            }
        }

        public void release(int key) {

        }

        public void release(params int[] keys) {
            foreach (int key in keys) {
                release(key);
            }
        }
    }
}
