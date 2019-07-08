<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col">Name</th>
            <th scope="col">ID (click to show)</th>
            <th scope="col"> <div data-toggle="tooltip" data-placement="bottom" title="'{}' for key chain&#10;'<>' for key sequences&#10;'()' for phrases&#10;'[]' for mouse gesture"> Activation (?) </div> </th>
            <th scope="col">Enabled</th>
            <th scope="col">Use Count</th>
            <th scope="col">Last Used</th>
        </tr>
    </thead>
    <tbody id="table-tasks-body">
        <#list tasks as task>
        <tr>
            <td>${task.name}</td>
            <td class="table-cell-align-center"><button id="button-show-task-id-${task.id}" title="${tooltips.showActionId}" type="button" class="btn btn-default repeat-btn-show-action-id">&nbsp</button></td>
            <td>${task.activation}</td>
            <td>${task.enabled}</td>
            <td>${task.useCount}</td>
            <td>${task.lastUsed}</td>
        </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>