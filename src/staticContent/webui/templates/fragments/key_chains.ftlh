<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Key chains (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list task.activation.keyChains as chain>
            <tr>
                <td data-original-index="${chain.originalIndex}">${chain.value}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>