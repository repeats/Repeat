<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Key chains (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list activation.keyChains as chain>
            <tr>
                <td>${chain}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>