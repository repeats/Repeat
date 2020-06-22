<#import "/fragments/task_builder_steps_table.ftlh" as task_builder_steps_table_fragment>
<#import "/fragments/manually_build_task_actions.ftlh" as manually_build_task_actions_fragment>

<#macro fragment>
    <div id="manually-build-task-id" style="display:none;">${constructorId}</div>

    <div class="row top-buffer">
        <div class="col-xs-2">
            <div class="dropdown">
                <button id="manually-build-task-button-actor" type="button" class="btn btn-default btn-span dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Actor
                </button>
                <ul id="manually-build-task-actor" class="dropdown-menu">
                    <li ><a href="#">Mouse</a></li>
                    <li ><a href="#">Keyboard</a></li>
                    <li ><a href="#">Controller</a></li>
                </ul>
            </div>
        </div>
        <div class="col-xs-4">
            <div class="dropdown">
                <button id="manually-build-task-button-action" type="button" class="btn btn-default btn-span dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Action
                </button>
                <ul id="manually-build-task-actions" class="dropdown-menu">
                    <@manually_build_task_actions_fragment.fragment/>
                </ul>
            </div>
        </div>
        <div class="col-xs-6">
            <input id="manually-build-task-parameters-value" type="text" class="form-control" placeholder="Enter parameters">
        </div>
    </div>
    <div class="row top-buffer"></div>
    <div class="row">
        <div class="col-xs-3"><button id="manually-build-task-insert-step-button" type="button" class="btn btn-default btn-span">Insert step</button></div>
        <div class="col-xs-3"><button id="manually-build-task-remove-step-button" type="button" class="btn btn-default btn-span">Remove step(s)</button></div>
        <div class="col-xs-3"><button id="manually-build-task-move-up-button" type="button" class="btn btn-default btn-span">Move up</button></div>
        <div class="col-xs-3"><button id="manually-build-task-move-down-button" type="button" class="btn btn-default btn-span">Move down</button></div>

    </div>

    <div class="row top-buffer">
        <div id="manually-build-action-steps">
            <@task_builder_steps_table_fragment.fragment/>
        </div>
    </div>
</#macro>
