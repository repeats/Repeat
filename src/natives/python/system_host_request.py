import request_generator


class SystemHostRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(SystemHostRequest, self).__init__(client)
        self.type = 'system_host'
        self.device = 'system'
        
    def keep_alive(self):
        self.action = 'keep_alive'
        self.params = []
        return self.send_request(blocking_wait = False)