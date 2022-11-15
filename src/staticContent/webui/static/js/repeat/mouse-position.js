function registerMousePosition() {
    state = { backOff: 500 }

    $("#mouse-position").click(buttonMousePositionToggle);
    refreshMousePosition($("#mouse-position"), state);
}

function buttonMousePositionToggle(e) {
    $.get("/internals/get/is-mouse-position-logging-enabled", function(data) {
        var wantedState = false + "";
        if (data == "false") {
            wantedState = true + "";
        }
        var postData = { "enabled": "" + wantedState }
        $.post("/internals/set/mouse-position-logging-enabled", JSON.stringify(postData), function(data) {
            if (wantedState == "true") {
                $("#mouse-position").addClass("btn-success");
                $("#mouse-position").removeClass("btn-default");
            } else {
                $("#mouse-position").addClass("btn-default");
                $("#mouse-position").removeClass("btn-success");
            }
        }).fail(function(response) {
            alert('Error setting current mouse position logging state to `' + wantedState + '`: ' + response.responseText);
        });
    }).fail(function(response) {
        alert('Error getting current mouse position logging state: ' + response.responseText);
    });
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