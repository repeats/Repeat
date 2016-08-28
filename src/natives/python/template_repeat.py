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

	keys = [] if len(invoker['hotkey']) == 0 else invoker['hotkey'][0]
	gesture = None if len(invoker['mouse_gesture']) == 0 else invoker['mouse_gesture'][0]['name']
	#Begin generated code
