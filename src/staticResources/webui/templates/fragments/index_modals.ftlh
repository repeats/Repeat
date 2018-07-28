<#import "/fragments/compiling_languages.ftlh" as compiling_languages_fragment>
<#import "/fragments/debug_levels.ftlh" as debug_levels_fragment>

<#macro fragment>
    <div id="modal-force-exit-confirm" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Confirm force exit.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label">Forcing exit will not do any clean up so you may have stale resources and background processes. Are you sure?</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button id="modal-force-exit-confirm-ok" type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-replay-config" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 id="modal-replay-config-title" class="modal-title">Enter a replay config.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label id="label-replay-config" class="form-check-label" for="new-replay-config-value">New replay config:</label>
                        <input class="form-check-input" name="new-replay-config-value" type="text" id="new-replay-config-value">
                    </div>
                </div>
                <div class="modal-footer">
                    <div id="modal-replay-config-param-name" style="display:none;">0</div>
                    <button id="modal-replay-config-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-task-name" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a new task name</h4>
                </div>
                <div class="modal-body">
                    <div>New name:</div>
                    <input class="form-control" type="text" id="new-task-name">
                </div>
                <div class="modal-footer">
                    <div id="modal-task-name-row" style="display:none;">0</div>
                    <button id="modal-task-name-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-set-hotkeys" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Pick a hotkey to configure</h4>
                </div>
                <div class="modal-body">
                    <a class="btn btn-default form-control" href="/task-activation?task=record"> Start/Stop recording </a>
                    <a class="btn btn-default form-control" href="/task-activation?task=replay"> Start/stop replaying </a>
                    <a class="btn btn-default form-control" href="/task-activation?task=runCompiled"> Run/Stop compiled action </a>
                    <a class="btn btn-default form-control" href="/task-activation?task=mouseGestureActivation"> Mouse gesture activation </a>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-set-compiler-path" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a path for the compiler</h4>
                </div>
                <div class="modal-body">
                    <div>New path (absolute):</div>
                    <input class="form-control" name="new-compiler-path" type="text" id="new-compiler-path">
                </div>
                <div class="modal-footer">
                    <button id="modal-set-compiler-path-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-configure-compiler" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Compiler configuration</h4>
                </div>
                <div id="modal-configure-compiler-body" class="modal-body">
                    <!-- Will be filled in at query time. -->
                </div>
                <div class="modal-footer">
                    <button id="modal-configure-compiler-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-compiling-languages" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Select a compiling language</h4>
                </div>
                <div id="modal-compiling-languages-body" class="modal-body">
                    <@compiling_languages_fragment.fragment/>
                </div>
                <div class="modal-footer">
                    <button id="modal-compiling-languages-select" type="button" class="btn btn-primary" data-dismiss="modal">Select</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-debug-level" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Select a debug level</h4>
                </div>
                <div class="modal-body" id="modal-debug-level-body">
                    <@debug_levels_fragment.fragment/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="modal-debug-level-save">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-move-to-task-group" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Select a task group</h4>
                </div>
                <div class="modal-body" id="modal-move-to-task-group-body">
                    <!-- Will be filled in at query time. -->
                </div>
                <div class="modal-footer">
                    <div id="modal-move-to-task-group-row" style="display:none;">0</div>
                    <button id="modal-move-to-task-group-move" type="button" class="btn btn-primary" data-dismiss="modal">Move</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</#macro>