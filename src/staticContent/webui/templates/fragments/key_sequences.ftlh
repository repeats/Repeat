<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Key sequences (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list task.activation.keySequences as sequence>
            <tr>
                <td data-original-index="${chain.originalIndex}">${sequence.value}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>