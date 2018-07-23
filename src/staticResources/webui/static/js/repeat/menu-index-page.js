function registerMenuIndexPageActions() {
    $("#menu-save-config").click(menuSaveConfigAction);
    $("#menu-clean-unused-sources").click(menuCleanUnusedSourcesAction);
    $("#menu-force-exit").click(menuForceExitAction);
    $("#menu-exit").click(menuExitAction);

    $("#menu-halt-all-tasks").click(menuHaltAllTasksAction);
    $("#menu-generate-source").click(menuGenerateSourceAction);
    $("#menu-record-mouse-click-only").click(menuRecordMouseClickOnlyAction);
    $("#menu-halt-task-by-escape").click(menuHaltTaskByEscapeAction);
    $("#menu-execute-on-release").click(menuExecuteOnReleaseAction);
    $("#menu-compiling-languages").click(menuCompilingLanguagesAction);

    $("#modal-force-exit-confirm-ok").click(forceExitAfterConfirmed);
    $("#modal-compiling-languages-select").click(selectCompilingLanguageAction);
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function menuSaveConfigAction(e) {
    $.post("/internals/menu/file/save-config", function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error saving config: ' + response.responseText);
    });
}

function menuCleanUnusedSourcesAction(e) {
    $.post("/internals/menu/file/clean-unused-sources", function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error cleaning unused sources: ' + response.responseText);
    });
}

function menuForceExitAction(e) {
    $("#modal-force-exit-confirm").modal();
}

function forceExitAfterConfirmed() {
    $.post("/internals/menu/file/force-exit", function(data) {
        alert(data);
    }).fail(function(response) {
        alert('Error initiating exit sequence: ' + response.responseText);
    });
}

function menuExitAction(e) {
    $.post("/internals/menu/file/exit", function(data) {
        alert(data);
    }).fail(function(response) {
        alert('Error initiating exit sequence: ' + response.responseText);
    });
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function menuHaltAllTasksAction() {
    $.post("/internals/menu/tools/halt-all-tasks", function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error halting all tasks: ' + response.responseText);
    });
}

function menuGenerateSourceAction() {
    $.post("/internals/menu/tools/generate-source", function(data) {
        _internalEditor.getDoc().setValue(data);
    }).fail(function(response) {
        alert('Error generating source: ' + response.responseText);
    });
}

function menuRecordMouseClickOnlyAction(e) {
    menuSetBooleanSettingAction("menu-record-mouse-click-only", "/internals/menu/settings/record-mouse-click-only");
}

function menuHaltTaskByEscapeAction(e) {
    menuSetBooleanSettingAction("menu-halt-task-by-escape", "/internals/menu/settings/halt-task-by-escape");
}

function menuExecuteOnReleaseAction(e) {
    menuSetBooleanSettingAction("menu-execute-on-release", "/internals/menu/settings/execute-on-release");
}

function menuCompilingLanguagesAction(e) {
    $.get("/internals/menu/tools/get-compiling-languages-options", function(data) {
        $("#modal-compiling-languages-body").html(data);
        $("#modal-compiling-languages").modal();
    }).fail(function(response) {
        alert('Error getting compiling languages options: ' + response.responseText);
    });
}

function selectCompilingLanguageAction(e) {
    var rows = $('#modal-compiling-languages-body').find("input");
    var index = -1;
    rows.each(function(i) {
        if ($(this).is(":checked")) {
            index = i;
            return false;
        }
    });

    if (index == -1) {
        return;
    }

    $.post("/internals/menu/tools/set-compiling-language", JSON.stringify({ index: index }), function(data) {
        _internalEditor.getDoc().setValue(data);
    }).fail(function(response) {
        alert('Error setting compiling languages with index ' + index + ':' + response.responseText);
    });
}

function menuSetBooleanSettingAction(elementId, endpoint) {
    var enabled = $("#" + elementId).find("input").is(':checked') + "";
    $.post(endpoint, JSON.stringify({ value: enabled }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error setting config (page refresh is needed): ' + response.responseText);
    });
}
