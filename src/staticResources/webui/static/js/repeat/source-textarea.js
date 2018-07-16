function registerSourceTextArea() {
    var sourceCodeTextArea = $('#source-code');
    getSourceTemplate(sourceCodeTextArea);

    sourceCodeTextArea.bind('input propertychange', function() {
        var textArea = $(this);
        var val = textArea.val();
        if (val == "") {
            getSourceTemplate(textArea);
        }
    });
}

function getSourceTemplate(textArea) {
    $.get("/internals/get/source-templates", function(data) {
        textArea.val(data);
    }).fail(function(response) {
        alert('Error getting source template: ' + response.responseText);
    });
}

function fillSourceForTask(index) {
    $.get("/internals/get/source-for-task?task=" + index, function(data) {
        $('#source-code').val(data);
    }).fail(function(response) {
        alert('Error getting source code for task: ' + response.responseText);
    });
}