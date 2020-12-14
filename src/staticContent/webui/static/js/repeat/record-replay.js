function registerRecordReplayActions() {
    $("#button-record").click(buttonRecordAction);
    $("#button-replay").click(buttonReplayAction);

    // Need to poll always because the start/stop action can be triggered by hotkeys instead.
    var pollingRecording = createPollingButtonFunction({
        endpoint: "/internals/get/is-recording",
        buttonId: "button-record",
        onClass: "repeat-btn-stop",
        offClass: "repeat-btn-record",
        onActive: function() {
            $("#button-replay").prop('disabled', true);
        },
        onInactive: function() {
            $("#button-replay").prop('disabled', false);
        },
    });
    pollingRecording({ backOff: 1 }, 1000);

    // Need to poll always because the start/stop action can be triggered by hotkeys instead.
    var pollingReplaying = createPollingButtonFunction({
        endpoint: "/internals/get/is-replaying",
        buttonId: "button-replay",
        onClass: "repeat-btn-stop",
        offClass: "repeat-btn-replay",
        onActive: function() {
            $("#button-record").prop('disabled', true);
        },
        onInactive: function() {
            $("#button-record").prop('disabled', false);
        },
    });
    pollingReplaying({ backOff: 1 }, 1000);
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
            $("#button-replay").prop('disabled', true);
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
        var replayCountText = $("#button-replay-count").html();
        var delayText = $("#button-replay-delay").html();
        var speedupText = $("#button-replay-speedup").html();

        $.post("/internals/action/start-replay", {
            count: replayCountText,
            delay: delayText,
            speedup: speedupText,
        }, function(status) {
            $("#button-record").prop('disabled', true);
        }).fail(function(response) {
            alert('Error sending request to start replaying: ' + response.responseText);
        });
    }
}
