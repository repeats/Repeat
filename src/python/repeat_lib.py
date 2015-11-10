import json
import os
import traceback
import time
import imp

import socket
import threading
import Queue


import specifications
import keyboard_request
import mouse_request
import system_host_request
import system_client_request


class RepeatClient(object):
    """Server will terminate connection if not received anything after this period of time"""
    REPEAT_SERVER_TIMEOUT_SEC = 10

    """Client must send keep alive message to maintain the connection with server.
    Therefore the client timeout has to be less than server timeout"""
    REPEAT_CLIENT_TIMEOUT_SEC = REPEAT_SERVER_TIMEOUT_SEC * 0.8 

    def __init__(self, host = 'localhost', port = 9999):
        super(RepeatClient, self).__init__()
        self.host = host
        self.port = port
        self.socket = None

        self.synchronization_events = {}
        self.send_queue = Queue.Queue()
        self.task_manager = TaskManager(self)

        self.system = system_host_request.SystemHostRequest(self)
        self.system_client = system_client_request.SystemClientRequest(self)
        self.mouse = mouse_request.MouseRequest(self)
        self.key = keyboard_request.KeyboardRequest(self)

    def _clear_queue(self):
        while not self.send_queue.empty():
            self.send_queue.get()

    def start(self):
        self._clear_queue()

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((self.host, self.port))
        
        self.system_client.identify()
        print "Successfully started python client"

    def stop(self):
        self._clear_queue()
        self.socket.close()

    def process_write(self):
        while True:
            data = None
            try:
                data = self.send_queue.get(block = True, timeout = RepeatClient.REPEAT_CLIENT_TIMEOUT_SEC)
            except Queue.Empty as e:
                pass

            keep_alive = data is None
            if keep_alive:
                self.system.keep_alive()
            else:
                self.socket.sendall(json.dumps(data))

    def process_read(self):
        while True:
            data = None
            try:
                data = self.socket.recv(1024)
            except socket.error as se:
                print traceback.format_exc()
                break
            except Exception as e:
                print traceback.format_exc()

            if data is not None and len(data.strip()) > 0:
                try:
                    parsed = json.loads(data)
                    message_type = parsed['type']
                    message_id = parsed['id']
                    message_content = parsed['content']

                    if message_id in self.synchronization_events:
                        cv = self.synchronization_events.pop(message_id)
                        cv.set()
                    else:
                        if message_type == 'task':
                            reply = self.task_manager.process_message(message_id, message_content)
                            if reply is not None:
                                self.send_queue.put({
                                        'type' : message_type,
                                        'id' : message_id,
                                        'content' : reply
                                    })
                        else:
                            print "Unknown id %s. Drop message..." % message_id


                except Exception as e:
                    print traceback.format_exc()


##############################################################################################################################
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
        self.executing_module = None

    """
        invoker is the hotkey that invoke this action
    """
    def run(self, invoker):
        print "Running task with file name %s" % self.file_name
        parent_dir = os.path.dirname(self.file_name)
        raw_file_name = os.path.basename(self.file_name)
        raw_file_name = os.path.splitext(raw_file_name)[0] #Remove file extension

        if self.executing_module is None:
            self.executing_module = imp.load_source(raw_file_name, self.file_name)
        self.executing_module.action(self.repeat_lib, invoker)

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

    def process_message(self, message_id, message):
        action = message['task_action']
        params = message['params']

        if action == 'create_task':
            return self.create_task(*params)
        elif action == 'run_task':
            return self.run_task(*params)
        elif action == 'remove_task':
            return self.remove_task(*params)

        return None

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
##############################################################################################################################

if __name__ == "__main__":
    client = RepeatClient()

    client.start()

    write_thread = threading.Thread(target=client.process_write)
    read_thread = threading.Thread(target=client.process_read)

    write_thread.start()
    read_thread.start()

    # time.sleep(2.5)
    # print "Starting"
    # dd = keyboard_request.KeyboardRequest(client)
    # dd.type_string(['aaa', 'bbb'])
