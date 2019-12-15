import threading
import logging
logger = logging.getLogger(__name__)

class RequestGenerator(object):

    REQUEST_TIMEOUT = 3 #3 seconds timeout
    base_id = 1

    def __init__(self, client):
        super(RequestGenerator, self).__init__()
        self.client = client
        self.timeout_sec = RequestGenerator.REQUEST_TIMEOUT

    @classmethod
    def _gen_id(cls):
        RequestGenerator.base_id += 1
        return RequestGenerator.base_id

    def set_timeout_sec(self, timeout_sec):
        self.timeout_sec = timeout_sec

    def get_request(self, new_id):
        return {
                'type' : self.type,
                'id' : new_id,
                'content' : {
                            'device': self.device,
                            'action': self.action,
                            'parameters' : self.params
                    }
                }

    def send_request(self, blocking_wait = True, timeout_sec = None):
        if self.client is None or self.client.synchronization_objects is None or self.client.send_queue is None:
            return None

        new_id = RequestGenerator._gen_id()

        event = threading.Event()
        events_pool = self.client.synchronization_objects

        events_pool[new_id] = event

        sending = self.get_request(new_id)
        self.client.send_queue.put(sending)

        if blocking_wait and not event.wait(timeout_sec if timeout_sec is not None else self.timeout_sec):
            logger.warning("Timeout for this request id {0}".format(new_id))
            return None

        if new_id in events_pool and events_pool[new_id] is not event:
            returned_object = events_pool.pop(new_id)
            return returned_object
        else:
            return None
