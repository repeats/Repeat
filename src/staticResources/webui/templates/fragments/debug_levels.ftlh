<#macro fragment>
    <#list config.debugLevels as level>
        <div class="form-check">
            <input class="form-check-input" name="radio-debug-level" type="radio" id="radio-debug-level-${level.name}" <#if level.selected> checked </#if>>
            <label class="form-check-label" for="radio-debug-level-${level.name}">${level.name}</label>
        </div>
    </#list>
</#macro>

<@fragment/>