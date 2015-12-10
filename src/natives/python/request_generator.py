import threading

class RequestGenerator(object):

    REQUEST_TIMEOUT = 1 #1 second timeout
    base_id = 1

    def __init__(self, client):
        super(RequestGenerator, self).__init__()
        self.client = client

    @classmethod
    def _gen_id(cls):
        RequestGenerator.base_id += 1
        return RequestGenerator.base_id

    def get_request(self, new_id):
        return {
                'type' : self.type,
                'id' : new_id,
                'content' : {
                            'device': self.device,
                            'action': self.action,
                            'params' : self.params
                    }
                }

    def send_request(self, blocking_wait = True):
        if self.client is None or self.client.synchronization_events is None or self.client.send_queue is None:
            return False

        new_id = RequestGenerator._gen_id()
        
        event = threading.Event()
        self.client.synchronization_events[new_id] = event

        self.client.send_queue.put(self.get_request(new_id))

        if blocking_wait:
            if not event.wait(RequestGenerator.REQUEST_TIMEOUT):
                print "Timeout for this request"
                return False

        return True
