function registerRecordReplayActions() {
    $("#button-record").click(buttonRecordAction);
    $("#button-replay").click(buttonReplayAction);
}

function buttonRecordAction(e) {
    console.log("buttonRecordActione");
    $.post("/internals/toggle/record", function(status) {
        // Nothing to do.
        console.log(status);
    }).fail(function(response) {
        alert('Error sending request to toggle recording: ' + response.responseText);
    });
}

function buttonReplayAction(e) {
    $.post("/internals/toggle/replay", function(status) {
        // Nothing to do.
        console.log(status);
    }).fail(function(response) {
        alert('Error sending request to toggle replaying: ' + response.responseText);
    });
}