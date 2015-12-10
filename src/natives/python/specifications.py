

SUCCESS = 'Success'
FAILURE = 'Failure'

server_specifications = {
	'action': {
		'create_task': {
			'params_type' : [basestring]
		},
		'run_task': {
			'params_type' : [int, list]
		},
		'remove_task': {
			'params_type' : [int]
		}
	}
}