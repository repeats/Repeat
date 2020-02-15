function createPollingButtonFunction(params) {
    function pollingFunction(state) {
        var scheduleNext = function(state) {
            setTimeout(function() {
                pollingFunction(state);
            }, state.backOff);
        }

        $.get(params.endpoint, function(data) {
            state.backOff = 100;
            if (data == "true") {
                $("#" + params.buttonId).removeClass(params.offClass);
                $("#" + params.buttonId).addClass(params.onClass);
                scheduleNext(state);
            } else {
                $("#" + params.buttonId).removeClass(params.onClass);
                $("#" + params.buttonId).addClass(params.offClass);
                if (typeof params.onFinish != 'undefined') {
                    params.onFinish();
                }
            }
        }).fail(function(response) {
            state.backOff = Math.min(3600000, Math.floor(state.backOff * 2));
            scheduleNext(state);
        });
    }
    return pollingFunction;
}
