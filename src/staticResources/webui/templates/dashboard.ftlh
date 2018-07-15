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
    <link href="/static/css/custom-buttons.css" rel="stylesheet" />
</head>
<body>

<div class="wrapper">
    <!-- Modals -->
    <div id="modal-task-name" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a new task name.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-task-name">New name:</label>
                        <input class="form-check-input" name="new-task-name" type="text" id="new-task-name">
                    </div>
                </div>
                <div class="modal-footer">
                    <div id="modal-task-name-row" style="display:none;">0</div>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="modal-task-name-save">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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
                    <h4 class="modal-title">Select a compiling language.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <input class="form-check-input" name="radio-compiling-language" type="radio" id="radio-compiling-java" checked>
                        <label class="form-check-label" for="radio-compiling-java">Java</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-compiling-language" type="radio" id="radio-compiling-python">
                        <label class="form-check-label" for="radio-compiling-python">Python</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-compiling-language" type="radio" id="radio-compiling-cs">
                        <label class="form-check-label" for="radio-compiling-cs">C#</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-compiling-language" type="radio" id="radio-compiling-scala" disabled>
                        <label class="form-check-label" for="radio-compiling-scala">Scala</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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
                    <h4 class="modal-title">Select a debug level.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <input class="form-check-input" name="radio-debug-level" type="radio" id="radio-debug-severe">
                        <label class="form-check-label" for="radio-debug-severe">Severe</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-debug-level" type="radio" id="radio-debug-warning">
                        <label class="form-check-label" for="radio-debug-warning">Warning</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-debug-level" type="radio" id="radio-debug-info" checked>
                        <label class="form-check-label" for="radio-debug-info">Info</label>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" name="radio-debug-level" type="radio" id="radio-debug-fine">
                        <label class="form-check-label" for="radio-debug-fine">Fine</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
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
                                <li><a href="#">Load config...</a></li>
                                <li><a href="#">Save config</a></li>
                                <li class="divider"></li>
                                <li><a href="#">Load source...</a></li>
                                <li><a href="#">Save source...</a></li>
                                <li><a href="#">Clean unused sources</a></li>
                                <li><a href="#">Import...</a></li>
                                <li><a href="#">Export</a></li>
                                <li class="divider"></li>
                                <li><a href="#">Force exit...</a></li>
                                <li><a href="#">Exit</a></li>
                              </ul>
                        </li>
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        Tool
                                        <b class="caret"></b>
                                    </p>
                              </a>
                              <ul class="dropdown-menu">
                                <li><a href="#">Halt all tasks</a></li>
                                <li><a href="#">Generate source</a></li>
                                <li><a href="/ipcs">Native Modules...</a></li>
                                <li><a href="#" data-toggle="modal" data-target="#modal-compiling-languages">Compiling languages...</a></li>
                                <li><a href="#">Common tools</a></li>
                              </ul>
                        </li>
                        <li class="dropdown">
                              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <p>
                                        Setting
                                        <b class="caret"></b>
                                    </p>

                              </a>
                              <ul class="dropdown-menu">
                                <li><a href="#">Hotkeys...</a></li>
                                <li><a href="#">Set compiler path...</a></li>
                                <li><a href="#">Configure compiler...</a></li>
                                <li><a href="#">Record mouse click only</a></li>
                                <li><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" checked/>&nbsp;Halt task by ESC</a></li>
                                <li><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox"/>&nbsp;Execute on release</a></li>
                                <li class="divider"></li>
                                <li><a href="#" data-toggle="modal" data-target="#modal-debug-level">Debug level...</a></li>
                                <li><a href="#" data-value="option1" tabIndex="-1"><input type="checkbox" checked/>&nbsp;Use tray icon</a></li>
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
                                <li><a href="#">API</a></li>
                                <li><a href="#">About</a></li>
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
                        <button title="${tooltips.record}" type="button" class="btn btn-default repeat-btn-record">&nbsp</button>
                        Mouse position: <button id="mouse-position" type="button" class="btn btn-default">0, 0</button>
                    </div>
                    <div class="col-xs-6">
                        <div class="btn-group pull-right">
                            <@task_groups_dropdown_fragment.fragment/>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-6">
                        <button title="${tooltips.replay}" type="button" class="btn btn-default repeat-btn-replay">&nbsp</button>
                        Repeat <button type="button" class="btn btn-default">1</button> times(s) with delay each time <button type="button" class="btn btn-default">0</button> ms, and speed up of <button type="button" class="btn btn-default">1.0</button>
                    </div>
                    <div class="col-xs-6 pull-right">
                        <span class="align-middle pull-right"><div>Execution time: <span class="label label-info">${executionTime}</span></div></span>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-6">
                        <button title="${tooltips.compile}" type="button" class="btn btn-default repeat-btn-compile">&nbsp</button>
                        <button title="${tooltips.run}" type="button" class="btn btn-default repeat-btn-run">&nbsp</button>
                        <button title="${tooltips.editCode}" type="button" class="btn btn-default repeat-btn-edit-code">&nbsp</button>
                        <button title="${tooltips.reload}" type="button" class="btn btn-default repeat-btn-reload">&nbsp</button>
                    </div>
                    <div class="col-xs-6">
                        <div class="pull-right">
                            <button title="${tooltips.add}" type="button" class="btn btn-default repeat-btn-add">&nbsp</button>
                            <button title="${tooltips.overwrite}" type="button" class="btn btn-default repeat-btn-overwrite">&nbsp</button>
                            <button title="${tooltips.delete}" type="button" class="btn btn-default repeat-btn-delete">&nbsp</button>
                            <button title="${tooltips.up}" type="button" class="btn btn-default repeat-btn-up">&nbsp</button>
                            <button title="${tooltips.down}" type="button" class="btn btn-default repeat-btn-down">&nbsp</button>
                            <button title="${tooltips.changeGroup}" type="button" class="btn btn-default repeat-btn-change-group">&nbsp</button>
                        </div>
                    </div>
                </div>
                <div class="row top-buffer">
                    <div class="col-xs-6">
                        <div class="col-xs-12 form-group">
                            <label for="source-code">Source code:</label>
                            <textarea class="form-control" rows="40" id="source-code" wrap="off"></textarea>
                        </div>
                    </div>
                    <div class="col-xs-6">
                        <div class="row">
                            <div class="col-xs-12 header-fixed">
                                <table id="tableTasks" class="table table-striped table-header-fixed" >
                                    <@task_fragment.fragment/>
                                </table>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 form-group">
                                <label for="main-log">Main log:</label>
                                <textarea class="form-control" rows="25" id="main-log" wrap="off"></textarea>
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
    <script src="/static/js/repeat/logs-textarea.js"></script>
    <script src="/static/js/repeat/mouse-position.js"></script>
    <script src="/static/js/repeat/source-textarea.js"></script>
    <script src="/static/js/repeat/table-tasks.js"></script>

    <script type="text/javascript">
        $(document).ready(function(){
            $('.dropdown-toggle').dropdown();
            registerLogHandler();
            registerMousePosition();
            registerSourceTextArea();
            registerTableTasks();
        });
    </script>

</html>
