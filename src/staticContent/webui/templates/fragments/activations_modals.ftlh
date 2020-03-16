
<#macro fragment>
    <div id="modal-phrase" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a phrase.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-phrase">Phrase:</label>
                        <input class="form-check-input" name="new-phrase" type="text" id="new-phrase">
                    </div>
                </div>
                <div class="modal-footer">
                    <button id="modal-phrase-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-shared-variables" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a variable.</h4>
                    <h5> Use 'all' on namespace for all variables. Use 'all' on name for all variables in a namespace. </h5>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-variable-namespace">Namespace:</label>
                        <input class="form-check-input" name="new-variable-namespace" type="text" id="new-variable-namespace">
                    </div>
                    <div class="form-check">
                        <label class="form-check-label" for="new-variable-name">Name:</label>
                        <input class="form-check-input" name="new-variable-name" type="text" id="new-variable-name">
                    </div>
                </div>
                <div class="modal-footer">
                    <button id="modal-shared-variables-save" type="button" class="btn btn-primary" data-dismiss="modal">Add</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

</#macro>

<@fragment/>