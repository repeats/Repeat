<#macro fragment>
    <#list tasks as task>
        <div id="div-task-id-${task.id}" style="display:none;">${task.id}</div>
    </#list>

    <button id="button-task-table-expand" type="button" class="btn btn-default repeat-btn-expand">&nbsp</button>
    <button id="button-task-table-shrink" type="button" class="btn btn-default repeat-btn-shrink">&nbsp</button>

    <table id="table-tasks" class="table table-header-fixed">
        <thead class="thead-dark">
            <tr>
                <th scope="col">Name</th>
                <th scope="col"> <div data-toggle="tooltip" data-placement="bottom" title="'{}' for key chain&#10;'<>' for key sequences&#10;'()' for phrases&#10;'[]' for mouse gesture"&#10;'-()-' for shared variables> Activation (?) </div> </th>
                <th scope="col">Enabled</th>
                <th scope="col">Use Count</th>
                <th scope="col">Last Used</th>
            </tr>
        </thead>
        <tbody id="table-tasks-body">
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
    </table>
</#macro>

<@fragment/>