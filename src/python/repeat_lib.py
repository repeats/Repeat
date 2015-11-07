import socket
import json
import threading
import Queue

import time

class RequestGenerator(object):
    def __init__(self, client_request_queue):
        super(RequestGenerator, self).__init__()
        self.client_request_queue = client_request_queue

    def get_request(self):
        return {
                'actions' : [
                        {
                            'device': self.device,
                            'action': self.action,
                            'params' : self.params
                        }
                    ]
                }

    def send_request(self):
        if self.client_request_queue is not None:
            self.client_request_queue.put(self.get_request())

class MouseRequest(RequestGenerator):
    def __init__(self, client_request_queue):
        super(MouseRequest, self).__init__(client_request_queue)
        self.device = 'mouse'

    def left_click(self, x = None, y = None):
        self.action = 'leftClick'
        if x is None or y is None:
            self.params = []
        else:
            self.params = [x, y]

        return self.send_request()

    def right_click(self, x = None, y = None):
        self.action = 'rightClick'
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
        self.action = 'moveBy'
        self.params = [x,y]
        return self.send_request()

class KeyboardRequest(RequestGenerator):
    def __init__(self, client_request_queue):
        super(KeyboardRequest, self).__init__(client_request_queue)
        self.device = 'keyboard'

    def type(self, keys):
        self.action = 'type'
        self.params = keys
        return self.send_request()

    def type_string(self, strings):
        self.action = 'typeString'
        self.params = strings
        return self.send_request()

    def combination(self, keys):
        self.action = 'combination'
        self.params = keys
        return self.send_request()

class SystemRequest(RequestGenerator):
    def __init__(self, client_request_queue):
        super(SystemRequest, self).__init__(client_request_queue)
        self.device = 'system'
        
    def keep_alive(self):
        self.action = 'keepAlive'
        self.params = []
        return self.send_request()


class RepeatClient(object):
    """Server will terminate connection if not received anything after this period of time"""
    REPEAT_SERVER_TIMEOUT_SEC = 1

    """Client must send keep alive message to maintain the connection with server.
    Therefore the client timeout has to be less than server timeout"""
    REPEAT_CLIENT_TIMEOUT_SEC = REPEAT_SERVER_TIMEOUT_SEC * 0.8 

    def __init__(self, host = 'localhost', port = 9999):
        super(RepeatClient, self).__init__()
        self.host = host
        self.port = port
        self.socket = None

        self.queue = Queue.Queue()

        self.system = SystemRequest(self.queue)
        self.mouse = MouseRequest(self.queue)
        self.key = KeyboardRequest(self.queue)

    def _clear_queue(self):
        while not self.queue.empty():
            self.queue.get()

    def start(self):
        self._clear_queue()

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((self.host, self.port))
        print "Successfully started python client"

    def stop(self):
        self._clear_queue()
        self.socket.close()

    def process(self):
        while True:
            data = None
            try:
                data = self.queue.get(block = True, timeout = RepeatClient.REPEAT_CLIENT_TIMEOUT_SEC)
            except Queue.Empty as e:
                pass

            keep_alive = data is None
            if keep_alive:
                data = self.system.keep_alive()                
            else:
                self.socket.sendall(json.dumps(data))
                data = self.socket.recv(1024)

if __name__ == "__main__":
    client = RepeatClient()
    
    def client_process():
        client.start()
        client.process()
        client.stop()

    client_thread = threading.Thread(target = client_process)
    client_thread.start()

    time.sleep(2)
    print "Passed sleep"
    dd = KeyboardRequest()
    client.queue.put(dd.type_string(['ddd', 'eee']))

    print "Done"