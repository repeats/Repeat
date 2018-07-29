function registerIpcPage(argument) {
    registerIpcTableRows();

    $("#modal-ipc-port-save").click(buttonSavePortAction);
    $("#button-run").click(buttonRunAction);
    $("#button-stop").click(buttonStopAction);
}

function registerIpcTableRows() {
    var table = document.getElementById("ipcs-table");
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $("#ipcs-table").find("tr").not(":first");

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
                tableIpcOnClick(cell, i - 1, j);
            };
        }(table.rows[i].cells[j], i, j);
    }
}

function tableIpcOnClick(cell, row, col) {
    if (col == 1) { // Port.
        // Hard code for now.
        if (row > 2) {
            return;
        }

        // Modify port.
        $("#modal-ipc-service-row").val(row);
        $("#new-ipc-port").val(cell.textContent);
        $("#modal-ipc-port").modal();
        utils_FocusInputForModal("new-ipc-port");
    }
    if (col == 3) { // Launch at startup.
        $.post("/internals/toggle/ipc-service-launch-at-startup", JSON.stringify({ipc: row}), function(data) {
            refreshIpcsWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling launch at startup: ' + response.responseText);
        });
    }
}

function buttonSavePortAction(e) {
    var row = $("#modal-ipc-service-row").val();
    var newPort = $("#new-ipc-port").val();

    $.post("/internals/modify/ipc-service-port", JSON.stringify({ipc: row, port: newPort}), function(data) {
        refreshIpcsWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing port: ' + response.responseText);
    });
}

function buttonRunAction(e) {
    var index = getSelectedIpcServiceIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/run-ipc-service", JSON.stringify({ipc: index}), function(data) {
        refreshIpcsWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error running IPC service: ' + response.responseText);
    });
}

function buttonStopAction(e) {
    var index = getSelectedIpcServiceIndex();
    if (index == -1) {
        return;
    }

    $.post("/internals/action/stop-ipc-service", JSON.stringify({ipc: index}), function(data) {
        refreshIpcsWithDataAndIndex(data, index);
    }).fail(function(response) {
        alert('Error stopping IPC service: ' + response.responseText);
    });
}

function refreshIpcsWithDataAndIndex(data, index) {
    $("#ipcs-table").html(data);
    setSelectedIndex(index);
    registerIpcTableRows();
}

function setSelectedIndex(index) {
    var rows = $("#ipcs-table").find("tr").not(":first");
    if (index < 0) {
        index = 0;
    }
    if (index >= rows.length) {
        index = rows.length - 1;
    }
    
    rows.each(function(i) {
        if (index == i) {
            rows.removeClass('table-highlight');
            $(this).addClass('table-highlight');
            return false;
        }
    });
}

function getSelectedIpcServiceIndex() {
    var rows = $("#ipcs-table").find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}
