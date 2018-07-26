<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Name</th>
            <th scope="col">Activation</th>
            <th scope="col">Enabled</th>
            <th scope="col">Use Count</th>
            <th scope="col">Last Used</th>
        </tr>
    </thead>
    <tbody>
        <#list tasks as task>
        <tr>
            <td>${task.name}</td>
            <td>${task.activation}</td>
            <td>${task.enabled}</td>
            <td>${task.useCount}</td>
            <td>${task.lastUsed}</td>
        </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>