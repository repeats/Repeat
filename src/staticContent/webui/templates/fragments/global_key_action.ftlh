<#macro fragment>
    <div class="col-xs-6">
        <button id="button-on-key-pressed" type="button" class="btn <#if task.activation.globalActivation.onKeyPressed> btn-info <#else> btn-default </#if> btn-span" <#if task.activation.config.disableGlobalAction> disabled </#if>>Listen on key pressed</button>
    </div>
    <div class="col-xs-6">
        <button id="button-on-key-released" type="button" class="btn <#if task.activation.globalActivation.onKeyReleased> btn-info <#else> btn-default </#if> btn-span" <#if task.activation.config.disableGlobalAction> disabled </#if>>Listen on key released</button>
    </div>
</#macro>