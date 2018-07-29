function registerTaskGroups() {
    $("#button-add").click(buttonAddTaskGroupAction);
    $("#button-delete").click(buttonDeleteTaskGroupAction);
    $("#button-up").click(buttonMoveTaskGroupUpAction);
    $("#button-down").click(buttonMoveTaskGroupDownAction);

    $("#modal-new-task-group-save").click(buttonAddNewTaskGroupAction);
    $("#modal-task-group-name-save").click(buttonSaveTaskGroupNameAction);

    registerTaskGroupCells();
}

function registerTaskGroupCells() {
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $('#table-task-groups').find("tr").not(":first");

    /* Create 'click' event handler for rows */
    rows.on('click', function(e) {
        /* Get current row */
        var row = $(this);

        /* Otherwise just highlight one row and clean others */
        rows.removeClass('table-highlight');
        row.addClass('table-highlight');
    });

    var table = document.getElementById("table-task-groups");
    // Start from row = 1 since row = 0 is the headings.
    for (var i = 1; i < table.rows.length; i++) {
        for (var j = 0; j < table.rows[i].cells.length; j++)
        table.rows[i].cells[j].onclick = function(cell, i, j) {
            return function() {
                // Minus one so that row index starts from 0.
                tableTaskGroupsOnClick(cell, i - 1, j);
            };
        }(table.rows[i].cells[j], i, j);
    }
}

function tableTaskGroupsOnClick(cell, row, col) {
    if (col == 0) { // Name.
        $('#modal-task-group-name-row').val(row);
        $('#new-task-group-name').val(cell.textContent);
        $('#modal-task-group-name').modal();
        utils_FocusInputForModal("new-task-group-name");
    }

    if (col == 2) { // Enabled.
        $.post("/internals/toggle/task-group-enabled", JSON.stringify({group: row}), function(data) {
            refreshTaskGroupsWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling task group enabled: ' + response.responseText);
        });
    }

    if (col == 3) { // Selected.
        $.post("/internals/action/switch-task-group", JSON.stringify({group: row, render: "groups"}), function(data) {
            refreshTaskGroupsWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling task group enabled: ' + response.responseText);
        });
    }
}

function buttonSaveTaskGroupNameAction(e) {
    var row = getSelectedTaskGroupIndex();
    var name = $("#new-task-group-name").val();

    $.post("/internals/action/change-task-group-name", JSON.stringify({group: row, name: name}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing task group name: ' + response.responseText);
    });
}

function buttonAddTaskGroupAction(e) {
    $('#name-for-new-task-group').val("");
    $('#modal-new-task-group').modal();
    utils_FocusInputForModal("name-for-new-task-group");
}

function buttonAddNewTaskGroupAction(e) {
    var row = getSelectedTaskGroupIndex();
    var name = $("#name-for-new-task-group").val();

    $.post("/internals/action/add-task-group", JSON.stringify({name: name}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error adding task group: ' + response.responseText);
    });
}

function buttonDeleteTaskGroupAction(e) {
    var row = getSelectedTaskGroupIndex();
    if (row == -1) {
        return;
    }

    $.post("/internals/action/delete-task-group", JSON.stringify({group: row}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error deleting task group: ' + response.responseText);
    });
}

function buttonMoveTaskGroupUpAction(e) {
    var row = getSelectedTaskGroupIndex();
    if (row == -1) {
        return;
    }

    $.post("/internals/action/move-task-group-up", JSON.stringify({group: row}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row - 1);
    }).fail(function(response) {
        alert('Error moving task group up: ' + response.responseText);
    });
}

function buttonMoveTaskGroupDownAction(e) {
    var row = getSelectedTaskGroupIndex();
    if (row == -1) {
        return;
    }

    $.post("/internals/action/move-task-group-down", JSON.stringify({group: row}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row + 1);
    }).fail(function(response) {
        alert('Error moving task group down: ' + response.responseText);
    });
}

function newNameOnClick(e) {
    var row = $('#modal-task-group-name-row').val();
    var newName = $('#new-task-group-name').val();

    $.post("/internals/modify/task-name", JSON.stringify({group: row, name: newName}), function(data) {
        refreshTaskGroupsWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing task group name: ' + response.responseText);
    });
}

function refreshTaskGroupsWithData(data) {
    var index = getSelectedTaskGroupIndex();
    refreshTaskGroupsWithDataAndIndex(data, index);
}

function refreshTaskGroupsWithDataAndIndex(data, index) {
    var tableElement = $("#table-task-groups");
    tableElement.html(data);
    registerTaskGroupCells();
    setSelectedTaskGroup(index);
}

function setSelectedTaskGroup(index) {
    var rows = $('#table-task-groups').find("tr").not(":first");
    if (index < 0) {
        index = 0;
    }
    if (index >= rows.length) {
        index = rows.length - 1;
    }

    rows.each(function(i) {
        if (i == index) {
            rows.removeClass('table-highlight');
            $(this).addClass('table-highlight');
            return false;
        }
    });
}

function getSelectedTaskGroupIndex() {
    var rows = $('#table-task-groups').find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}
