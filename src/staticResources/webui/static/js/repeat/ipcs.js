function registerIpcPage(argument) {
    registerIpcTableRows();

    $("#modal-ipc-port-save").click(buttonSavePortAction);
    $("#button-run").click(buttonRunAction);
    $("#button-stop").click(buttonStopAction);
}

function registerIpcTableRows() {
    utils_TableHighlight("ipcs-table");
    utils_TableOnclick("ipcs-table", tableIpcOnClick);
}

function tableIpcOnClick(cell, row, col) {
    if (utils_GetTableSelectedRowIndex("ipcs-table") != row) {
        return;
    }

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
    var index = utils_GetTableSelectedRowIndex("ipcs-table");
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
    var index = utils_GetTableSelectedRowIndex("ipcs-table");
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
    utils_SetTableSelectedIndex("ipcs-table", index);
    registerIpcTableRows();
}
