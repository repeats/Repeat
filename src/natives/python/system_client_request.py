import request_generator


class SystemClientRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(SystemClientRequest, self).__init__(client)
        self.type = 'system_client'
        self.device = 'system'

    def identify(self):
        self.action = 'identify'
        self.params = ['python', str(self.client.socket.getsockname()[1])]
        return self.send_request(blocking_wait = False)