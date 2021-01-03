<#import "/fragments/global_key_action.ftlh" as global_key_action_fragment>
<#import "/fragments/key_chains.ftlh" as key_chains_fragment>
<#import "/fragments/key_sequences.ftlh" as key_sequences_fragment>
<#import "/fragments/phrases.ftlh" as phrases_fragment>
<#import "/fragments/shared_variables.ftlh" as shared_variables>

<#macro fragment>
    <h1> Activation </h1>
    <div class="row">
        <span class="label label-warning">Moving tasks around when editting its activation may result in undefined behavior!</span>
        <div><button id="button-save" type="button" class="btn btn-default btn-span">Save Activation</button></div>
    </div>
    <div class="row top-buffer">
        <span class="label label-default">Global Key Action</span>
        <div>
            <@global_key_action_fragment.fragment/>
        </div>
    </div>
    <div class="row top-buffer">
        <span class="label label-default">Start pressing keys when enabled</span>
        <div><button id="button-strokes" type="button" class="btn btn-default btn-span">None</button></div>
    </div>
    <div class="row top-buffer">
        <div class="col-xs-4">
            <div><button id="button-add-mouse-key-left" type="button" class="btn btn-default btn-span" title="This is usually the left mouse button unless configured otherwise.">Add left mouse button (button 1)</button></div>
        </div>
        <div class="col-xs-4">
            <div><button id="button-add-mouse-key-middle" type="button" class="btn btn-default btn-span" title="This is usually the middle mouse button unless configured otherwise.">Add middle mouse button (button 2)</button></div>
        </div>
        <div class="col-xs-4">
            <div><button id="button-add-mouse-key-right" type="button" class="btn btn-default btn-span" title="This is usually the right mouse button unless configured otherwise.">Add right button (button 3)</button></div>
        </div>
    </div>
    <div class="row top-buffer">
        <div class="col-xs-6">
            <button id="button-add-key-chain" type="button" class="btn btn-default btn-span" <#if task.activation.config.disableKeyChain> disabled </#if>>Add key chain</button>
            <table id="table-key-chains" class="table">
                <@key_chains_fragment.fragment/>
            </table>
        </div>
        <div class="col-xs-6">
            <button id="button-add-key-sequence" type="button" class="btn btn-default btn-span" <#if task.activation.config.disableKeySequence> disabled </#if>>Add key sequence</button>
            <table id="table-key-sequences" class="table">
                <@key_sequences_fragment.fragment/>
            </table>
        </div>
    </div>

    <div class="row top-buffer">
        <div class="col-xs-6">
            <span class="label label-default">Phrases</span>
            <button id="button-add-phrase" type="button" class="btn btn-default btn-span" <#if task.activation.config.disablePhrase> disabled </#if>>Add phrase</button>
            <table id="table-phrases" class="table">
                <@phrases_fragment.fragment/>
            </table>
        </div>
        <div class="col-xs-6">
            <span class="label label-default">Shared variables</span>
            <button id="button-add-shared-variables" type="button" class="btn btn-default btn-span" <#if task.activation.config.disableSharedVariable> disabled </#if>>Add shared variable</button>
            <table id="table-shared-variables" class="table">
                <@shared_variables.fragment/>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="form-group">
            <span class="label label-default">Mouse gestures (hold shift to select more than one)</span>
            <select id="mouse-gestures-selection" multiple class="form-control" size="9">
                <#if !task.activation.config.disableMouseGesture>
                <option <#if task.activation.mouseGestures.alpha> selected </#if>>Alpha</option>
                <option <#if task.activation.mouseGestures.circleLeft> selected </#if>>Circle Left</option>
                <option <#if task.activation.mouseGestures.gamma> selected </#if>>Gamma</option>
                <option <#if task.activation.mouseGestures.greaterThan> selected </#if>>Greater than</option>
                <option <#if task.activation.mouseGestures.hat> selected </#if>>Hat</option>
                <option <#if task.activation.mouseGestures.lessThan> selected </#if>>Less than</option>
                <option <#if task.activation.mouseGestures.n> selected </#if>>N</option>
                <option <#if task.activation.mouseGestures.square> selected </#if>>Square</option>
                <option <#if task.activation.mouseGestures.squareRoot> selected </#if>>Square root</option>
                <option <#if task.activation.mouseGestures.tilda> selected </#if>>Tilda</option>
                <option <#if task.activation.mouseGestures.triangle> selected </#if>>Triangle</option>
                <option <#if task.activation.mouseGestures.z> selected </#if>>Z</option>
                </#if>
            </select>
        </div>
    </div>
</#macro>

<@fragment/>