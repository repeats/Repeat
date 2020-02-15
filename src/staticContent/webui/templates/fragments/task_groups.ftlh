<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Group name</th>
            <th scope="col">Group ID</th>
            <th scope="col">Task count</th>
            <th scope="col">Enabled</th>
            <th scope="col">Selected</th>
        </tr>
    </thead>
    <tbody>
        <#list groups as group>
        <tr>
            <td>${group.name}</td>
            <td>${group.id}</td>
            <td>${group.taskCount}</td>
            <td>${group.enabled}</td>
            <td>${group.selected}</td>
        </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>