function registerMousePosition() {
    state = { backOff: 500 }

    setTimeout(function() {
        refreshMousePosition($("#mouse-position"), state);
    }, state.backOff);
}

function refreshMousePosition(element, state) {
    $.get("/internals/get/mouse-position", function(data) {
        state.backOff = 500;
        element.html(data);
    }).fail(function(response) {
        state.backOff = Math.min(10000, Math.floor(state.backOff * 2));
    });

    setTimeout(function() {
        refreshMousePosition(element, state);
    }, state.backOff);
}