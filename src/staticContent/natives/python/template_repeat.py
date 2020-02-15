import time
from key_code import *

"""
    Refer to API to see how to use repeat_api
    invoker is the hotkey (list of keys) that invoked this action
"""
def action(controller, invoker):
    mem = controller.shared_memory.get_instance('global') # Change string to change namespace
    m = controller.mouse
    k = controller.key
    t = controller.tool

    # k.set_timeout_sec(5) # Optional: set timeout for any of the above component if you think your call takes a long time

    hk = [] if len(invoker['hotkey']) == 0 else invoker['hotkey'][0]
    ks = [] if len(invoker['key_sequence']) == 0 else invoker['key_sequence'][0]
    mg = None if len(invoker['mouse_gesture']) == 0 else invoker['mouse_gesture'][0]['name']
    phrase = None if len(invoker['phrases']) == 0 else invoker['phrases'][0]
    # Begin generated code
