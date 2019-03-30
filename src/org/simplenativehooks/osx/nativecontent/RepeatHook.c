// Compile using $cc -O3 RepeatHook.c -framework ApplicationServices

#include <stdio.h>
#include <time.h>
#include <ApplicationServices/ApplicationServices.h>

int isKeyboardEvent(CGEventType);
int isMouseCursorEvent(CGEventType);
int getEvent(CGEventType);
CGEventRef myCGEventCallback(CGEventTapProxy, CGEventType, CGEventRef, void *);

#define EVENT_UNDEFINED 0
#define EVENT_KEY_DOWN 1
#define EVENT_KEY_UP 2
#define EVENT_LEFT_MOUSE_DOWN 3
#define EVENT_LEFT_MOUSE_UP 4
#define EVENT_RIGHT_MOUSE_DOWN 5
#define EVENT_RIGHT_MOUSE_UP 6
#define EVENT_MOUSE_SCROLLED 7
#define EVENT_MOUSE_MOVED 8

int main (int argc, const char * argv[]) {
  CGEventFlags oldFlags = CGEventSourceFlagsState(kCGEventSourceStateCombinedSessionState);

  CGEventMask eventMask = (CGEventMaskBit(kCGEventKeyDown) |
                           CGEventMaskBit(kCGEventKeyUp) |

                           CGEventMaskBit(kCGEventLeftMouseDown) |
                           CGEventMaskBit(kCGEventLeftMouseUp) |
                           CGEventMaskBit(kCGEventRightMouseDown) |
                           CGEventMaskBit(kCGEventRightMouseUp) |
                           CGEventMaskBit(kCGEventScrollWheel) |
                           CGEventMaskBit(kCGEventMouseMoved) |

                           CGEventMaskBit(kCGEventFlagsChanged));
  CFMachPortRef eventTap = CGEventTapCreate(kCGSessionEventTap, kCGHeadInsertEventTap, 0, eventMask, myCGEventCallback, &oldFlags);
  
  printf("Setting up...\n");
  if (!eventTap) {
    fprintf(stderr, "failed to create event tap\nyou need to enable \"Enable access for assitive devices\" in Universal Access preference panel.");
    exit(1);
  }
  
  printf("Setting up loop source...\n");
  CFRunLoopSourceRef runLoopSource = CFMachPortCreateRunLoopSource(kCFAllocatorDefault, eventTap, 0);
  CFRunLoopAddSource(CFRunLoopGetCurrent(), runLoopSource, kCFRunLoopCommonModes);
  printf("Enabling event Tap...\n");
  CGEventTapEnable(eventTap, true);
  
  printf("Running loop\n");
  printf("###################################\n");
  fflush(stdout);
  CFRunLoopRun();
  
  return 0;
}


CGEventRef myCGEventCallback (CGEventTapProxy proxy, CGEventType type, CGEventRef event, void *refcon) {
  if (!isKeyboardEvent(type) && 
      !isMouseCursorEvent(type) &&
      (type != kCGEventScrollWheel) &&
      (type != kCGEventFlagsChanged)) {
    return event;
  }
  
  int eventCode = getEvent(type);

  if (type == kCGEventFlagsChanged) {
    CGKeyCode keyCode = (CGKeyCode) CGEventGetIntegerValueField(event, kCGKeyboardEventKeycode);  
    CGEventFlags flag = CGEventGetFlags(event);

    int code = (int) keyCode;
    printf("E:%d,C:%d,M:%llu\n", eventCode, code, flag);
  }

  if (type == kCGEventKeyDown || type == kCGEventKeyUp) {
    CGKeyCode keyCode = (CGKeyCode) CGEventGetIntegerValueField(event, kCGKeyboardEventKeycode);

    int code = (int) keyCode;
    printf("E:%d,C:%d\n", eventCode, code);
  }

  if (isMouseCursorEvent(type)) {
    CGPoint point = CGEventGetLocation(event);
    double x = (double) point.x;
    double y = (double) point.y;
    printf("E:%d,X:%.0f,Y:%.0f\n", eventCode, x, y);
  }

  if (type == kCGEventScrollWheel) {
    printf("E:%d\n", eventCode);
  }

  fflush(stdout);
  return event;
}

int isKeyboardEvent(CGEventType type) {
  return (type == kCGEventKeyDown) || (type == kCGEventKeyUp);
}

int isMouseCursorEvent(CGEventType type) {
  return (type == kCGEventLeftMouseDown) ||
        (type == kCGEventLeftMouseUp) ||
        (type == kCGEventRightMouseDown) ||
        (type == kCGEventRightMouseUp) ||
        (type == kCGEventMouseMoved);
}

int getEvent(CGEventType type) {
  int output = 0;

  if (type == kCGEventKeyDown) {
    output = EVENT_KEY_DOWN;
  } else if (type == kCGEventKeyUp) {
    output = EVENT_KEY_UP;
  } else if (type == kCGEventLeftMouseDown) {
    output = EVENT_LEFT_MOUSE_DOWN;
  } else if (type == kCGEventLeftMouseUp) {
    output = EVENT_LEFT_MOUSE_UP;
  } else if (type == kCGEventRightMouseDown) {
    output = EVENT_RIGHT_MOUSE_DOWN;
  } else if (type == kCGEventRightMouseUp) {
    output = EVENT_RIGHT_MOUSE_UP;
  } else if (type == kCGEventMouseMoved) {
    output = EVENT_MOUSE_MOVED;
  } else if (type == kCGEventScrollWheel) {
    output = EVENT_MOUSE_SCROLLED;
  }  else if (type == kCGEventFlagsChanged) {
    output = EVENT_UNDEFINED;
  }

  return output;
}

