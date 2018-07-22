function registerLogHandler() {
    refreshLog($("#main-log"), { backOff: 500, since: 0 });
}

function refreshLog(textArea, state) {
    var scheduleNext = function(state) {
        setTimeout(function() {
            refreshLog(textArea, state);
        }, state.backOff);
    }

    $.get("/internals/get/logs?since=" + state.since, function(data) {
        state.backOff = 500;
        state.since = Date.now();

        textArea.val(textArea.val() + data);

        // Scroll to bottom if not focused.
        if (textArea.length && !textArea.is(":focus")) {
            textArea.scrollTop(textArea[0].scrollHeight - textArea.height());
        }
    }).fail(function(response) {
        state.backOff = Math.min(60000, Math.floor(state.backOff * 2));
    }).always(function() {
        scheduleNext(state);
    });
}