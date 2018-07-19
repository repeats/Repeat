function registerMousePosition() {
    state = { backOff: 500 }

    refreshMousePosition($("#mouse-position"), state);
}

function refreshMousePosition(element, state) {
    var scheduleNext = function(state) {
        setTimeout(function() {
            refreshMousePosition(element, state);
        }, state.backOff);
    }

    $.get("/internals/get/mouse-position", function(data) {
        state.backOff = 500;
        element.html(data);
    }).fail(function(response) {
        state.backOff = Math.min(60000, Math.floor(state.backOff * 2));
    }).always(function() {
        scheduleNext(state);
    });
}