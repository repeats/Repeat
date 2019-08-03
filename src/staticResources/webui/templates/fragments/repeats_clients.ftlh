<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Host</th>
            <th scope="col">Port</th>
            <th scope="col">Running</th>
            <th scope="col">Launch at startup</th>
        </tr>
    </thead>
    <tbody>
        <#list clients as client>
        <tr id="repeats-client-id-${client.id}">
            <td>${client.host}</td>
            <td>${client.port}</td>
            <td>${client.running}</td>
            <td>${client.launchAtStartup}</td>
        </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>