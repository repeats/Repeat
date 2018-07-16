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
    $.post("/internals/action/run-compiled-task", function(status) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error sending request to run compiled action: ' + response.responseText);
    });
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
    $.post("/internals/action/add-task", function(status) {
        refreshTasksWithoutIndex();
    }).fail(function(response) {
        alert('Error sending request to add task: ' + response.responseText);
    });
}

function buttonOverwriteAction(e) {
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/overwrite-task", JSON.stringify({task: index}), function(status) {
        refreshTasksWithSelectedIndex(index);
    }).fail(function(response) {
        alert('Error sending request to overwrite task: ' + response.responseText);
    });
}

function buttonDeleteAction(e) {
    console.log("buttonDeleteAction");
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/delete-task", JSON.stringify({task: index}), function(status) {
        refreshTasksWithSelectedIndex(index);
    }).fail(function(response) {
        alert('Error sending request to delete task: ' + response.responseText);
    });
}

function buttonMoveUpAction(e) {
    console.log("buttonMoveUpAction");
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-up", JSON.stringify({task: index}), function(status) {
        refreshTasksWithSelectedIndex(index - 1);
    }).fail(function(response) {
        alert('Error sending request to move task up: ' + response.responseText);
    });
}

function buttonMoveDownAction(e) {
    console.log("buttonMoveDownAction");
    var index = getSelectedTaskIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/move-task-down", JSON.stringify({task: index}), function(status) {
        refreshTasksWithSelectedIndex(index + 1);
    }).fail(function(response) {
        alert('Error sending request to move task down: ' + response.responseText);
    });
}

function buttonChangeGroupAction(e) {
    console.log("buttonChangeGroupAction");
}
