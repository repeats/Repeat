function registerGlobalConfigsPage(argument) {
    registerClientsTableRows("tools-clients-table");
    registerClientsTableRows("core-clients-table");
    registerClientsTableRows("repeats-remote-compiler-clients-table");

    $("#button-save-tools-config").click(buttonSaveToolsConfigAction);
    $("#button-save-core-config").click(buttonSaveCoreConfigAction);
    $("#button-save-remote-repeats-compilers-config").click(buttonSaveRemoteRepeatsCompilerConfigAction);
}

function registerClientsTableRows(tableId) {
    utils_TableHighlight(tableId);
    utils_TableOnclick(tableId, tableRepeatsClientsOnClick(tableId));
}

function tableRepeatsClientsOnClick(tableId) {
    return function(cell, row, col) {
        if (utils_GetTableSelectedRowIndex(tableId) != row) {
            return;
        }

        if (col == 2) { // Enabled.
            var val = cell.textContent == 'false' ? 'true' : 'false';
            cell.innerHTML = val;
        }
    }
}

function buttonSaveToolsConfigAction(e) {
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
        refreshRepeatsClientTable("tools-clients-table", data, 0);
    }).fail(function(response) {
        alert('Error setting tools clients: ' + response.responseText);
    });
}

function buttonSaveCoreConfigAction(e) {
    var rows = $("#core-clients-table").find("tr").not(":first");
    var ids = [];

    for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        var id = row.id.substring("core-clients-id-".length);
        if (row.cells[2].innerHTML == "true") {
            ids.push(id);
        }
    }

    $.post("/internals/global-configs/core-config/set-clients", JSON.stringify({ clients: ids }), function(data) {
        refreshRepeatsClientTable("core-clients-table", data, 0);
    }).fail(function(response) {
        alert('Error setting keyboard + mouse clients: ' + response.responseText);
    });
}

function buttonSaveRemoteRepeatsCompilerConfigAction(e) {
    var rows = $("#repeats-remote-compiler-clients-table").find("tr").not(":first");
    var ids = [];

    for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        var id = row.id.substring("remote-repeats-compiler-clients-id-".length);
        if (row.cells[2].innerHTML == "true") {
            ids.push(id);
        }
    }

    $.post("/internals/global-configs/remote-repeats-compiler-config/set-clients", JSON.stringify({ clients: ids }), function(data) {
        refreshRepeatsClientTable("repeats-remote-compiler-clients-table", data, 0);
    }).fail(function(response) {
        alert('Error setting remote Repeats compiler clients: ' + response.responseText);
    });
}

function refreshRepeatsClientTable(tableId, data, index) {
    $("#" + tableId).html(data);
    utils_SetTableSelectedIndex(tableId, index);
    registerClientsTableRows(tableId);
}
