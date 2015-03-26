import time
import subprocess

def mouse_move(x, y):
    command = "java -jar RepeatCli.jar mouse move %s %s" % (x, y)
    subprocess.check_call(command, shell= True)

def mouse_move_by(x, y):
    command = "java -jar RepeatCli.jar mouse moveBy %s %s" % (x, y)
    subprocess.check_call(command, shell= True)

def mouse_click(button_code):
    command = "java -jar RepeatCli.jar mouse click %s" % button_code
    subprocess.check_call(command, shell= True)

def mouse_press(button_code):
    command = "java -jar RepeatCli.jar mouse press %s" % button_code
    subprocess.check_call(command, shell= True)

def mouse_release(button_code):
    command = "java -jar RepeatCli.jar mouse release %s" % button_code
    subprocess.check_call(command, shell= True)

def key_type(string):
    command = "java -jar RepeatCli.jar key type %s" % string
    subprocess.check_call(command, shell= True)

def key_press(key_code):
    command = "java -jar RepeatCli.jar key press %s" % key_code
    subprocess.check_call(command, shell= True)

def key_release(key_code):
    command = "java -jar RepeatCli.jar key release %s" % key_code
    subprocess.check_call(command, shell= True)

def wait(millisecond):
    time.sleep(millisecond / 1000.0)