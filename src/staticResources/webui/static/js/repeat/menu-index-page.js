function registerMenuIndexPageActions() {
    $("#menu-save-config").click(menuSaveConfigAction);
    $("#menu-clean-unused-sources").click(menuCleanUnusedSourcesAction);
    $("#menu-force-exit").click(menuForceExitAction);
    $("#menu-exit").click(menuExitAction);

    $("#menu-halt-all-tasks").click(menuHaltAllTasksAction);
    $("#menu-generate-source").click(menuGenerateSourceAction);
    $("#menu-compiling-languages").click(menuCompilingLanguagesAction);

    $("#menu-compiler-path").click(menuSetCompilerPathAction);
    $("#menu-configure-compiler").click(menuConfigureCompilerAction);
    $("#menu-record-mouse-click-only").click(menuRecordMouseClickOnlyAction);
    $("#menu-halt-task-by-escape").click(menuHaltTaskByEscapeAction);
    $("#menu-execute-on-release").click(menuExecuteOnReleaseAction);
    $("#menu-debug-level").click(menuDebugLevelAction);
    $("menu-use-tray-icon").click(menuUseTrayIconAction);

    $("#modal-force-exit-confirm-ok").click(forceExitAfterConfirmed);
    $("#modal-compiling-languages-select").click(selectCompilingLanguageAction);
    $("#modal-set-compiler-path-save").click(setCompilerPathAction);
    $("#modal-configure-compiler-save").click(setCompilerConfigurationAction);
    $("#modal-debug-level-save").click(setDebugLevelAction);
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

function menuSetCompilerPathAction(e) {
    $.get("/internals/menu/settings/get-compiler-path", function(data) {
        var path = data;
        $("#new-compiler-path").val(data);
        $("#modal-set-compiler-path").modal();
        utils_FocusInputForModal("new-compiler-path");
    }).fail(function(response) {
        if (response.status != 400) {
            alert('Error getting path for compiler: ' + response.responseText);
        }
    })
}

function setCompilerPathAction(e) {
    var path = $("#new-compiler-path").val();
    $.post("/internals/menu/settings/set-compiler-path", JSON.stringify({ path: path }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error setting compiler path: ' + response.responseText);
    });
}

function menuConfigureCompilerAction(e) {
    $.get("/internals/menu/settings/compiler-config-options", function(data) {
        $("#modal-configure-compiler-body").html(data);
        $("#modal-configure-compiler").modal();

        // Do some special focusing for each language.
        if ($("#modal-language-config-language-name").html() == "Java") {
            utils_FocusTextAreaForModal("modal-language-config-language-name");
        }
    }).fail(function(response) {
        if (response.status != 400) {
            alert('Error getting current compiler configuration: ' + response.responseText);
        }
    });
}

function setCompilerConfigurationAction(e) {
    var language = $("#modal-language-config-language-name").html();
    if (language != "Java") {
        return;
    }
    var classPaths = $("#new-java-compiler-class-paths").val();

    $.post("/internals/menu/settings/set-compiler-config", JSON.stringify({classPaths: classPaths}), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error setting current compiler configuration: ' + response.responseText);
    });
}

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

function menuCompilingLanguagesAction(e) {
    $.get("/internals/menu/tools/get-compiling-languages-options", function(data) {
        $("#modal-compiling-languages-body").html(data);
        $("#modal-compiling-languages").modal();
    }).fail(function(response) {
        alert('Error getting compiling languages options: ' + response.responseText);
    });
}

function selectCompilingLanguageAction(e) {
    var index = getSelectedRadioInputFromModalBody("modal-compiling-languages-body");
    if (index == -1) {
        return;
    }

    $.post("/internals/menu/tools/set-compiling-language", JSON.stringify({ index: index }), function(data) {
        _internalEditor.getDoc().setValue(data);
    }).fail(function(response) {
        alert('Error setting compiling languages with index ' + index + ':' + response.responseText);
    });
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function menuRecordMouseClickOnlyAction(e) {
    menuSetBooleanSettingAction("menu-record-mouse-click-only", "/internals/menu/settings/record-mouse-click-only");
}

function menuHaltTaskByEscapeAction(e) {
    menuSetBooleanSettingAction("menu-halt-task-by-escape", "/internals/menu/settings/halt-task-by-escape");
}

function menuExecuteOnReleaseAction(e) {
    menuSetBooleanSettingAction("menu-execute-on-release", "/internals/menu/settings/execute-on-release");
}

function menuDebugLevelAction(e) {
    $.get("/internals/menu/settings/debug-level-options", function(data) {
        $("#modal-debug-level-body").html(data);
        $("#modal-debug-level").modal();
    }).fail(function(response) {
        alert('Error getting debug levels options: ' + response.responseText);
    });
}

function setDebugLevelAction(e) {
    var index = getSelectedRadioInputFromModalBody("modal-debug-level-body");
    if (index == -1) {
        return;
    }

    $.post("/internals/menu/settings/set-debug-level", JSON.stringify({ index: index }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error setting debug level with index ' + index + ':' + response.responseText);
    });
}

function menuUseTrayIconAction(e) {
    menuSetBooleanSettingAction("menu-use-tray-icon", "/internals/menu/settings/use-tray-icon");
}

function menuSetBooleanSettingAction(elementId, endpoint) {
    var enabled = $("#" + elementId).find("input").is(':checked') + "";
    $.post(endpoint, JSON.stringify({ value: enabled }), function(data) {
        // Nothing to do.
    }).fail(function(response) {
        alert('Error setting config (page refresh is needed): ' + response.responseText);
    });
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

function getSelectedRadioInputFromModalBody(modalBodyId) {
    var rows = $('#' + modalBodyId).find("input");
    var index = -1;
    rows.each(function(i) {
        if ($(this).is(":checked")) {
            index = i;
            return false;
        }
    });

    return index;
}
