// $gcc -O3 key.c -o key.out -lX11 -lXtst

#include <stdio.h>

#include <X11/XKBlib.h>
#include <X11/extensions/record.h>


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

    record_range->device_events.first = KeyPress;
    record_range->device_events.last = KeyRelease;
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

void callback_function(XPointer ptr, XRecordInterceptData *data) {
    if (data->category != XRecordFromServer) {
        return;
    }

    int key = ((unsigned char*) data->data)[1];
    int type = ((unsigned char*) data->data)[0] & 0x7F;
    int repeat = data->data[2] & 1;

    key -= 8;
    if(!repeat) {
        switch (type) {
            case KeyPress:
                printf("K,T:P,K:%d\n", key);
                fflush(stdout);
                break;
            case KeyRelease:
                printf("K,T:R,K:%d\n", key);
                fflush(stdout);
                break;
            default:
                break;
        }
    } else { // Repeat.
        printf("K,T:E,K:%d\n", key);
        fflush(stdout);
    }

    XRecordFreeData(data);
}

int main(void) {
    printf("=======================================================\n");
    printf("Starting key listener...\n");
    fflush(stdout);
    int started = scan();
    if (started != 0) {
        printf("Error encountered. Return code %d.\n", started);
        return -1;
    }
    printf("Key listener terminated.\n");
    return 0;
}