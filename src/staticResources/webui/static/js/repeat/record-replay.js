function registerRecordReplayActions() {
    $("#button-record").click(buttonRecordAction);
    $("#button-replay").click(buttonReplayAction);
}

function buttonRecordAction(e) {
    var isRecording = $("#button-record").hasClass("repeat-btn-stop");
    if (isRecording) {
        $.post("/internals/action/stop-record", function(status) {
            // Nothing to do.
        }).fail(function(response) {
            alert('Error sending request to stop recording: ' + response.responseText);
        });
    } else {
        $.post("/internals/action/start-record", function(status) {
            var polling = createPollingButtonFunction({
                endpoint: "/internals/get/is-recording",
                buttonId: "button-record",
                onClass: "repeat-btn-stop",
                offClass: "repeat-btn-record",
            });
            polling({ backOff: 1 });
        }).fail(function(response) {
            alert('Error sending request to start replaying: ' + response.responseText);
        });
    }
}

function buttonReplayAction(e) {
    var isReplaying = $("#button-replay").hasClass("repeat-btn-stop");
    if (isReplaying) {
        $.post("/internals/action/stop-replay", function(status) {
            // Nothing to do.
        }).fail(function(response) {
            alert('Error sending request to stop replaying: ' + response.responseText);
        });
    } else {
        $.post("/internals/action/start-replay", function(status) {
            var polling = createPollingButtonFunction({
                endpoint: "/internals/get/is-replaying",
                buttonId: "button-replay",
                onClass: "repeat-btn-stop",
                offClass: "repeat-btn-replay",
            });
            polling({ backOff: 1 });
        }).fail(function(response) {
            alert('Error sending request to start replaying: ' + response.responseText);
        });
    }
}
