<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Process</th>
            <th scope="col">Port</th>
            <th scope="col">Running</th>
            <th scope="col">Launch at startup</th>
        </tr>
    </thead>
    <tbody>
        <#list ipcs as ipc>
        <tr>
            <td>${ipc.name}</td>
            <td>${ipc.port}</td>
            <td>${ipc.running}</td>
            <td>${ipc.launchAtStartup}</td>
        </tr>
        </#list>
    </tbody>
</#macro>