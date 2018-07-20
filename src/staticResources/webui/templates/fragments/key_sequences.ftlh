<#macro fragment>
    <#list activation.keySequences as sequence>
        <tr>
            <td>${sequence}</td>
        </tr>
    </#list>
</#macro>