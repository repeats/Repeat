function registerIndexPageButtonActions() {
    $("#button-compile").click(buttonCompileAction)
    $("#button-run").click(buttonRunAction)
    $("#button-edit-code").click(buttonEditCodeAction)
    $("#button-reload").click(buttonReloadAction)

    $("#button-add").click(buttonAddAction)
    $("#button-overwrite").click(buttonOverwriteAction)
    $("#button-delete").click(buttonDeleteAction)
    $("#button-up").click(buttonMoveUpAction)
    $("#button-down").click(buttonMoveDownAction)
    $("#button-change-group").click(buttonChangeGroupAction)
    $("#modal-move-to-task-group-move").click(buttonMoveGroupAction);
}

function buttonCompileAction(e) {
    var source = $("#source-code").val();

    $.post("/internals/action/compile-task", source, function(status) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to compile: ' + response.responseText);
    });
}

function buttonRunAction(e) {
    var isRunning = $("#button-run").hasClass("repeat-btn-stop-running-compiled");
    if (isRunning) {
        $.post("/internals/action/stop-running-compiled-task", function(status) {
            // Nothing to do.
        }).fail(function(response) {
            alert('Error sending request to stop running compiled action: ' + response.responseText);
        });
    } else {
        $.post("/internals/action/run-compiled-task", function(status) {
            var polling = createPollingButtonFunction({
                endpoint: "/internals/get/is-running-compiled-task",
                buttonId: "button-run",
                onClass: "repeat-btn-stop-running-compiled",
                offClass: "repeat-btn-run",
            });
            polling({ backOff: 1 });

        }).fail(function(response) {
            alert('Error sending request to run compiled action: ' + response.responseText);
        });
    }
}

function buttonEditCodeAction(e) {
    var source = $("#source-code").val();

    $.post("/internals/action/edit-source", source, function(status) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to compile: ' + response.responseText);
    });
}

function buttonReloadAction(e) {
    $.get("/internals/get/editted-source", function(data) {
        $("#source-code").val(data);
    }).fail(function(response) {
        alert('Error sending request get editted source: ' + response.responseText);
    });
}

function buttonAddAction(e) {
    $.post("/internals/action/add-task", function(data) {
        refreshTasksWithData(data);
    }).fail(function(response) {
        alert('Error sending request to add task: ' + response.responseText);
    });
}

function buttonOverwriteAction(e) {
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/overwrite-task", JSON.stringify({task: index}), function(data) {
        refreshTasksWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error sending request to overwrite task: ' + response.responseText);
    });
}

function buttonDeleteAction(e) {
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/delete-task", JSON.stringify({task: index}), function(data) {
        refreshTasksWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error sending request to delete task: ' + response.responseText);
    });
}

function buttonMoveUpAction(e) {
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-up", JSON.stringify({task: index}), function(data) {
        refreshTasksWithDataAndIndex(data, index - 1);
    }).fail(function(response) {
        alert('Error sending request to move task up: ' + response.responseText);
    });
}

function buttonMoveDownAction(e) {
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-down", JSON.stringify({task: index}), function(data) {
        refreshTasksWithDataAndIndex(data, index + 1);
    }).fail(function(response) {
        alert('Error sending request to move task down: ' + response.responseText);
    });
}

function buttonChangeGroupAction(e) {
    console.log("buttonChangeGroupAction");
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }
    $('#modal-move-to-task-group-row').val(index);
    $.get("/internals/get/rendered-task-groups-select-modal", JSON.stringify({task: index}), function(data) {
        $("#modal-move-to-task-group-body").html(data);
        $("#modal-move-to-task-group").modal();
    }).fail(function(response) {
        alert('Error getting task groups to select: ' + response.responseText);
    });
}

function buttonMoveGroupAction(e) {
    var taskIndex = $('#modal-move-to-task-group-row').val();
    var groupIndex = -1;

    var groups = $("#modal-move-to-task-group-body").find(".form-check-input");
    groups.each(function(i) {
        console.log(i);
        if ($(this).is(':checked')) {
            groupIndex = i;
            return false;
        }
    })

    if (groupIndex == -1) {
        return;
    }

    $.post("/internals/action/change-task-group-for-task", JSON.stringify({task: taskIndex, group: groupIndex}), function(data) {
        refreshTasksWithData(data);
    }).fail(function(response) {
        alert('Error sending request to move task to new group: ' + response.responseText);
    });
}
