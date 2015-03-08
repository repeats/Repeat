import time
import subprocess

def mouseMove(x, y):
    command = "java -jar RepeatCli.jar mouse move %s %s" % (x, y)
    subprocess.check_call(command, shell= True)

def mouseMoveBy(x, y):
    command = "java -jar RepeatCli.jar mouse moveBy %s %s" % (x, y)
    subprocess.check_call(command, shell= True)

def mouseClick(button_code):
    command = "java -jar RepeatCli.jar mouse click %s" % button_code
    subprocess.check_call(command, shell= True)

def mousePress(button_code):
    command = "java -jar RepeatCli.jar mouse press %s" % button_code
    subprocess.check_call(command, shell= True)

def mouseRelease(button_code):
    command = "java -jar RepeatCli.jar mouse release %s" % button_code
    subprocess.check_call(command, shell= True)

def keyType(string):
    command = "java -jar RepeatCli.jar key type %s" % string
    subprocess.check_call(command, shell= True)

def keyPress(key_code):
    command = "java -jar RepeatCli.jar key press %s" % key_code
    subprocess.check_call(command, shell= True)

def keyRelease(key_code):
    command = "java -jar RepeatCli.jar key release %s" % key_code
    subprocess.check_call(command, shell= True)

def wait(millisecond):
    time.sleep(millisecond / 1000.0)