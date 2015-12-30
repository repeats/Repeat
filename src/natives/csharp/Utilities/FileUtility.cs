using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.utilities {
    class FileUtility {
        public static string ReadFile(string absolutePath) {
            try {
                using (StreamReader sr = new StreamReader(absolutePath)) {
                    String line = sr.ReadToEnd();
                    return line;
                }
            } catch (Exception e) {
                Console.WriteLine("The file could not be read:");
                Console.WriteLine(e.Message);
                return null;
            }
        }
    }
}
