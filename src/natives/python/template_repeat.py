from key_code import *

"""
	Refer to API to see how to use repeat_api
	invoker is the hotkey (list of keys) that invoked this action
"""
def action(controller, invoker):
	mem = controller.shared_memory
	m = controller.mouse
	k = controller.key
	t = controller.tool
	#Begin generated code
