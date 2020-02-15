var _internalEditor = null;

function registerSourceTextArea() {
    var editor = CodeMirror.fromTextArea(document.getElementById("source-code"), {
      lineNumbers: true,
      mode: "text/html",
      matchBrackets: true,
    });
    _internalEditor = editor;

    var sourceCodeTextArea = $('#source-code');
    getSourceTemplate();

    editor.on("change", function(editor) {
        if (getCurrentSourceCode() == "") {
            getSourceTemplate();
        }
    });
}

function getSourceTemplate(editor) {
    $.get("/internals/get/source-templates", function(data) {
        setCurrentSourceCode(data);
    }).fail(function(response) {
        alert('Error getting source template: ' + response.responseText);
    });
}

function fillSourceForTask(index) {
    $.post("/internals/set/selected-task", JSON.stringify({ "task" : index }), function(data) {
        setCurrentSourceCode(data);
    }).fail(function(response) {
        alert('Error getting source code for task: ' + response.responseText);
    });
}

function getCurrentSourceCode() {
    return _internalEditor.getValue();
}

function setCurrentSourceCode(source) {
    _internalEditor.getDoc().setValue(source);
}
