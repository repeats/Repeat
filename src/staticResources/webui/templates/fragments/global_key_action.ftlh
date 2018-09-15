<#macro fragment>
    <div class="col-xs-6">
        <button id="button-on-key-pressed" type="button" class="btn <#if activation.globalActivation.onKeyPressed> btn-info <#else> btn-default </#if> btn-span" <#if activation.config.disableGlobalAction> disabled </#if>>Listen on key pressed</button>
    </div>
    <div class="col-xs-6">
        <button id="button-on-key-released" type="button" class="btn <#if activation.globalActivation.onKeyReleased> btn-info <#else> btn-default </#if> btn-span" <#if activation.config.disableGlobalAction> disabled </#if>>Listen on key released</button>
    </div>
</#macro>