<#import "/fragments/task_builder_body.ftlh" as task_builder_body_fragment>

<#macro fragment>
    <#if displayManualBuild>
        <@task_builder_body_fragment.fragment/>
    <#else>
        <label for="source-code">Source code:</label>
        <textarea class="form-control" id="source-code">abc</textarea>
    </#if>
</#macro>

<@fragment/>