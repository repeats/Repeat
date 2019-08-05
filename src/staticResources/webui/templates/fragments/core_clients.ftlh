<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Host</th>
            <th scope="col">Port</th>
            <th scope="col">Enabled</th>
            <th scope="col">Defunc? (Client deleted or not running)</th>
        </tr>
    </thead>
    <tbody>
        <#list globalConfigs.coreConfigs.clients as client>
        <tr id="core-clients-id-${client.id}">
            <td>${client.host}</td>
            <td>${client.port}</td>
            <td>${client.enabled}</td>
            <td>${client.defunc}</td>
        </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>