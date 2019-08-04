function registerGlobalConfigsPage(argument) {
    registerToolsClientsTableRows();

    $("#button-save").click(buttonSaveAction);
}

function registerToolsClientsTableRows() {
    var table = document.getElementById("tools-clients-table");
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $("#tools-clients-table").find("tr").not(":first");

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
                tableRepeatsClientsOnClick(cell, i - 1, j);
            };
        }(table.rows[i].cells[j], i, j);
    }
}

function tableRepeatsClientsOnClick(cell, row, col) {
    if (getSelectedToolsClientIndex() != row) {
        return;
    }

    if (col == 2) { // Enabled.
        var val = cell.textContent == 'false' ? 'true' : 'false';
        cell.innerHTML = val;
    }
}


function buttonSaveAction(e) {
    var rows = $("#tools-clients-table").find("tr").not(":first");
    var ids = [];

    for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        var id = row.id.substring("tools-clients-id-".length);
        if (row.cells[2].innerHTML == "true") {
            ids.push(id);
        }
    }

    $.post("/internals/global-configs/tools-config/set-clients", JSON.stringify({ clients: ids }), function(data) {
        refreshRepeatsClientTable(data, 0);
    }).fail(function(response) {
        alert('Error setting tools clients: ' + response.responseText);
    });
}

function setSelectedIndex(index) {
    var rows = $("#tools-clients-table").find("tr").not(":first");
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

function getSelectedToolsClientIndex() {
    var rows = $("#tools-clients-table").find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}

function refreshRepeatsClientTable(data, index) {
    $("#tools-clients-table").html(data);
    setSelectedIndex(index);
    registerToolsClientsTableRows();
}
