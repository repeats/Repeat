import request_generator

class MouseRequest(request_generator.RequestGenerator):
    def __init__(self, client):
        super(MouseRequest, self).__init__(client)
        self.type = 'action'
        self.device = 'mouse'

    def left_click(self, x = None, y = None):
        self.action = 'left_lick'
        if x is None or y is None:
            self.params = []
        else:
            self.params = [x, y]

        return self.send_request()

    def right_click(self, x = None, y = None):
        self.action = 'right_click'
        if x is None or y is None:
            self.params = []
        else:
            self.params = [x, y]

        return self.send_request()

    def move(self, x, y):
        self.action = 'move'
        self.params = [x,y]
        return self.send_request()

    def move_by(self, x, y):
        self.action = 'move_by'
        self.params = [x,y]
        return self.send_request()

    def drag(self, x1, y1, x2, y2):
        self.action = 'drag'
        self.params = [x1, y1, x2, y2]
        return self.send_request()

    def drag_by(self, x, y):
        self.action = 'drag_by'
        self.params = [x,y]
        return self.send_request()

    def get_position(self):
        self.action = 'get_position'
        self.params = []
        return self.send_request()

    def get_color(self, x = None, y = None):
        self.action = 'get_color'
        if x is None or y is None:
            self.params = []
        else:
            self.params = [x, y]

        return self.send_request()