<#import "/fragments/activations_modals.ftlh" as activations_modals_fragment>
<#import "/fragments/activations.ftlh" as activations_fragment>
<#import "/fragments/task_statistics.ftlh" as task_statistics_fragment>
<#import "/fragments/common_header.ftlh" as common_header_fragment>
<#import "/fragments/footer.ftlh" as footer_fragment>
<#import "/fragments/common_js.ftlh" as common_js_fragment>

<!doctype html>
<html lang="en">
<head>
    <@common_header_fragment.fragment/>
    <script src="/static/js/Chart.bundle.min.js"></script>
</head>
<body>

<div class="wrapper">
    <!-- Modals -->
    <@activations_modals_fragment.fragment/>
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
                               <p>Task Details</p>
                            </a>
                        </li>
                        <li class="separator hidden-lg"></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="content">
            <div class="container-fluid">
                <h1> Overview </h1>
                <div class="row col-xs-12">
                    <p><b>Task ID:</b> ${task.id}</p>
                    <p><b>Task Name:</b> ${task.name}</p>
                    <p><b>Enabled:</b> ${task.isEnabled}</p>
                </div>

                <div id="task-activation-task" style="display:none;">${task.id}</div>
                <div id="task-activation-constructor-id" style="display:none;">${taskActivationConstructorId}</div>

                <@activations_fragment.fragment/>
                <@task_statistics_fragment.fragment/>
            </div>
        </div>

        <@footer_fragment.fragment/>
    </div>
</div>


</body>
    <@common_js_fragment.fragment/>

    <!--  JavaScript for this page.    -->
    <script src="/static/js/repeat/task-activation.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        registerTaskActivationActions();
    });
    </script>

</html>
