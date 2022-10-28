<#import "/fragments/index_modals.ftlh" as index_modals_fragment>
<#import "/fragments/source_code.ftlh" as source_code_fragment>
<#import "/fragments/tasks.ftlh" as task_fragment>
<#import "/fragments/task_groups_dropdown.ftlh" as task_groups_dropdown_fragment>
<#import "/fragments/common_header.ftlh" as common_header_fragment>
<#import "/fragments/footer.ftlh" as footer_fragment>
<#import "/fragments/common_js.ftlh" as common_js_fragment>

<!doctype html>
<html lang="en">
<head>
    <@common_header_fragment.fragment/>

    <!-- Custom button CSS     -->
    <link rel="stylesheet" href="/static/css/custom-buttons.css"/>
    <!-- Source code text area CSS     -->
    <link rel="stylesheet" href="/static/css/codemirror.css">
</head>
<body>

<div class="wrapper">
    <!-- Modals -->
    <@index_modals_fragment.fragment/>
    <!-- End Modals -->

    <div class="main-panel">
        <nav class="navbar navbar-default navbar-fixed">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navigation-example-2">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/">Repeat</a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-left">
                    </ul>

                    <ul class="nav navbar-nav navbar-right">
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        File
                                        <b class="caret"></b>
                                    </p>
                              </a>
                              <ul class="dropdown-menu">
                                <li id="menu-save-config"><a href="#">Save config</a></li>
                                <li class="divider"></li>
                                <li id="menu-clean-unused-sources"><a href="#">Clean unused sources</a></li>
                                <li id="menu-import"><a href="#">Import...</a></li>
                                <li id="menu-export"><a href="#">Export</a></li>
                                <li class="divider"></li>
                                <li id="menu-force-exit"><a href="#">Force exit...</a></li>
                                <li id="menu-exit"><a href="#">Exit</a></li>
                              </ul>
                        </li>
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        Tools
                                        <b class="caret"></b>
                                    </p>
                              </a>
                              <ul class="dropdown-menu">
                                <li id="menu-halt-all-tasks"><a href="#">Halt all tasks</a></li>
                                <li id="menu-generate-source"><a href="#">Generate source</a></li>
                                <li><a href="/ipcs">Native modules...</a></li>
                                <li><a href="/repeats-remote-clients">Remote Repeats clients...</a></li>
                                <li id="menu-compiling-languages"><a href="#">Compiling languages...</a></li>
                              </ul>
                        </li>
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        Settings
                                        <b class="caret"></b>
                                    </p>
                              </a>
                              <ul class="dropdown-menu">
                                <li id="menu-hotkeys"><a href="#">Hotkeys...</a></li>
                                <li id="menu-compiler-path"><a href="#">Set compiler path...</a></li>
                                <li id="menu-configure-compiler"><a href="#">Configure compiler...</a></li>
                                <li><a href="/global-configs">Global configs...</a></li>
                                <li id="menu-record-mouse-click-only"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.recordMouseClickOnly> checked </#if>/>&nbsp;Record mouse click only</a></li>
                                <li id="menu-halt-task-by-escape"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.haltTaskByEscape> checked </#if>/>&nbsp;Halt task by ESC</a></li>
                                <li id="menu-execute-on-release"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.executeOnRelease> checked </#if>/>&nbsp;Execute on release</a></li>
                                <li id="menu-use-clipboard-to-type-string"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.useClipboardToTypeString> checked </#if>/>&nbsp;Use clipboard to type strings</a></li>
                                <li id="menu-run-task-with-server-config"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.runTaskWithServerConfig> checked </#if>/>&nbsp;Run task using server config</a></li>
                                <li id="menu-use-java-awt-for-mouse-position"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.useJavaAwtToGetMousePosition> checked </#if>/>&nbsp;Use Java AWT for mouse position</a></li>
                                <li class="divider"></li>
                                <li id="menu-debug-level"><a href="#">Debug level...</a></li>
                                <li id="menu-use-tray-icon"><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" <#if config.useTrayIcon> checked </#if>/>&nbsp;Use tray icon</a></li>
                              </ul>
                        </li>
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        Help
                                        <b class="caret"></b>
                                    </p>
                              </a>
                              <ul class="dropdown-menu">
                                <li><a href="/api">API</a></li>
                                <li><a href="/about">About</a></li>
                              </ul>
                        </li>
                        <li class="separator hidden-lg"></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-xs-6">
                        <button id="button-record" title="${tooltips.record}" type="button" class="btn btn-default repeat-btn-record">&nbsp</button>
                        Mouse position: <button id="mouse-position" type="button" class="btn btn-default">0, 0</button>
                    </div>
                    <div class="col-xs-6">
                        <div id="task-groups-dropdown-container" class="btn-group pull-right">
                            <@task_groups_dropdown_fragment.fragment/>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-6">
                        <button id="button-replay" title="${tooltips.replay}" type="button" class="btn btn-default repeat-btn-replay">&nbsp</button>
                        Repeat <button id="button-replay-count" type="button" class="btn btn-default">${replayConfig.count}</button> times(s) with delay each time <button id="button-replay-delay" type="button" class="btn btn-default">${replayConfig.delay}</button> ms, and speed up of <button id="button-replay-speedup" type="button" class="btn btn-default">${replayConfig.speedup}</button>
                    </div>
                    <div class="col-xs-6 pull-right">
                        <span class="align-middle pull-right"><div>Execution time: <span class="label label-info">${executionTime}</span></div></span>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-6">
                        <button id="button-compile" title="${tooltips.compile}" type="button" class="btn btn-default repeat-btn-compile">&nbsp</button>
                        <button id="button-run" title="${tooltips.run}" type="button" class="btn btn-default repeat-btn-run">&nbsp</button>
                        <button id="button-edit-code" title="${tooltips.editCode}" type="button" class="btn btn-default repeat-btn-edit-code">&nbsp</button>
                        <button id="button-reload" title="${tooltips.reload}" type="button" class="btn btn-default repeat-btn-reload">&nbsp</button>
                    </div>
                    <div class="col-xs-6">
                        <div class="pull-right">
                            <button id="button-run-selected" title="${tooltips.runSelected}" type="button" class="btn btn-default repeat-btn-run-selected">&nbsp</button>
                            <button id="button-add" title="${tooltips.add}" type="button" class="btn btn-default repeat-btn-add">&nbsp</button>
                            <button id="button-overwrite" title="${tooltips.overwrite}" type="button" class="btn btn-default repeat-btn-overwrite">&nbsp</button>
                            <button id="button-delete" title="${tooltips.delete}" type="button" class="btn btn-default repeat-btn-delete">&nbsp</button>
                            <button id="button-up" title="${tooltips.up}" type="button" class="btn btn-default repeat-btn-up">&nbsp</button>
                            <button id="button-down" title="${tooltips.down}" type="button" class="btn btn-default repeat-btn-down">&nbsp</button>
                            <button id="button-change-group" title="${tooltips.changeGroup}" type="button" class="btn btn-default repeat-btn-change-group">&nbsp</button>
                            <button id="button-task-table-expand-shrink" type="button" class="btn btn-default repeat-btn-expand">&nbsp</button>
                        </div>
                    </div>
                </div>
                <div class="row top-buffer">
                    <div class="col-xs-6">
                        <div id="source-code-container" class="col-xs-12 form-group">
                            <@source_code_fragment.fragment/>
                        </div>
                    </div>
                    <div class="col-xs-6">
                        <div class="row">
                            <div class="col-xs-12 header-fixed">
                                <div id="table-tasks-container">
                                    <@task_fragment.fragment/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 form-group">
                                <label for="main-log"><a href="/logs">Main logs:</a></label> <button id="button-clear-log" class="btn btn-default">Clear</button>
                                <textarea class="form-control" rows="20" id="main-log" wrap="off"></textarea>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                </div>
            </div>
        </div>


        <@footer_fragment.fragment/>
    </div>
</div>


</body>
    <@common_js_fragment.fragment/>

    <!--  JavaScript for this page.    -->
    <script src="/static/js/codemirror.js"></script>

    <script src="/static/js/repeat/menu-index-page.js"></script>
    <script src="/static/js/repeat/polling-common.js"></script>
    <script src="/static/js/repeat/button-index-page-actions.js"></script>
    <script src="/static/js/repeat/logs-textarea.js"></script>
    <script src="/static/js/repeat/mouse-position.js"></script>
    <script src="/static/js/repeat/record-replay.js"></script>
    <script src="/static/js/repeat/source-textarea.js"></script>
    <script src="/static/js/repeat/task-groups-dropdown.js"></script>
    <script src="/static/js/repeat/table-tasks.js"></script>
    <script src="/static/js/repeat/table-tasks.js"></script>
    <script src="/static/js/repeat/manually-build-task.js"></script>

    <script type="text/javascript">
        $(document).ready(function(){
            $('.dropdown-toggle').dropdown();
            registerIndexPageButtonActions();
            registerLogHandler();
            registerMousePosition();
            registerRecordReplayActions();
            registerSourceTextArea();
            registerTableTasks();
            registerTaskGroupsDropdownEvents();
            registerMenuIndexPageActions();

            manuallyBuildTask.registerActions();
        });
    </script>

</html>
