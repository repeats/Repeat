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