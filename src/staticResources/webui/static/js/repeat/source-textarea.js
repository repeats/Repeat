function registerSourceTextArea() {
    $('#source-code').bind('input propertychange', function() {
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