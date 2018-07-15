function registerLogHandler() {
    state = { backOff: 500 }

    setTimeout(function() {
        refreshLog($("#main-log"), state);
    }, state.backOff);
}

function refreshLog(textArea, state) {
    $.get("/internals/get/logs", function(data) {
        state.backOff = 500;
        textArea.val(data);
    }).fail(function(response) {
        state.backOff = Math.min(10000, Math.floor(state.backOff * 2));
    });

    setTimeout(function() {
        refreshLog(textArea, state);
    }, state.backOff);
}