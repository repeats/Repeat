<#import "/fragments/task_builder_body.ftlh" as task_builder_body_fragment>
<#import "/fragments/common_header.ftlh" as common_header_fragment>
<#import "/fragments/footer.ftlh" as footer_fragment>
<#import "/fragments/common_js.ftlh" as common_js_fragment>

<!doctype html>
<html lang="en">
<head>
    <@common_header_fragment.fragment/>
</head>
<body>

<div class="wrapper">
    <!-- Modals -->
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
                               <p>Task builder</p>
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
                    <div><button id="manually-build-task-save-button" type="button" class="btn btn-default btn-span">Save Task</button></div>
                </div>
                <@task_builder_body_fragment.fragment/>
            </div>
        </div>

        <@footer_fragment.fragment/>
    </div>
</div>


</body>
    <@common_js_fragment.fragment/>

    <!--  JavaScript for this page.    -->
    <script src="/static/js/repeat/manually-build-task.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        manuallyBuildTask.registerActions();
    });
    </script>

</html>
