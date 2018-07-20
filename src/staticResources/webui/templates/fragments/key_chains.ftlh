<#macro fragment>
    <#list activation.keyChains as chain>
        <tr>
            <td>${chain}</td>
        </tr>
    </#list>
</#macro>