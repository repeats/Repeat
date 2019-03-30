// $gcc -O3 test.c

#include <assert.h>
#include <errno.h>
#include <fcntl.h>   // open
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>  // strerror
#include <unistd.h>  // daemon, close

#include <linux/input.h>

int open_device_file(char *deviceFile) {
    int device_file_desc = open(deviceFile, O_RDONLY);
    if (device_file_desc == -1) {
        printf("Error opening input file '%s': %s\n", deviceFile, strerror(errno));
        return 0;
    }

    return device_file_desc;
}

void read_events(int fd) {
    struct input_event event;
    while (1) {
        ssize_t n = read(fd, &event, sizeof(struct input_event));
        if (n == (ssize_t)-1) {
            if (errno == EINTR) {
                continue;
            } else {
                printf("Error reading event: %s\n", strerror(errno));
                continue;
            }
        } else if (n != sizeof event) {
            errno = EIO;
            break;
        }

        if ((event.type == 0) && (event.value == 0) && (event.code == 0)) {
            continue;
        }

        if (event.type != EV_KEY && event.type != EV_REL && event.type != EV_ABS) {
            continue;
        }

        printf("Ts:%ld,Tus:%ld,T:%d,C:%d,V:%d\n", event.time.tv_sec, event.time.tv_usec, event.type, event.code, event.value);
        fflush(stdout);
    }
}

void print_usage() {
    printf("Run this as sudo.\n\
            sudo ./repeat_hook input_file\n\
            Example: sudo ./repeat_hook /dev/input/event2\n");
}

int main(int argc, char **argv) {
    if (argc != 2) {
        print_usage();
        return 0;
    }

    int device_file_desc = open_device_file(argv[1]);
    if (device_file_desc == 0) {
        return 0;
    }

    read_events(device_file_desc);
    close(device_file_desc);
    return 0;
}
