<#import "/fragments/tools_clients.ftlh" as tools_clients_fragment>
<#import "/fragments/core_clients.ftlh" as core_clients_fragment>
<#import "/fragments/remote_repeats_compiler_clients.ftlh" as remote_repeats_compiler_clients_fragment>
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
                               <p>Global configurations</p>
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
                    <span class="label label-default">Tools execution clients</span>
                    <table id="tools-clients-table" class="table">
                        <@tools_clients_fragment.fragment/>
                    </table>
                </div>
                <div class="row">
                    <div><button id="button-save-tools-config" type="button" class="btn btn-default btn-span">Save Tools Config</button></div>
                </div>

                <div class="row">
                    <span class="label label-default">Mouse + Keyboard execution clients</span>
                    <table id="core-clients-table" class="table">
                        <@core_clients_fragment.fragment/>
                    </table>
                </div>
                <div class="row">
                    <div><button id="button-save-core-config" type="button" class="btn btn-default btn-span">Save Mouse + Keyboard Config</button></div>
                </div>

                <div class="row">
                    <span class="label label-default">Repeats remote compiler clients</span>
                    <table id="repeats-remote-compiler-clients-table" class="table">
                        <@remote_repeats_compiler_clients_fragment.fragment/>
                    </table>
                </div>
                <div class="row">
                    <div><button id="button-save-remote-repeats-compilers-config" type="button" class="btn btn-default btn-span">Save Repeats Remote Compiler Config</button></div>
                </div>
            </div>
        </div>

        <@footer_fragment.fragment/>
    </div>
</div>


</body>
    <@common_js_fragment.fragment/>

    <!--  JavaScript for this page.    -->
    <script src="/static/js/repeat/global-configs.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        registerGlobalConfigsPage();
    });
    </script>

</html>
