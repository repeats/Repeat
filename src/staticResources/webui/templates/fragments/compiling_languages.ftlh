<#macro fragment>
    <#list compilingLanguages as language>
    <div class="form-check">
        <input class="form-check-input" name="radio-compiling-language" type="radio" id="radio-compiling-${language.id}" <#if language.disabled> disabled </#if> <#if language.selected> checked </#if>>
        <label class="form-check-label" for="radio-compiling-${language.id}">${language.name}</label>
    </div>
    </#list>
</#macro>