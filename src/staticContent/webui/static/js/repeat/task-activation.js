function registerTaskActivationActions() {
    $("#button-save").click(buttonSaveAction);
    $("#button-on-key-pressed").click(buttonOnKeyPressedAction);
    $("#button-on-key-released").click(buttonOnKeyReleasedAction);
    $("#button-strokes").click(buttonStrokeAction);
    $("#button-add-mouse-key-left").click(buttonMouseKeyAction("LEFT"));
    $("#button-add-mouse-key-middle").click(buttonMouseKeyAction("MIDDLE"));
    $("#button-add-mouse-key-right").click(buttonMouseKeyAction("RIGHT"));
    $("#button-add-key-chain").click(buttonAddKeyChainAction);
    $("#button-add-key-sequence").click(buttonAddKeySequenceAction);
    $("#button-add-phrase").click(buttonAddPhraseAction);
    $("#button-add-shared-variables").click(buttonAddSharedVariables);

    $("#modal-phrase-save").click(buttonModalAddPhraseAction);
    $("#modal-shared-variables-save").click(buttonModalAddSharedVariablesAction);
    registerRemoveKeyChainAction();
    registerRemoveKeySequenceAction();
    registerRemovePhraseAction();
    registerRemoveSharedVariableAction();
}

function registerRemoveKeyChainAction() {
    registerRemoveActivationAction("table-key-chains", "/internals/action/task-activation/key-chain/remove");
}

function registerRemoveKeySequenceAction() {
    registerRemoveActivationAction("table-key-sequences", "/internals/action/task-activation/key-sequence/remove");
}

function registerRemovePhraseAction() {
    registerRemoveActivationAction("table-phrases", "/internals/action/task-activation/phrase/remove");
}

function registerRemoveSharedVariableAction() {
    registerRemoveActivationAction("table-shared-variables", "/internals/action/task-activation/shared-variables/remove");
}

function registerRemoveActivationAction(tableId, endpoint) {
    var table = document.getElementById(tableId);
    // Start from row = 1 since row = 0 is the headings.
    for (var i = 1; i < table.rows.length; i++) {
        for (var j = 0; j < table.rows[i].cells.length; j++)
        table.rows[i].cells[j].ondblclick = function(cell, i, j) {
            return function() {
                // Minus one so that row index starts from 0.
                removeActivationRowAction(i - 1, tableId, endpoint);
            };
        }(table.rows[i].cells[j], i, j);
    }
}

function removeActivationRowAction(row, tableId, endpoint) {
    var postData = getTaskActivationParameters();
    postData.index = row;

    $.post(endpoint, JSON.stringify(postData), function(data) {
        $("#" + tableId).html(data);
        registerRemoveActivationAction(tableId, endpoint);
    }).fail(function(response) {
        alert('Error removing entry: ' + response.responseText);
    });
}

function buttonSaveAction(e) {
    function save() {
        $.post("/internals/action/task-activation/save", JSON.stringify(getTaskActivationParameters()), function(data) {
            window.location.assign("/");
        }).fail(function(response) {
            alert('Error saving task activation: ' + response.responseText);
        });
    }

    var postData = getTaskActivationParameters();
    selectedGestures = [];
    $("#mouse-gestures-selection").find("option").each(function(index) {
        if ($(this).is(':selected')) {
            selectedGestures.push(index);
        }
    });
    postData.gestures = JSON.stringify(selectedGestures);

    $.post("/internals/action/task-activation/mouse-gestures/set", JSON.stringify(postData), function(data) {
        save();
    }).fail(function(response) {
        alert('Error setting mouse gestures: ' + response.responseText);
    });
}

function buttonOnKeyPressedAction(e) {
    var button = $(this);
    var isOn = button.hasClass("btn-info");
    
    var endpoint = '/internals/action/task-activation/global-key-action/pressed/set';
    var data = getTaskActivationParameters();
    data.pressed = isOn ? 'false' : 'true';

    _setGlobalKeyActionConfiguration(button, endpoint, data);
}

function buttonOnKeyReleasedAction(e) {
    var button = $(this);
    var isOn = button.hasClass("btn-info");
    
    var endpoint = '/internals/action/task-activation/global-key-action/released/set';
    var data = getTaskActivationParameters();
    data.released = isOn ? 'false' : 'true';

    _setGlobalKeyActionConfiguration(button, endpoint, data);
}

function _setGlobalKeyActionConfiguration(button, endpoint, data) {
    $.post(endpoint, data = JSON.stringify(data), function(data) {
        button.toggleClass("btn-default");
        button.toggleClass("btn-info");
    }).fail(function(response) {
        alert('Error toggling global key task activation: ' + response.responseText);
    });
}

function buttonStrokeAction(e) {
    var button = $(this);
    var endpoint = "/internals/action/task-activation/start-listening";
    if (button.hasClass("btn-info")) {
        endpoint = "/internals/action/task-activation/stop-listening";
    }

    $.post(endpoint, JSON.stringify(getTaskActivationParameters()), function(data) {
        button.toggleClass("btn-default");
        button.toggleClass("btn-info");

        var isListening = button.hasClass("btn-info")

        if (!isListening) {
            shouldStopPollingKeyStrokes = true;
        } else {
            shouldStopPollingKeyStrokes = false;
            pollKey(button, { backOff: 1 });
        }
    }).fail(function(response) {
        alert('Error toggling task activation listening: ' + response.responseText);
    });
}

function buttonMouseKeyAction(key) {
    return function(e) {
        var endpoint = "/internals/action/task-activation/strokes/add-mouse-key";
        var params = getTaskActivationParameters();
        params.key = key;

        $.post(endpoint, JSON.stringify(params), function(data) {
            var button = $("#button-strokes");
            button.html(data);
        }).fail(function(response) {
            alert('Error adding mouse key: ' + response.responseText);
        });
    }
}

var shouldStopPollingKeyStrokes = false;

function pollKey(button, state) {
    var scheduleNext = function(state) {
        setTimeout(function() {
            pollKey(button, state);
        }, state.backOff);
    }

    $.post("/internals/action/task-activation/strokes/get", JSON.stringify(getTaskActivationParameters()), function(data) {
        state.backOff = 100;
        button.html(data);
    }).fail(function(response) {
        state.backOff = Math.min(3600000, Math.floor(state.backOff * 2));
    });

    if (!shouldStopPollingKeyStrokes) {
        scheduleNext(state);
    }
}

function buttonAddKeyChainAction(e) {
    $.post("/internals/action/task-activation/strokes/add-as-key-chain", JSON.stringify(getTaskActivationParameters()), function(data) {
        $("#table-key-chains").html(data);
        registerRemoveKeyChainAction();
        resetPollingKeys();
    }).fail(function(response) {
        alert('Error adding strokes as key chain: ' + response.responseText);
    });
}

function buttonAddKeySequenceAction(e) {
    $.post("/internals/action/task-activation/strokes/add-as-key-sequence", JSON.stringify(getTaskActivationParameters()), function(data) {
        $("#table-key-sequences").html(data);
        registerRemoveKeySequenceAction();
        resetPollingKeys();
    }).fail(function(response) {
        alert('Error adding strokes as key sequence: ' + response.responseText);
    });
}

function buttonAddPhraseAction(e) {
    $("#new-phrase").val("");
    $("#modal-phrase").modal();
}

function buttonModalAddPhraseAction(e) {
    var phrase = $("#new-phrase").val();

    var postData = getTaskActivationParameters();
    postData.phrase = phrase;

    $.post("/internals/action/task-activation/phrase/add", JSON.stringify(postData), function(data) {
        $("#table-phrases").html(data);
        registerRemovePhraseAction();
    }).fail(function(response) {
        alert('Error adding phrase: ' + response.responseText);
    });
}

function buttonAddSharedVariables(e) {
    $("#new-variable-namespace").val("");
    $("#new-variable-name").val("");
    $("#modal-shared-variables").modal();
}

function buttonModalAddSharedVariablesAction(e) {
    var namespace = $("#new-variable-namespace").val();
    var name = $("#new-variable-name").val();

    var postData = getTaskActivationParameters();
    postData.variables = JSON.stringify({
        vars: [{
            namespace: namespace,
            name: name,
        }],
    });

    $.post("/internals/action/task-activation/shared-variables/add", JSON.stringify(postData), function(data) {
        $("#table-shared-variables").html(data);
        registerRemoveSharedVariableAction();
    }).fail(function(response) {
        alert('Error adding shared variables: ' + response.responseText);
    });
}

function resetPollingKeys() {
    var button = $("#button-strokes");
    button.addClass("btn-default");
    button.removeClass("btn-info");
    button.html("None");

    shouldStopPollingKeyStrokes = true;
}

function getTaskActivationParameters() {
    return {
        id: $("#task-activation-constructor-id").html(),
        task: $("#task-activation-task").html(),
    };
}
