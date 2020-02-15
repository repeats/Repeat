function registerTableTasks() {
    registerCells();

    $('#modal-task-name-save').click(newNameOnClick);
    $('button[id^=\'button-show-task-id-\']').click(showActionId);
}

function registerCells() {
    utils_TableHighlight("table-tasks");
    utils_TableOnclick("table-tasks", tableTaskOnClick);

    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var table = document.getElementById("table-tasks");
    var rows = $('#table-tasks').find("tr").not(":first");
    if (rows.length > 0) {
        var lastCellIndex = table.rows[1].cells.length - 1;
        table.rows[1].cells[lastCellIndex].click();
    }
}

function tableTaskOnClick(cell, row, col) {
    var isFocused = utils_GetTableSelectedRowIndex("table-tasks") == row;

    if (!isFocused || col != 1) {
        fillSourceForTask(row);
    }

    if (!isFocused) {
        return;
    }

    if (col == 0) { // Name.
        $("#modal-task-name-row").val(row);
        $("#new-task-name").val(cell.textContent);
        $("#modal-task-name").modal();
        utils_FocusInputForModal("new-task-name");
    }

    if (col == 2) { // Activation.
        window.location.assign("/task-activation?task=" + getIdForTaskIndex(row));
    }

    if (col == 3) { // Enable/disable.
        $.post("/internals/toggle/task-enabled", JSON.stringify({task: getIdForTaskIndex(row)}), function(data) {
            refreshTasksWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling state: ' + response.responseText);
        });
    }
}

function showActionId(e) {
    var taskId = $(this)[0].id.slice('button-show-task-id-'.length);
    $("#current-task-id").val(taskId);
    $("#modal-task-id-button").modal();
    utils_SelectInputForModal("current-task-id");
}

function newNameOnClick(e) {
    var row = $('#modal-task-name-row').val();
    var newName = $('#new-task-name').val();

    $.post("/internals/modify/task-name", JSON.stringify({task: getIdForTaskIndex(row), name: newName}), function(data) {
        refreshTasksWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing name: ' + response.responseText);
    });
}

function refreshTasksWithData(data) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    refreshTasksWithDataAndIndex(data, index);
}

function refreshTasksWithDataAndIndex(data, index) {
    _rememberLastScrollPosition();
    var tableElement = $("#table-tasks");
    tableElement.html(data);
    registerCells();
    utils_SetTableSelectedIndex("table-tasks", index);
    _setLastToLastScrollPosition();
}

function getIdForTaskIndex(index) {
    return $('button[id^=\'button-show-task-id-\']').eq(index)[0].id.substring('button-show-task-id-'.length);
}

var _lastScrollPosition = 0;

function _rememberLastScrollPosition() {
    _lastScrollPosition = $("#table-tasks-body")[0].scrollTop;
}

function _setLastToLastScrollPosition() {
    $("#table-tasks-body")[0].scrollTop = _lastScrollPosition;
}
