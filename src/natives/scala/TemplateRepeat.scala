import ipc.RepeatClient

import userDefinedAction.Activation
import userDefinedAction.UserDefinedAction
import java.awt.KeyEvent._

new UserDefinedAction() {
  
  override def execute(c : RepeatClient, activation : Activation) : Unit = {
    val m = c.requests.mouseRequest
    val k = c.requests.keyboardRequest
    val mem = c.requests.sharedMemoryRequest.getInstance("global")
    
    val invoker = activation.hotkeys
    val gesture = activation.mouseGesture
    /* Begin generated code. */
    
  }
}