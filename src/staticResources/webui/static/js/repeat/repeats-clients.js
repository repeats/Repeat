function registerRepeatsClientPage(argument) {
    registerRepeatsClientTableRows();

    $("#repeats-clients-button-add").click(buttonAddRepeatsClientAction);
    $("#repeats-clients-button-delete").click(buttonDeleteRepeatsClientAction);
    $("#repeats-clients-button-run").click(buttonStartRepeatsClientAction);
    $("#repeats-clients-button-stop").click(buttonStopRepeatsClientAction);
    $("#modal-repeats-clients-button-add").click(buttonModalAddRepeatsClientAction);
}

function registerRepeatsClientTableRows() {
    var table = document.getElementById("repeats-clients-table");
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $("#repeats-clients-table").find("tr").not(":first");

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
    if (getSelectedIpcServiceIndex() != row) {
        return;
    }

    if (col == 3) { // Launch at startup.
        var row = getSelectedRepeatsClientRow();
        if (row == null) {
            return;
        }

        var val = cell.textContent == 'false' ? 'true' : 'false';
        var id = row.id.substring('repeats-client-id-'.length);
        var index = getSelectedIpcServiceIndex();
        $.post("/internals/repeats-remote-clients/set-launch-at-startup", JSON.stringify({
                id: id,
                value: val
            }), function(data) {
            refreshRepeatsClientTable(data, index);
        }).fail(function(response) {
            alert('Error setting launch at startup value for remote Repeats client: ' + response.responseText);
        });
    }
}

function setSelectedIndex(index) {
    var rows = $("#repeats-clients-table").find("tr").not(":first");
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
    var rows = $("#repeats-clients-table").find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}

function getSelectedRepeatsClientRow() {
    var rows = $("#repeats-clients-table").find("tr").not(":first");
    var index = getSelectedIpcServiceIndex();
    if (index == -1) {
        return null;
    }
    return rows[index];
}

function buttonAddRepeatsClientAction(e) {
    $("#new-repeats-client-address").val("localhost:9999");
    $("#modal-repeats-client-add").modal();
}

function buttonModalAddRepeatsClientAction(e) {
    var val = $("#new-repeats-client-address").val();

    $.post("/internals/repeats-remote-clients/add", JSON.stringify({server: val}), function(data) {
        refreshRepeatsClientTable(data, 9999); // Select the last row.
    }).fail(function(response) {
        alert('Error adding new remote Repeats client: ' + response.responseText);
    });
}

function buttonDeleteRepeatsClientAction(e) {
    var row = getSelectedRepeatsClientRow();
    if (row == null) {
        return;
    }

    var id = row.id.substring('repeats-client-id-'.length);
    $("#repeats-clients-button-delete").prop("disabled", true);
    $.post("/internals/repeats-remote-clients/delete", JSON.stringify({id: id}), function(data) {
        refreshRepeatsClientTable(data, 0);
        $("#repeats-clients-button-delete").prop("disabled", false);
    }).fail(function(response) {
        $("#repeats-clients-button-delete").prop("disabled", false);
        alert('Error deleting remote Repeats client: ' + response.responseText);
    });
}

function buttonStartRepeatsClientAction(e) {
    var row = getSelectedRepeatsClientRow();
    if (row == null) {
        return;
    }
    var index = getSelectedIpcServiceIndex();
    var id = row.id.substring('repeats-client-id-'.length);
    $.post("/internals/repeats-remote-clients/start", JSON.stringify({id: id}), function(data) {
        refreshRepeatsClientTable(data, index);
    }).fail(function(response) {
        alert('Error starting remote Repeats client: ' + response.responseText);
    });
}

function buttonStopRepeatsClientAction(e) {
    var row = getSelectedRepeatsClientRow();
    if (row == null) {
        return;
    }

    var id = row.id.substring('repeats-client-id-'.length);
    var index = getSelectedIpcServiceIndex();
    $("#repeats-clients-button-stop").prop("disabled", true);
    $.post("/internals/repeats-remote-clients/stop", JSON.stringify({id: id}), function(data) {
        refreshRepeatsClientTable(data, index);
        $("#repeats-clients-button-stop").prop("disabled", false);
    }).fail(function(response) {
        alert('Error stopping remote Repeats client: ' + response.responseText);
        $("#repeats-clients-button-stop").prop("disabled", false);
    });
}

function refreshRepeatsClientTable(data, index) {
    $("#repeats-clients-table").html(data);
    setSelectedIndex(index);
    registerRepeatsClientTableRows();
}
