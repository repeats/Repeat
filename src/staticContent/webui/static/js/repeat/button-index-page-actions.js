function registerIndexPageButtonActions() {
    $("#button-replay-count").click(buttonReplayCountAction);
    $("#button-replay-delay").click(buttonReplayDelayAction);
    $("#button-replay-speedup").click(buttonReplaySpeedupAction);
    $("#modal-replay-config-save").click(buttonReplayConfigSaveAction);

    $("#button-compile").click(buttonCompileAction)
    $("#button-run").click(buttonRunAction)
    $("#button-edit-code").click(buttonEditCodeAction)
    $("#button-reload").click(buttonReloadAction)

    $("#button-run-selected").click(buttonRunSelectedAction)
    $("#modal-run-selected-run").click(buttonRunSelectedRunAction)
    $("#modal-run-selected-save-config").click(buttonRunSelectedSaveConfigAction)
    $("#button-add").click(buttonAddAction)
    $("#button-overwrite").click(buttonOverwriteAction)
    $("#button-delete").click(buttonDeleteAction)
    $("#button-up").click(buttonMoveUpAction)
    $("#button-down").click(buttonMoveDownAction)
    $("#button-change-group").click(buttonChangeGroupAction)
    $("#modal-move-to-task-group-move").click(buttonMoveGroupAction);
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function buttonReplayCountAction(e) {
    $("#modal-replay-config-title").html("Enter replay count.");
    $("#label-replay-config").html("New replay count:");
    $("#modal-replay-config-param-name").html("count");
    $("#new-replay-config-value").val($("#button-replay-count").html());
    $("#modal-replay-config").modal();
    utils_FocusInputForModal("new-replay-config-value");
}

function buttonReplayDelayAction(e) {
    $("#modal-replay-config-title").html("Enter replay delay.");
    $("#label-replay-config").html("New replay delay:");
    $("#modal-replay-config-param-name").html("delay");
    $("#new-replay-config-value").val($("#button-replay-delay").html());
    $("#modal-replay-config").modal();
    utils_FocusInputForModal("new-replay-config-value");
}

function buttonReplaySpeedupAction(e) {
    $("#modal-replay-config-title").html("Enter replay speedup.");
    $("#label-replay-config").html("New replay speedup:");
    $("#modal-replay-config-param-name").html("speedup");
    $("#new-replay-config-value").val($("#button-replay-speedup").html());
    $("#modal-replay-config").modal();
    utils_FocusInputForModal("new-replay-config-value");
}

function buttonReplayConfigSaveAction(e) {
    var value = $("#new-replay-config-value").val();
    var name = $("#modal-replay-config-param-name").html();

    var postData = {}
    postData[name] = value

    $.post("/internals/action/change-replay-config", JSON.stringify(postData), function(data) {
        var response = JSON.parse(data);
        $("#button-replay-count").html(response.count);
        $("#button-replay-delay").html(response.delay);
        $("#button-replay-speedup").html(response.speedup);
    }).fail(function(response) {
        alert('Error sending request to change replay config: ' + response.responseText);
    })
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function buttonCompileAction(e) {
    var source = getCurrentSourceCode();

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
    var source = getCurrentSourceCode();

    $.post("/internals/action/edit-source", source, function(status) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to compile: ' + response.responseText);
    });
}

function buttonReloadAction(e) {
    $.get("/internals/get/editted-source", function(data) {
        setCurrentSourceCode(data);
    }).fail(function(response) {
        alert('Error sending request get editted source: ' + response.responseText);
    });
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function buttonRunSelectedAction(e) {
    var runWithServerConfig = $("#menu-run-task-with-server-config").find("input").is(':checked');
    if (runWithServerConfig) {
        var index = utils_GetTableSelectedRowIndex("table-tasks");
        if (index == -1) {
            return;
        }

        $.post("/internals/action/run", JSON.stringify({id: getIdForTaskIndex(index)}), function(data) {
            // Nothing to do.
        }).fail(function(response) {
            alert('Error sending request to run task: ' + response.responseText);
        });
    } else {
        $.get("/internals/action/run-config/get", "", function(data) {
            $("#modal-run-selected-body").html(data);
            $("#modal-run-selected").modal();
        }).fail(function(response) {
            alert('Error sending request to get current run config: ' + response.responseText);
        });
    }
}

function buttonRunSelectedRunAction(e) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }

    $.post("/internals/action/run", JSON.stringify({
        id: getIdForTaskIndex(index),
        runConfig: {
            repeatCount: $("#new-run-selected-repeat-value").val(),
            delayMsBetweenRepeat: $("#new-run-selected-delay-value").val(),
        },
    }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to run task: ' + response.responseText);
    });
}

function buttonRunSelectedSaveConfigAction(e) {
    $.post("/internals/action/run-config/save", JSON.stringify({
        repeatCount: $("#new-run-selected-repeat-value").val(),
        delayMsBetweenRepeat: $("#new-run-selected-delay-value").val(),
    }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to run task: ' + response.responseText);
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
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }

    $.post("/internals/action/overwrite-task", JSON.stringify({task: getIdForTaskIndex(index)}), function(data) {
        refreshTasksWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error sending request to overwrite task: ' + response.responseText);
    });
}

function buttonDeleteAction(e) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }

    $.post("/internals/action/delete-task", JSON.stringify({task: getIdForTaskIndex(index)}), function(data) {
        refreshTasksWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error sending request to delete task: ' + response.responseText);
    });
}

function buttonMoveUpAction(e) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-up", JSON.stringify({task: getIdForTaskIndex(index)}), function(data) {
        refreshTasksWithDataAndIndex(data, index - 1);
    }).fail(function(response) {
        alert('Error sending request to move task up: ' + response.responseText);
    });
}

function buttonMoveDownAction(e) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-down", JSON.stringify({task: getIdForTaskIndex(index)}), function(data) {
        refreshTasksWithDataAndIndex(data, index + 1);
    }).fail(function(response) {
        alert('Error sending request to move task down: ' + response.responseText);
    });
}

function buttonChangeGroupAction(e) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    if (index == -1) {
        return;
    }
    $('#modal-move-to-task-group-row').val(index);
    $.get("/internals/get/rendered-task-groups-select-modal", JSON.stringify({task: getIdForTaskIndex(index)}), function(data) {
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
        if ($(this).is(':checked')) {
            groupIndex = i;
            return false;
        }
    })

    if (groupIndex == -1) {
        return;
    }
    var groups = $("#task-groups-dropdown").find("li");
    var groupId = groups.eq(groupIndex)[0].id;

    $.post("/internals/action/change-task-group-for-task", JSON.stringify({task: getIdForTaskIndex(taskIndex), group: groupId}), function(data) {
        refreshTasksWithData(data);
    }).fail(function(response) {
        alert('Error sending request to move task to new group: ' + response.responseText);
    });
}
