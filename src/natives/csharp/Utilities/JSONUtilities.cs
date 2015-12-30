using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.utilities {

    class JSONUtilities {
        public static string getJSON(Action<JsonWriter> writeValues) {
            StringBuilder sb = new StringBuilder();
            StringWriter sw = new StringWriter(sb);

            using (JsonWriter writer = new JsonTextWriter(sw)) {
                writer.Formatting = Formatting.Indented;

                writer.WriteStartObject();
                writeValues.Invoke(writer);
                writer.WriteEndObject();
            }
            return sb.ToString();
        }
    }
}
