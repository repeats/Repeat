import socket
import traceback
import json
import threading
import os
import imp

import specifications
import repeat_lib as repeat_client

def generate_reply(status, message):
    return {
        'status' : status,
        'message' : message
    }

class UserDefinedTask(object):
    def __init__(self, repeat_lib, file_name):
        super(UserDefinedTask, self).__init__()
        self.file_name = file_name
        self.repeat_lib = repeat_lib

    """
        invoker is the hotkey that invoke this action
    """
    def run(self, invoker):
        print "Running task with file name %s" % self.file_name
        parent_dir = os.path.dirname(self.file_name)
        raw_file_name = os.path.basename(self.file_name)
        raw_file_name = os.path.splitext(raw_file_name)[0] #Remove file extension

        executing = imp.load_source(raw_file_name, self.file_name)
        executing.action(self.repeat_lib, invoker)

class TaskManager(object):
    def __init__(self, repeat_lib):
        super(TaskManager, self).__init__()
        assert repeat_lib is not None
        self.repeat_lib = repeat_lib
        self.tasks = {}
        self.base_id = 0

    def _next_id(self):
        self.base_id += 1
        return self.base_id

    def sync_tasks(self):
        pass

    def create_task(self, file_name):
        if not os.path.isfile(file_name):
            return generate_reply(specifications.FAILURE, 'File %s does not exist' % file_name)
        elif not os.access(file_name, os.X_OK):
            return generate_reply(specifications.FAILURE, 'File %s is not executable' % file_name)

        next_id = self._next_id()
        self.tasks[next_id] = UserDefinedTask(self.repeat_lib, file_name)

        return generate_reply(specifications.SUCCESS, {
                'id' : next_id,
                'file_name' : file_name
            })

    def run_task(self, task_id, hotkeys):
        if task_id not in self.tasks:
            return generate_reply(specifications.FAILURE, 'Cannot find task id %s' % task_id)
        self.tasks[task_id].run(hotkeys)
        return generate_reply(specifications.SUCCESS, {
                'id' : task_id,
                'file_name' : self.tasks[task_id].file_name
            })

    def remove_task(self, task_id):
        if task_id not in self.tasks:
            return generate_reply(specifications.SUCCESS, {
                    'id' : task_id,
                    'file_name' : ''
                })

        removing = self.tasks.pop(task_id)
        return generate_reply(specifications.SUCCESS, {
                'id' : task_id,
                'file_name' : removing.file_name
            })

class PythonIPCServer(object):

    DEFAULT_TIMEOUT_SEC = 1

    def __init__(self, repeat_lib, host = 'localhost', port = 9998, timeout = DEFAULT_TIMEOUT_SEC):
        super(PythonIPCServer, self).__init__()
        self.host = host
        self.port = port
        self.timeout = timeout
        self.empty_count_down = 5

        self.socket = None
        self.client = None
        self.address = None

        self.task_manager = TaskManager(repeat_lib)

    def start(self):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((self.host, self.port))
        self.socket.listen(0)

        print "Waiting for client..."
        self.client, self.address = self.socket.accept()
        print "Accepting client %s %s" % (self.client, self.address)
        self.socket.settimeout(self.timeout)
        self.empty_count_down = 5

        
    def process(self):
        print "Accepted client %s at address %s" % (self.client, self.address)

        while True:
            data = None
            try:
                data = self.client.recv(1024)
            except socket.timeout:
                print "Terminating connection due to socket timeout"
                break

            if data is not None and len(data) > 0:
                try:
                    request = json.loads(data)

                    print "Processing request %s" % request
                    reply = self._process_request(request)
                    reply = json.dumps(reply)

                    print "Sending back %s" % reply
                    self.client.sendall(reply)
                except Exception as e:
                    print traceback.format_exc()
                    print "Sending reply..."

                    message = generate_reply(specifications.FAILURE, traceback.format_exc())
                    self.client.sendall(json.dumps(message))
            else:
                self.empty_count_down -= 1
                if self.empty_count_down == 0:
                    print "Receiving too many empty packets. Terminating connection"
                    break

    def _verify_request(self, request):
        assert 'action' in request
        assert 'params' in request

        action = request['action']
        params = request['params']

        assert type(params) is list
        assert action in specifications.server_specifications['action']

        action_specification = specifications.server_specifications['action'][action]['params_type']
        assert len(params) == len(action_specification)

        for index, param in enumerate(params):
            assert isinstance(param, action_specification[index])

    def _process_request(self, request):
        self._verify_request(request)

        action = request['action']
        params = request['params']

        if action == 'create_task':
            return self.task_manager.create_task(*params)
        elif action == 'run_task':
            return self.task_manager.run_task(*params)
        elif action == 'remove_task':
            return self.task_manager.remove_task(*params)
        else:
            print "Unknown action"
            return generate_reply(specifications.FAILURE, 'Unknown action')

    def stop(self):
        if self.client is not None:
            self.client.close()

        if self.socket is not None:
            self.socket.close()

if __name__ == "__main__":
    client = repeat_client.RepeatClient()
    server = PythonIPCServer(client)
    

    def server_process():
        server.start()
        server.process()
        server.stop()

    def client_process():
        client.start()
        client.process()
        client.stop()


    server_thread = threading.Thread(target = server_process)
    client_thread = threading.Thread(target = client_process)

    server_thread.start()
    import time
    time.sleep(5)

    print "Starting client..."
    client_thread.start()