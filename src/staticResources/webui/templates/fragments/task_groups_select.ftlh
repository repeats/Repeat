<#macro fragment>
    <#list groups as group>
    <div class="form-check">
        <input class="form-check-input" name="radio-move-to-task-group" id="modal-task-group-select-${group?index}" type="radio" ${group.selected}>
        <label class="form-check-label" for="modal-task-group-select-${group?index}">${group.name}</label>
    </div>
    </#list>
</#macro>

<@fragment/>