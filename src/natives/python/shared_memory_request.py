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