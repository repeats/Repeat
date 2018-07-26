<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Key sequences (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list activation.keySequences as sequence>
            <tr>
                <td>${sequence}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>