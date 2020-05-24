
manuallyBuildTask = function() {
    var listActorAction = function(e) {
        var actor = e.target.innerHTML;
        $.get("/internals/action/manually-build/constructor/possible-actions?actor=" + actor, function(data) {
            document.getElementById("manually-build-task-button-actor").innerHTML = actor;
            document.getElementById("manually-build-task-actions").innerHTML = data;
            document.getElementById("manually-build-task-button-action").innerHTML = "Action";
        }).fail(function(response) {
            alert('Error sending request to get list of possible actions: ' + response.responseText);
        });
    }

    var listActionsAction = function(e) {
        var action = e.target.innerHTML;
        document.getElementById("manually-build-task-button-action").innerHTML = action;
    }

    var addStepAction = function(e) {
        var actor = document.getElementById("manually-build-task-button-actor").innerHTML;
        var action = document.getElementById("manually-build-task-button-action").innerHTML;
        var params = document.getElementById("manually-build-task-parameters-value").value;

        var data = {
            id: document.getElementById("manually-build-task-id").innerHTML,
            actor: actor,
            action: action,
            parameters: params,
        }
        $.post("/internals/action/manually-build/constructor/add-step", JSON.stringify(data), function(data) {
            document.getElementById("manually-build-action-steps").innerHTML = data;
            registerRemoveStepAction();
        }).fail(function(response) {
            alert('Error adding step: ' + response.responseText);
        });
    }

    var registerRemoveStepAction = function() {
        var tableId = "manually-build-action-table-steps";
        var table = document.getElementById(tableId);
        if (!table) {
            return;
        }

        // Start from row = 1 since row = 0 is the headings.
        for (var i = 1; i < table.rows.length; i++) {
            for (var j = 0; j < table.rows[i].cells.length; j++) {
                table.rows[i].cells[j].ondblclick = function(cell, i, j) {
                    return function() {
                        // Minus one so that row index starts from 0.
                        removeStepRowAction(i - 1, tableId);
                    };
                }(table.rows[i].cells[j], i, j);
            }
        }
    }

    var removeStepRowAction = function(row, tableId) {
        var data = {
            id: document.getElementById("manually-build-task-id").innerHTML,
            index: row,
        }

        $.post("/internals/action/manually-build/constructor/remove-step", JSON.stringify(data), function(data) {
            $("#manually-build-action-steps").html(data);
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

    registerActions = function() {
        $("#manually-build-task-actor").click(listActorAction);
        $("#manually-build-task-actions").click(listActionsAction);
        $("#manually-build-task-add-step-button").click(addStepAction);

        if (document.getElementById("manually-build-task-save-button")) {
            $("#manually-build-task-save-button").click(saveButtonAction);
        }

        registerRemoveStepAction();
    }

    return {
        registerActions: registerActions,
    }
}()
