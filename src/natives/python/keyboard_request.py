import request_generator


class KeyboardRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(KeyboardRequest, self).__init__(client)
        self.type = 'action'
        self.device = 'keyboard'

    def type(self, keys):
        self.action = 'type'
        self.params = keys
        return self.send_request()

    def type_string(self, strings):
        self.action = 'type_string'
        self.params = strings
        return self.send_request()

    def combination(self, keys):
        self.action = 'combination'
        self.params = keys
        return self.send_request()