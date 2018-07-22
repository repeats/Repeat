var _internalEditor = null;

function registerSourceTextArea() {
    var editor = CodeMirror.fromTextArea(document.getElementById("source-code"), {
      lineNumbers: true,
      mode: "text/html",
      matchBrackets: true,
    });
    _internalEditor = editor;

    var sourceCodeTextArea = $('#source-code');
    getSourceTemplate(editor);

    editor.on("change", function(editor) {
        if (editor.getValue() == "") {
            getSourceTemplate(editor);
        }
    });
}

function getSourceTemplate(editor) {
    $.get("/internals/get/source-templates", function(data) {
        editor.getDoc().setValue(data);
    }).fail(function(response) {
        alert('Error getting source template: ' + response.responseText);
    });
}

function fillSourceForTask(index) {
    $.get("/internals/get/source-for-task?task=" + index, function(data) {
        _internalEditor.getDoc().setValue(data);
    }).fail(function(response) {
        alert('Error getting source code for task: ' + response.responseText);
    });
}