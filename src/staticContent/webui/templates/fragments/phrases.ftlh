<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Phrases (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list task.activation.phrases as phrase>
            <tr>
                <td data-original-index="${phrase.originalIndex}">${phrase.value}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>