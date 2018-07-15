<#macro fragment>
    <a href="/task-groups" class="btn btn-default">${task_group.current}</a>
    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <span class="caret"></span>
        <span class="sr-only">Toggle Dropdown</span>
    </button>
    <ul class="dropdown-menu">
        <#list task_group.groups as group>
        <li><a href="#">${group.name}</a></li>
        </#list>
    </ul>
</#macro>