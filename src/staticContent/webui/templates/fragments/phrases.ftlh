<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Phrases (Double click to remove)</th>
        </tr>
    </thead>
    <tbody>
        <#list activation.phrases as phrase>
            <tr>
                <td>${phrase}</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>