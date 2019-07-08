function registerTableTasks() {
    registerCells();

    $('#modal-task-name-save').click(newNameOnClick);
    $('button[id^=\'button-show-task-id-\']').click(showActionId);
}

function registerCells() {
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = _getRows();

    /* Create 'click' event handler for rows */
    rows.click(function(e) {
        /* Get current row */
        var row = $(this);

        /* Otherwise just highlight one row and clean others */
        rows.removeClass('table-highlight');
        row.addClass('table-highlight');
    });

    var table = document.getElementById("table-tasks");
    // Start from row = 1 since row = 0 is the headings.
    for (var i = 1; i < table.rows.length; i++) {
        for (var j = 0; j < table.rows[i].cells.length; j++)
        table.rows[i].cells[j].onclick = function(cell, i, j) {
            return function() {
                // Minus one so that row index starts from 0.
                tableTaskOnClick(cell, i - 1, j);
            };
        }(table.rows[i].cells[j], i, j);
    }

    if (rows.length > 0) {
        var lastCellIndex = table.rows[1].cells.length - 1;
        table.rows[1].cells[lastCellIndex].click();
    }
}

function tableTaskOnClick(cell, row, col) {
    var isFocused = getSelectedTaskIndex() == row;

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
        window.location.assign("/task-activation?task=" + row);
    }

    if (col == 3) { // Enable/disable.
        $.post("/internals/toggle/task-enabled", JSON.stringify({task: row}), function(data) {
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

    $.post("/internals/modify/task-name", JSON.stringify({task: row, name: newName}), function(data) {
        refreshTasksWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing name: ' + response.responseText);
    });
}

function refreshTasksWithData(data) {
    var index = getSelectedTaskIndex();
    refreshTasksWithDataAndIndex(data, index);
}

function refreshTasksWithDataAndIndex(data, index) {
    _rememberLastScrollPosition();
    var tableElement = $("#table-tasks");
    tableElement.html(data);
    registerCells();
    setSelectedTask(index);
    _setLastToLastScrollPosition();
}

function setSelectedTask(index) {
    var rows = _getRows();
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

function getSelectedTaskIndex() {
    var rows = _getRows();
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}

function _getRows() {
    return $('#table-tasks').find("tr").not(":first");
}

var _lastScrollPosition = 0;

function _rememberLastScrollPosition() {
    _lastScrollPosition = $("#table-tasks-body")[0].scrollTop;
}

function _setLastToLastScrollPosition() {
    $("#table-tasks-body")[0].scrollTop = _lastScrollPosition;
}
