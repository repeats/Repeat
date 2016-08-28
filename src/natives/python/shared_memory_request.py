import request_generator


class SharedMemoryRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(SharedMemoryRequest, self).__init__(client)
        self.type = 'shared_memory'
        self.device = 'shared_memory'

    def get(self, namespace, variable_name):
        self.action = 'get'
        self.params = [namespace, variable_name]
        return self.send_request()

    def set(self, namespace, variable_name, value):
        self.action = 'set'
        self.params = [namespace, variable_name, str(value)]
        return self.send_request()

    def delete(self, namespace, variable_name):
        self.action = 'del'
        self.params = [namespace, variable_name]
        return self.send_request()

    def get_instance(self, namespace):
        return SharedMemoryInstance(self, namespace)

class SharedMemoryInstance(object):
    """
        A small class to conveniently call SharedMemoryRequest class with fixed parameters.

    """
    def __init__(self, shared_memory_request, namespace):
        super(SharedMemoryInstance, self).__init__()
        self.shared_memory_request = shared_memory_request
        self.namespace = namespace

    def get(self, variable_name):
        return self.shared_memory_request.get(self.namespace, variable_name)

    def set(self, variable_name, value):
        return self.shared_memory_request.set(self.namespace, variable_name, value)

    def delete(self, variable_name):
        return self.shared_memory_request.delete(self.namespace, variable_name)
