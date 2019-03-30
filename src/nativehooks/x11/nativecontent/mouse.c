// $gcc -O3 mouse.c -o mouse.out -lX11 -lXtst

#include <stdio.h>

#include <X11/Xlibint.h>
#include <X11/extensions/record.h>

typedef union {
    unsigned char type;
    xEvent event;
    xResourceReq req;
    xGenericReply reply;
    xError error;
    xConnSetupPrefix setup;
} XRecordDatum;

void callback_function(XPointer arg, XRecordInterceptData *data);

int scan() {
    printf("Opening Xrecord context\n");
    printf("=======================================================\n");
    fflush(stdout);

    Display *display = XOpenDisplay(NULL);
    if (display == NULL) {
        fprintf(stderr, "XOpenDisplay error.\n");
        return -1;
    }
    
    XRecordRange* record_range = XRecordAllocRange();
    if (record_range == NULL) {
        fprintf(stderr, "XRecordAllocRange error.\n");
        return -2;
    }

    record_range->device_events.first = ButtonPress;
    record_range->device_events.last = MotionNotify;
    XRecordClientSpec client_spec = XRecordAllClients;

    XRecordContext context = XRecordCreateContext(display, 0, &client_spec, 1, &record_range, 1);
    if (context == 0) {
        fprintf(stderr, "XRecordCreateContext error.\n");
        return -3;
    }

    XFree(record_range);
    if (XRecordEnableContext(display, context, callback_function, NULL) == 0) {
        fprintf(stderr, "XRecordEnableContext error.\n");
        return -4;
    }

    // Unreachable.
    return 0;
}

void callback_function(XPointer arg, XRecordInterceptData *data) {
    if (data->category != XRecordFromServer) {
        return;
    }

    XRecordDatum *xrec_data  = (XRecordDatum *) data->data;
    int x, y, button;
    switch (xrec_data->type) {
        case MotionNotify:
            ;
            x = xrec_data->event.u.keyButtonPointer.rootX;
            y = xrec_data->event.u.keyButtonPointer.rootY;
            printf("M,T:M,X:%d,Y:%d\n", x, y);
            fflush(stdout);
            break;
        case ButtonPress:
            ;
            button = xrec_data->event.u.u.detail;
            printf("M,T:P,B:%d\n", button);
            fflush(stdout);
            break;
        case ButtonRelease:
            ;
            button = xrec_data->event.u.u.detail;
            printf("M,T:R,B:%d\n", button);
            fflush(stdout);
            break;
        default:
            break;
    }

    XRecordFreeData(data);
}

int main(void) {
    printf("=======================================================\n");
    printf("Starting mouse listener...\n");
    fflush(stdout);
    int started = scan();
    if (started != 0) {
        printf("Error encountered. Return code %d.\n", started);
        return -1;
    }
    printf("Mouse listener terminated.\n");
    return 0;
}