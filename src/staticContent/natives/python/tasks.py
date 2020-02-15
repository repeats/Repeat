import imp
import os
import uuid
import logging
logger = logging.getLogger(__name__)

import specifications


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
        logger.info("Running task with file name %s" % self.file_name)
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

    def _next_id(self):
        return str(uuid.uuid4())

    def process_message(self, message_id, message):
        action = message['task_action']
        params = message['parameters']

        if action == 'create_task':
            return self.create_task(*params)
        elif action == 'run_task':
            return self.run_task(*params)
        elif action == 'remove_task':
            return self.remove_task(*params)

        return None

    def sync_tasks(self):
        pass

    def _generate_reply(self, status, message):
        return {
            'status' : status,
            'message' : message,
            'is_reply_message': True,
        }

    def create_task(self, file_name):
        if not os.path.isfile(file_name):
            return self._generate_reply(specifications.FAILURE, 'File %s does not exist' % file_name)
        elif not os.access(file_name, os.R_OK):
            return self._generate_reply(specifications.FAILURE, 'File %s is not executable' % file_name)

        next_id = self._next_id()
        self.tasks[next_id] = UserDefinedTask(self.repeat_lib, file_name)
        logger.info("Created task with ID %s" % next_id)

        return self._generate_reply(specifications.SUCCESS, {
                'id' : next_id,
                'file_name' : file_name
            })

    def run_task(self, task_id, invoker):
        if task_id not in self.tasks:
            return self._generate_reply(specifications.FAILURE, 'Cannot find task id %s' % task_id)
        self.tasks[task_id].run(invoker)
        return self._generate_reply(specifications.SUCCESS, {
                'id' : task_id,
                'file_name' : self.tasks[task_id].file_name
            })

    def remove_task(self, task_id):
        if task_id not in self.tasks:
            return self._generate_reply(specifications.SUCCESS, {
                    'id' : task_id,
                    'file_name' : ''
                })

        removing = self.tasks.pop(task_id)
        logger.info('Removed task with ID {}.'.format(task_id))
        return self._generate_reply(specifications.SUCCESS, {
                'id' : task_id,
                'file_name' : removing.file_name
            })