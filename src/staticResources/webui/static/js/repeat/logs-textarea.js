function registerLogHandler() {
    refreshLog($("#main-log"), { backOff: 500 });
}

function refreshLog(textArea, state) {
    var scheduleNext = function(state) {
        setTimeout(function() {
            refreshLog(textArea, state);
        }, state.backOff);
    }

    $.get("/internals/get/logs", function(data) {
        state.backOff = 500;
        textArea.val(data);
    }).fail(function(response) {
        state.backOff = Math.min(60000, Math.floor(state.backOff * 2));
    }).always(function() {
        scheduleNext(state);
    });
}