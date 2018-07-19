<#import "/fragments/ipcs.ftlh" as ipcs_fragment>
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
    <div id="modal-ipc-port" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a new port:</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-ipc-port">New port:</label>
                        <input class="form-check-input" name="new-ipc-port" type="text" id="new-ipc-port">
                    </div>
                </div>
                <div class="modal-footer">
                    <div id="modal-ipc-service-row" style="display:none;">0</div>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="modal-ipc-port-save">Save</button>
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
                               <p>IPCs</p>
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
                    <button id="button-run" title="${tooltips.run}" type="button" class="btn btn-default repeat-btn-replay">&nbsp</button>
                    <button id="button-stop" title="${tooltips.stop}" type="button" class="btn btn-default repeat-btn-stop">&nbsp</button>
                </div>
                <div class="row">
                    <table id="ipcs-table" class="table">
                        <@ipcs_fragment.fragment/>
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
    <script src="/static/js/repeat/ipcs.js"></script>

    <script type="text/javascript">
    $(document).ready(function(){
        registerIpcPage();
    });
    </script>

</html>
