
manuallyBuildTask = function() {
    var tableStepsId = 'manually-build-action-table-steps';

    var listActorAction = function(e) {
        var actor = e.target.innerHTML;
        $.get("/internals/action/manually-build/constructor/possible-actions?actor=" + actor, function(data) {
            document.getElementById("manually-build-task-button-actor").innerHTML = actor;
            document.getElementById("manually-build-task-actions").innerHTML = data;
            document.getElementById("manually-build-task-button-action").innerHTML = "Action";
            document.getElementById("manually-build-task-parameters-value").placeholder = "Pick an action.";
        }).fail(function(response) {
            alert('Error sending request to get list of possible actions: ' + response.responseText);
        });
    }

    var listActionsAction = function(e) {
        var actor = document.getElementById("manually-build-task-button-actor").innerHTML;
        var action = e.target.innerHTML;

        var query = "?actor=" + encodeURIComponent(actor);
        query += "&action=" + encodeURIComponent(action);

        document.getElementById("manually-build-task-button-action").innerHTML = action;
        $.get("/internals/action/manually-build/constructor/params-placeholder" + query, function(data) {
            document.getElementById("manually-build-task-parameters-value").placeholder = data;
        }).fail(function(response) {
            alert('Error sending request to get list of possible actions: ' + response.responseText);
        });
    }

    var addStepAction = function(e) {
        var actor = document.getElementById("manually-build-task-button-actor").innerHTML;
        var action = document.getElementById("manually-build-task-button-action").innerHTML;
        var params = document.getElementById("manually-build-task-parameters-value").value;

        var data = {
            id: document.getElementById("manually-build-task-id").innerHTML,
            index: utilMutiSelectTable.lastSelected(tableStepsId),
            actor: actor,
            action: action,
            parameters: params,
        }
        $.post("/internals/action/manually-build/constructor/insert-step", JSON.stringify(data), function(data) {
            document.getElementById("manually-build-action-steps").innerHTML = data;
            utilMutiSelectTable.register(tableStepsId);
            registerRemoveStepAction();
        }).fail(function(response) {
            alert('Error adding step: ' + response.responseText);
        });
    }

    var registerRemoveStepAction = function() {
        var table = document.getElementById(tableStepsId);
        if (!table) {
            return;
        }

        // Start from row = 1 since row = 0 is the headings.
        for (var i = 1; i < table.rows.length; i++) {
            for (var j = 0; j < table.rows[i].cells.length; j++) {
                table.rows[i].cells[j].ondblclick = function(cell, i, j) {
                    return function() {
                        // Minus one so that row index starts from 0.
                        removeStepRowAction([i - 1]);
                    };
                }(table.rows[i].cells[j], i, j);
            }
        }
    }

    var postActionOnSelectedRows = function(endpoint) {
        var selectedRows = utilMutiSelectTable.allSelected(tableStepsId);
        var data = {
            id: document.getElementById("manually-build-task-id").innerHTML,
            indices: selectedRows,
        }
        $.post(endpoint, JSON.stringify(data), function(data) {
            $("#manually-build-action-steps").html(data);
            utilMutiSelectTable.register(tableStepsId);
            registerRemoveStepAction();
        }).fail(function(response) {
            alert('Error calling ' + endpoint + ': ' + response.responseText);
        });
    }

    var moveUpAction = function(tableId) {
        postActionOnSelectedRows("/internals/action/manually-build/constructor/move-up");
    }

    var moveDownAction = function(tableId) {
        postActionOnSelectedRows("/internals/action/manually-build/constructor/move-down");
    }

    var removeStepsAction = function(tableId) {
        postActionOnSelectedRows("/internals/action/manually-build/constructor/remove-steps");
    }

    var removeStepRowAction = function(rows) {
        if (rows.length == 0) {
            return;
        }

        var data = {
            id: document.getElementById("manually-build-task-id").innerHTML,
            indices: rows,
        }

        $.post("/internals/action/manually-build/constructor/remove-steps", JSON.stringify(data), function(data) {
            $("#manually-build-action-steps").html(data);
            utilMutiSelectTable.register(tableStepsId);
            registerRemoveStepAction();
        }).fail(function(response) {
            alert('Error removing step: ' + response.responseText);
        });
    }

    var saveButtonAction = function() {
        $.post("/internals/action/manually-build/constructor/build", JSON.stringify({id: document.getElementById("manually-build-task-id").innerHTML}), function(data) {
            // Nothing to do.
        }).fail(function(response) {
            alert('Error compiling task: ' + response.responseText);
        });
    }

    var registerTableSelectionHooks = function() {
        utilMutiSelectTable.register(tableStepsId);
    }

    registerActions = function() {
        $("#manually-build-task-actor").click(listActorAction);
        $("#manually-build-task-actions").click(listActionsAction);
        $("#manually-build-task-insert-step-button").click(addStepAction);
        $("#manually-build-task-remove-step-button").click(removeStepsAction);
        $("#manually-build-task-move-up-button").click(moveUpAction);
        $("#manually-build-task-move-down-button").click(moveDownAction);

        if (document.getElementById("manually-build-task-save-button")) {
            $("#manually-build-task-save-button").click(saveButtonAction);
        }

        registerRemoveStepAction();
        registerTableSelectionHooks();
    }

    return {
        registerActions: registerActions,
    }
}()
