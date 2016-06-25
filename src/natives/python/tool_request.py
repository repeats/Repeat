import request_generator


class ToolRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(ToolRequest, self).__init__(client)
        self.type = 'action'
        self.device = 'tool'

    def get_clipboard(self):
        self.action = 'get_clipboard'
        self.params = []
        return self.send_request()

    def set_clipboard(self, data):
        self.action = 'set_clipboard'
        self.params = [data]
        return self.send_request()

    def execute(self, cmd, cwd = None):
        self.action = 'execute'
        self.params = [cmd]
        if cwd is not None:
            self.params.append(cwd)

        return self.send_request()
