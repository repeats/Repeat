<#import "/fragments/task_groups.ftlh" as task_groups_fragment>
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
    <div id="modal-new-task-group" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a name for the new task group:</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="name-for-new-task-group">Name:</label>
                        <input class="form-check-input" name="name-for-new-task-group" type="text" id="name-for-new-task-group">
                    </div>
                </div>
                <div class="modal-footer">
                    <button id="modal-new-task-group-save" type="button" class="btn btn-primary" data-dismiss="modal">Create</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-task-group-name" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a new name for task group:</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-task-group-name">Name:</label>
                        <input class="form-check-input" name="new-task-group-name" type="text" id="new-task-group-name">
                    </div>
                </div>
                <div class="modal-footer">
                    <div id="modal-task-group-name-row" style="display:none;">0</div>
                    <button id="modal-task-group-name-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
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
                        <li>
                           <a href="">
                               <p>Task groups</p>
                            </a>
                        </li>
                        <li class="separator hidden-lg"></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <button id="button-add" title="${tooltips.add}" type="button" class="btn btn-default repeat-btn-add">&nbsp</button>
                    <button id="button-delete" title="${tooltips.remove}" type="button" class="btn btn-default repeat-btn-delete">&nbsp</button>
                    <button id="button-up" title="${tooltips.up}" type="button" class="btn btn-default repeat-btn-up">&nbsp</button>
                    <button id="button-down" title="${tooltips.down}" type="button" class="btn btn-default repeat-btn-down">&nbsp</button>
                </div>
                <div class="row">
                    <table id="table-task-groups" class="table">
                        <@task_groups_fragment.fragment/>
                    </table>
                </div>
            </div>
        </div>


        <@footer_fragment.fragment/>
    </div>
</div>


</body>
    <@common_js_fragment.fragment/>

    <!--  JavaScript for this page.    -->
    <script src="/static/js/repeat/task-groups.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        registerTaskGroups();
    });
    </script>

</html>
