<#import "/fragments/repeats_clients.ftlh" as repeats_clients_fragment>
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
    <div id="modal-repeats-client-add" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter server address</h4>
                </div>
                <div class="modal-body">
                    <div>Server address (e.g. 192.168.0.1:9999):</div>
                    <input class="form-control" name="new-repeats-client-address" type="text" id="new-repeats-client-address">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="modal-repeats-clients-button-add">Add</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
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
                               <p>Remote Repeat Clients</p>
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
                    <button id="repeats-clients-button-add" title="${tooltips.add}" type="button" class="btn btn-default repeat-btn-add">&nbsp</button>
                    <button id="repeats-clients-button-delete" title="${tooltips.delete}" type="button" class="btn btn-default repeat-btn-delete">&nbsp</button>
                    <button id="repeats-clients-button-run" title="${tooltips.run}" type="button" class="btn btn-default repeat-btn-replay">&nbsp</button>
                    <button id="repeats-clients-button-stop" title="${tooltips.stop}" type="button" class="btn btn-default repeat-btn-stop">&nbsp</button>
                </div>
                <div class="row">
                    <table id="repeats-clients-table" class="table">
                        <@repeats_clients_fragment.fragment/>
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
    <script src="/static/js/repeat/repeats-clients.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        registerRepeatsClientPage();
    });
    </script>

</html>
