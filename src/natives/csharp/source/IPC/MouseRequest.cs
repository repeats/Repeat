using Newtonsoft.Json.Linq;
using Repeat.ipc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repeat.IPC {
    public class MouseRequest : RequestGenerator {

        public MouseRequest(RepeatClient client) : base(client) {
            this.Type = "action";
            this.Device = "mouse";
        }

        public bool LeftClick() {
            Action = "left_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            return SendRequest() == null ? false : true;
        }

        public bool RightClick() {
            Action = "right_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            return SendRequest() == null ? false : true;
        }

        public bool LeftClick(int x, int y) {
            Action = "left_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest() == null ? false : true;
        }

        public bool RightClick(int x, int y) {
            Action = "right_click";
            ParamInt.Clear();
            ParamStrings.Clear();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest() == null ? false : true;
        }

        public bool Move(int x, int y) {
            Action = "move";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest() == null ? false : true;
        }

        public bool MoveBy(int x, int y) {
            Action = "move_by";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest() == null ? false : true;
        }

        /// <summary>
        /// Drag mouse from a point to another point.
        /// This will move mouse to the start point, hold left click, then move mouse from the start point to the end point,
        /// then release the left click.
        /// </summary>
        /// <param name="x1"> x coordinate of the start point </param>
        /// <param name="y1"> y coordinate of the start point </param>
        /// <param name="x2"> x coordinate of the end point</param>
        /// <param name="y2"> y coordinate of the end point </param>
        /// <returns> true if success else false </returns>
        public bool Drag(int x1, int y1, int x2, int y2) {
            Action = "drag";
            ClearParams();
            ParamInt.Add(x1);
            ParamInt.Add(y1);
            ParamInt.Add(x2);
            ParamInt.Add(y2);

            return SendRequest() == null ? false : true;
        }

        /// <summary>
        /// Drag mouse from a point to another point.
        /// This will move mouse to the start point, hold left click, then move mouse from the start point to the end point,
        /// then release the left click.
        /// </summary>
        /// <param name="x"> amount to drag in x direction </param>
        /// <param name="y"> amount to drag in y direction </param>
        /// <returns> true if success else false </returns>
        public bool DragBy(int x, int y) {
            Action = "drag_by";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);

            return SendRequest() == null ? false : true;
        }


        /// <summary>
        /// Get the current position of the mouse.
        /// </summary>
        /// <returns>A tuple of two integers representing x and y position of the mouse.</returns>
        public Tuple<int, int> GetPosition() {
            Action = "get_position";
            ClearParams();
            JToken result = SendRequest();
            if (result == null) {
                Console.WriteLine("Nope nope nope");
                return null;
            }
            JArray output = result.Value<JArray>();
            if (output.Count != 2) {
                return null;
            }

            return new Tuple<int, int>(output[0].Value<int>(), output[1].Value<int>());
        }

        /// <summary>
        /// Get color of the pixel at the current position of the mouse pointer.
        /// </summary>
        /// <returns>A tuple of three integers (RGB) </returns>
        public Tuple<int, int, int> GetColor() {
            Action = "get_color";
            ClearParams();
            JToken result = SendRequest();
            if (result == null) {
                return null;
            }
            JArray output = result.Value<JArray>();
            if (output.Count != 3) {
                return null;
            }

            return new Tuple<int, int, int>(output[0].Value<int>(), output[1].Value<int>(), output[2].Value<int>());
        }

        /// <summary>
        /// Get color of the pixel at a specific pixel location on screen.
        /// </summary>
        /// <param name="x"> x coordinate of the pixel </param>
        /// <param name="y"> y coordinate of the pixel </param>
        /// <returns>A tuple of three integers (RGB) </returns>
        public Tuple<int, int, int> GetColor(int x, int y) {
            Action = "get_color";
            ClearParams();
            ParamInt.Add(x);
            ParamInt.Add(y);
            JToken result = SendRequest();

            if (result == null) {
                return null;
            }
            JArray output = result.Value<JArray>();
            if (output.Count != 3) {
                return null;
            }

            return new Tuple<int, int, int>(output[0].Value<int>(), output[1].Value<int>(), output[2].Value<int>());
        }
    }
}
