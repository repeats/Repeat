function registerTableTasks() {
    registerCells();

    $('#modal-task-name-save').click(newNameOnClick);
}

function registerCells() {
    var table = document.getElementById("table-tasks");
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $('#table-tasks').find("tr").not(":first");

    /* Create 'click' event handler for rows */
    rows.click(function(e) {
        /* Get current row */
        var row = $(this);

        /* Otherwise just highlight one row and clean others */
        rows.removeClass('table-highlight');
        row.addClass('table-highlight');
    });

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
}

function tableTaskOnClick(cell, row, col) {
    if (col == 0) { // Name.
        $('#modal-task-name-row').val(row);
        $('#new-task-name').val(cell.textContent);
        $('#modal-task-name').modal();
    }

    if (col == 1) { // Activation.
        window.location.assign("/task-activation?task=" + row);
    }

    if (col == 2) { // Enable/disable.
        $.post("/internals/toggle/task-enabled", JSON.stringify({task: row}), function(data) {
            refreshTasksWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling state: ' + response.responseText);
        });
    }

    fillSourceForTask(row);
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
    var tableElement = $("#table-tasks");
    tableElement.html(data);
    registerCells();
    setSelectedTask(index);
}

function setSelectedTask(index) {
    var rows = $('#table-tasks').find("tr").not(":first");
    rows.each(function(i) {
        if (i == index) {
            rows.removeClass('table-highlight');
            $(this).addClass('table-highlight');
            return false;
        }
    });
}

function getSelectedTaskIndex() {
    var rows = $('#table-tasks').find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}
