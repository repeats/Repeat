<#macro fragment>
<#if task.hasPreconditions == "true">
    <h1> Preconditions </h1>
    <div class="row top-buffer">
        <div class="col-xs-6">
            <span class="label label-default">Active window title</span>
            <select id="precondition-active-window-title-match-type" class="form-control">
                <#list task.preconditions.activeWindowInfosPreconditions.activeWindowTitleMatchingTypes as selection>
                <option <#if selection.chosen> selected </#if> value="${selection.htmlValue}">${selection.displayedOption}</option>
                </#list>
            </select>
            <input id="precondition-active-window-title" type="text" class="form-control" data-toggle="tooltip" title="Leave empty to allow all" placeholder="Title of active window."
            value="${task.preconditions.activeWindowInfosPreconditions.activeWindowTitle}">
        </div>
        <div class="col-xs-6">
            <span class="label label-default">Active process name (the process of the active window)</span>
            <select id="precondition-active-process-name-match-type" class="form-control">
                <#list task.preconditions.activeWindowInfosPreconditions.activeProcessNameMatchingTypes as selection>
                <option <#if selection.chosen> selected </#if> value="${selection.htmlValue}">${selection.displayedOption}</option>
                </#list>
            </select>
            <input id="precondition-active-process-name" type="text" class="form-control" data-toggle="tooltip" title="Leave empty to allow all" placeholder="Active process name."
            value="${task.preconditions.activeWindowInfosPreconditions.activeProcessName}">
        </div>
    </div>
</#if>
</#macro>

<@fragment/>