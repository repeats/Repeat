Repeat
======

Tired of clicking the same spot on your screen again and again? Got bored of typing the same phrase over and over again during your day? Repeat yourself with some intelligence.

Demo
====
[Task creation](https://youtu.be/AwkZt7mqLSg)

[Playing Collapse 3](https://youtu.be/19i5ZlZvsAc)

[Playing Plants vs Zombies](https://youtu.be/7pQHcFfrpDI)

Features
=======
1. Record and replay computer activity.
2. Store recorded task and replay them later.
3. Write your own task so you have more control over the computer.
4. Assign multiple arbitrary hotkey combinations to activate a stored task.
5. Assign multiple mouse gestures to activate a stored task.
6. Manage your Repeat tasks (either recorded or written).

Disclaimer
==========
1. This is not a password storage program. Source code written is not encrypted.
2. This program executes your own code. Use the advanced compile - replay feature carefully. Test your task before adding it to the list.

Installation
============
Just download the [latest version](https://github.com/hptruong93/Repeat/releases/latest), put the jar in a separate directory, and run it with java. That's it! You may need appropriate privileges since Repeat needs to listen to and/or control the mouse and keyboard.

Known issues
============
1. <del> Home/End buttons can only be used if numlocks is off </del> (This is a feature, not a bug)
2. <del> Some source code files are not getting cleaned up. However, they're all in the data/source folder </del> [Since 1.7.3](https://github.com/hptruong93/Repeat/releases/tag/1.7.3) --> Provide a menu for user to clean up unused source files.
3. <del>Python language is not fully supported. I'll have to write a better controller (mouse & keyboard) library for python.</del> Fully integrated python module [Since 2.0](https://github.com/hptruong93/Repeat/releases/tag/v2.0).
4. If application is not initialized correctly, it may not exit and has to be killed. This rarely happens. I've not seen such thing for a very long time.
5. <del> Does not catch error if hotkeys collide for different tasks. (i.e. you are responsible for not violating your hotkey assignment) </del> [Since 1.8](https://github.com/hptruong93/Repeat/releases/tag/1.8) --> Does not allow user to have overlapping keys.

FAQ
===

## What is the difference between this and [AutoHotkey](https://autohotkey.com/)?
1. This runs on any platform that supports Java and is non [headless](https://en.wikipedia.org/wiki/Headless_software). AutoHotkey is written for Windows. Repeat works fine on your Linux machine at work as well as your Window gaming setup at home.
2. The only limit to your hotkey power is your knowledge of the language you write your tasks in (e.g. Java, Python or C#). You don't have to learn a new meta language provided by AutoHotkey. This allows you to leverage your expertise in the language chosen and/or the immense support from the internet.

## Why is this not available on headless systems?
It does not make sense to listen to keyboard and mouse events in a headless system. How can you move your mouse if you have no screen? What would typing a key mean in such system?

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

## I am running a Linux machine. Will the C# module get started at application start time?
C# module is disabled if you are on a non Windows OS. Nothing from C# module will get started.

## It's 2016. Why Java Swing?
At the time I was desperately in need of a tool to liberate myself from the repetitive tasks at work. I could have used JavaFX but I would take more time since I was not that proficient at JavaFX.

## Why can't this be a web service? It's a lot of effort downloading the JAR and run it.
This application listens on your mouse and keyboard events as well as allowing you to simulate mouse/keyboard events. If I could do such thing from a web browser, some hackers must have got your personal information, passwords, bank account number by now.

## Why is there no support for JavaScript?
It's coming. I will get it done when I have time.

Code examples
=============
Type out 100! = 1 * 2 * 3 * ... * 100
-------------------------------------
    /* Write your code beneath "Begin generated code" */
    controller.blockingWait(100); //To make sure the hotkey has been fully released
    key.type("100! = ");
    for (int i = 1; i <= 100; i++) {
        key.type(i + "");
        if (i != 100)
            key.type(" * ");
    }

Libraries used
==============
1. jnativehook Global Hooks library at https://github.com/kwhat/jnativehook
2. Argo JSON library at http://argo.sourceforge.net/
3. Jama - A Java matrix package at http://math.nist.gov/javanumerics/jama/
