
Repeat
======

Full-fledged mouse/keyboard record/replay and sophisticated automation macros/hotkeys creation using modern programming languages, and more advanced automation features. Available across three major OSes: Windows, OSX, and Linux.

[![SourceForge](https://sourceforge.net/sflogo.php?type=11&group_id=3172773)](https://sourceforge.net/projects/repeat1/)
========================================================================================================================

Demo
====

Note that the following gifs are at 1x speedup.
![Word expansion demo](https://raw.githubusercontent.com/repeats/Repeat/master/demo_key_expansion.gif)

![Recording & playback](https://raw.githubusercontent.com/repeats/Repeat/master/demo_record_replay.gif)

![Mouse gesture activation](https://raw.githubusercontent.com/repeats/Repeat/master/demo_gesture.gif)

![Multi clipboard](https://raw.githubusercontent.com/repeats/Repeat/master/demo_multi_clipboard.gif)


[Task creation - All caps](https://youtu.be/wICRVQNVNSM)

[Task creation - Fixing a typo](https://youtu.be/oCCyYbj198U)

[Task creation - Clipboard with history](https://youtu.be/dqNckwIPjCE)

[Playing Collapse 3](https://youtu.be/19i5ZlZvsAc)

[Playing Plants vs Zombies](https://youtu.be/7pQHcFfrpDI)

Features
=======
1. Record and replay computer activities.
2. Store recorded tasks and replay them later.
3. Write your own task **in your favorite text editor** using Python or Java so you have more control over the computer.
4. Assign multiple arbitrary hotkey combinations to activate a stored task.
5. Assign multiple mouse gestures to activate a stored task.
6. Compile and run tasks on a group of remote machines.
7. Manage your Repeat tasks (either recorded or written).

Disclaimer
==========
1. This is not a password storage program. Source code written is not encrypted.
2. This program executes your own code. Use the advanced compile - replay feature carefully. Test your task before adding it to the list.

Requirements
============

JDK 8.0 or above. Both Oracle JDK and OpenJDK are OK.

If you wish to write/run tasks in Python, then Python3 is required.

On Windows, no special permission required.

On Linux, X11 window system. This would not work on Wayland window system.

On OSX, accessibility permission is required for the native hook to work. Enable this in System Preference --> Security & Privacy --> Accessibilty --> Privacy.

Installation
============
Just download the [latest version](https://github.com/repeats/Repeat/releases/latest), put the jar in a **separate** directory, and run it with java. That's it! You may need appropriate privileges since Repeat needs to listen to and/or control the mouse and keyboard.

The recommended way to launch the program is through terminal:

     $cd <jar_directory>
     $java -jar Repeat.jar

**Important:** The path containing the JAR file **must not** have space in it.

Note that since **Java 9**, the jar file must be launched from a JDK (as opposed to a JRE) to be able to compile file. The workaround used in Java 8 and before to set Java home no longer works.

FAQ
===

## What is the difference between this and [AutoHotkey](https://autohotkey.com/) or [AutoKey](https://github.com/autokey/autokey)?
1. This runs on any platform that supports Java and is non [headless](https://en.wikipedia.org/wiki/Headless_software). AutoHotkey is written for Windows only, and AutoKey is only for Linux. Repeat works on Linux, Windows, and OSX. **The written macro can be re-used cross platforms.**
2. The only limit to your hotkey power is your knowledge of the language you write your tasks in (e.g. Java, Python or C#). You don't have to learn a new meta language provided by AutoHotkey. This allows you to leverage your expertise in the language chosen and/or the immense support from the internet.

## Why is this only available in non headless system?
It does not make sense to listen to keyboard and mouse events in a headless system. How can you move your mouse if you have no screen? What would typing a key mean in such system?

## I am running a Linux machine. Will the C# module get started at application start time?
C# module is disabled if you are on a non Windows OS. Nothing from C# module will get started.

## Why can't this be a web service? It's a lot of effort downloading the JAR and run it.
This application listens on your mouse and keyboard events as well as allowing you to simulate mouse/keyboard events. If I could do such thing from a web browser, some hackers must have got your personal information, passwords, bank account number by now.

## Why is there no support for JavaScript?
It's coming. I will get it done when I have time.

Common usage questions
===============

## How do I change the global hotkeys (e.g. run task, start recording, stop recording)?
You can change global hotkeys: In menu Settings --> Hotkey

## How do I start recording?
Record: Press record (default hotkey F9) to start recording and stop at any time by pressing the same button again (or press default recording button F9)

## How do I replay what I just recorded?
Replay: Press replay (default hotkey F11) to start replaying and stop at any time by pressing the same button again (or press default replay button F11)

## How do I compile my code?
Compile your code and play it: the buttons are right under the record and replay section. (F12 for play/stop compiled code)

## What do the buttons on the right hand side mean?
Manage your tasks: using the buttons on the right hand side of the windows to Add, Override, Remove or Reorganize (move up/down, change group) your tasks.

## How do I assign a hotkey for my task?
Change hotkey for your task: just left click on the hotkey column of your task's row in the table and input the new hotkey

## My task is shown as enabled. How do I disable it?
Enabling/disabling task: click on the column in the table that shows enabled/disabled.

## I accidentally activated an infinite loop in my program with a hotkey. How do I stop the program?
By default, pressing escape will interrupt the running tasks. However, you can disable this feature in Setting --> Halt tasks by escape.

## There's a button with text "default". What is it?
It's the button to manage task group.

## How do I manage my task groups?
Manage task group: click on the button next to Task group on the top right corner at any time to manage the task group.

## My Java program won't compile even if I use the default template. What's wrong?
Change Java class path: Settings --> Set compiler path. Choose the directory where your JDK is installed (not JRE). For example, mine is at C:\Program Files\Java\jdk1.8.0_25
More explanation: Usually the jar will get started by JRE. Obviously you can't compile anything with JRE.

## My python code won't load even if I use default template. What's wrong?
Similar to Java, you need to identify where the python interpreter is at. First switch to Python using Tool --> Compiling Language --> Python. Then configure python interpreter path using Settings --> Set compiler path. For example in my Linux machine my python interpreter is at /usr/bin/python

## Are you serious? I still can't load my python code even after I've configured my python interpreter.
You need to start Python submodule first. Go to Tool --> Native modules and click on the python IPC line, then click on the play button to start it.
You can achieve the same thing by just restarting the application. Python module will be automatically started if you've configured python interpreter correctly.

Libraries used
==============
1. [Simple Native Hooks](https://github.com/repeats/SimpleNativeHooks)
2. [Argo JSON](http://argo.sourceforge.net)
3. [Jama - A Java matrix package](https://math.nist.gov/javanumerics/jama/)
4. [Apache HttpComponents Core](https://hc.apache.org/httpcomponents-core-ga/index.html)
5. [Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
6. [FreeMarker Java Template Engine](https://freemarker.apache.org/)
7. [Light Bootstrap Dashboard](https://creative-tim.com/product/light-bootstrap-dashboard)
8. [CodeMirror: a versatile text editor in Javascript](http://codemirror.net/)
