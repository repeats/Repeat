function utils_FocusInputForModal(elementId) {
    // We need to wait for the modal to appear first.
    setTimeout(function() {
        // Trick to set focus on the field.
        $("#" + elementId).focus().val($("#" + elementId).val());
    }, 500);
}

function utils_SelectInputForModal(elementId) {
    // We need to wait for the modal to appear first.
    setTimeout(function() {
        // Trick to set focus on the field.
        $("#" + elementId).focus().val($("#" + elementId).val());
        // Then select the input.
        $("#" + elementId).select();
    }, 500);
}

function utils_FocusTextAreaForModal(elementId) {
    var el = $("#" + elementId);

    if (typeof el.selectionStart == "number") {
        el.selectionStart = el.selectionEnd = el.value.length;
    } else if (typeof el.createTextRange != "undefined") {
        el.focus();
        var range = el.createTextRange();
        range.collapse(false);
        range.select();
    }
}

function utils_SuggestPaths(elementId) {
    var input = $("#" + elementId);
    input.autocomplete({ source: [] });

    input.keypress(function(){
        var val = input.val();
        $.get("/internals/get/path-suggestion?path=" + encodeURIComponent(val), function(data) {
            var newSources = JSON.parse(data);
            input.autocomplete("option", "source", newSources.paths);
        });
    });
}

/********************************************************************************/
/**************************Common table operations ******************************/
/********************************************************************************/

utilMutiSelectTable = function() {
    var clearRows = function(rows) {
        rows.removeClass('table-highlight');
    }

    var toggleRow = function(cell) {
        cell.classList.toggle('table-highlight');
    }

    var highlightRow = function(cell) {
        cell.classList.add('table-highlight');
    }

    var setSelectedRow = function(tableId, index) {
        var rows = $("#" + tableId).find("td");
        if (index < 0 || index > rows.length) {
            return;
        }

        console.log(rows[index]);
        rows[index].classList.add('table-highlight');
    }

    var onClick = function(e, tableId, rows, cell, row, col) {
        if (e.ctrlKey) {
            toggleRow(cell);
            return;
        }
        if (e.shiftKey) {
            var firstIndex = firstSelected(tableId);
            if (firstIndex == -1) {
                firstIndex = 0;
            }
            var low = Math.min(firstIndex, row);
            var high = Math.max(firstIndex, row);
            if (low == high) {
                return;
            }

            for (var i = low; i <= high; i++) {
                rows[i].classList.add('table-highlight');
            }
            return;
        }

        clearRows(rows);
        var isSelected = cell.classList.contains('table-highlight');
        if (!isSelected) {
            highlightRow(cell);
        }
    }

    var register = function(tableId) {
        var rows = $("#" + tableId).find("td");
        var table = document.getElementById(tableId);
        if (table == null) {
            return;
        }

        for (var i = 1; i < table.rows.length; i++) {
            for (var j = 0; j < table.rows[i].cells.length; j++)
            table.rows[i].cells[j].onclick = function(cell, i, j) {
                return function(e) {
                    // Minus one so that row index starts from 0.
                    onClick(e, tableId, rows, cell, i - 1, j);
                };
            }(table.rows[i].cells[j], i, j);
        }
    }


    var firstSelected = function(tableId) {
        var rows = $("#" + tableId).find("td");
        for (var i = 0; i < rows.length; i++) {
            if (rows[i].classList.contains('table-highlight')) {
                return i;
            }
        }
        return -1;
    }

    var lastSelected = function(tableId) {
        var rows = $("#" + tableId).find("td");
        for (var i = rows.length - 1; i >= 0; i--) {
            if (rows[i].classList.contains('table-highlight')) {
                return i;
            }
        }
        return -1;
    }

    var allSelected = function(tableId) {
        var rows = $("#" + tableId).find("td");
        var selectedIndices = [];
        for (var i = rows.length - 1; i >= 0; i--) {
            if (rows[i].classList.contains('table-highlight')) {
                selectedIndices.push(i);
            }
        }
        return selectedIndices;
    }

    return {
        register: register,
        firstSelected: firstSelected,
        lastSelected: lastSelected,
        allSelected: allSelected,
        setSelectedRow: setSelectedRow,
    }
}()

function utils_TableHighlight(tableId) {
    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var rows = $("#" + tableId).find("tr").not(":first");

    /* Create 'click' event handler for rows */
    rows.click(function(e) {
        /* Get current row */
        var row = $(this);

        /* Otherwise just highlight one row and clean others */
        rows.removeClass('table-highlight');
        row.addClass('table-highlight');
    });
}

// onClickFunc takes in a cell element, a row index and a column index.
// Row index and column index start from 0.
function utils_TableOnclick(tableId, onClickFunc) {
    var table = document.getElementById(tableId);

    // Start from row = 1 since row = 0 is the headings.
    for (var i = 1; i < table.rows.length; i++) {
        for (var j = 0; j < table.rows[i].cells.length; j++)
        table.rows[i].cells[j].onclick = function(cell, i, j) {
            return function() {
                // Minus one so that row index starts from 0.
                onClickFunc(cell, i - 1, j);
            };
        }(table.rows[i].cells[j], i, j);
    }
}

function utils_SetTableSelectedIndex(tableId, index) {
    var rows = $("#" + tableId).find("tr").not(":first");
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

// Returns -1 if nothing is selected.
function utils_GetTableSelectedRowIndex(tableId) {
    var rows = $("#" + tableId).find("tr").not(":first");
    var index = -1;
    rows.each(function(i) {
        if ($(this).hasClass("table-highlight")) {
            index = i;
            return false;
        }
    });

    return index;
}

// Returns null if nothing is selected.
function utils_GetTableSelectedRow(tableId) {
    var rows = $("#" + tableId).find("tr").not(":first");
    var index = utils_GetTableSelectedRowIndex(tableId);
    if (index == -1) {
        return null;
    }
    return rows[index];
}
