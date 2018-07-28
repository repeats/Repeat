<#import "/fragments/key_chains.ftlh" as key_chains_fragment>
<#import "/fragments/key_sequences.ftlh" as key_sequences_fragment>
<#import "/fragments/phrases.ftlh" as phrases_fragment>
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
    <div id="modal-phrase" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Enter a phrase.</h4>
                </div>
                <div class="modal-body">
                    <div class="form-check">
                        <label class="form-check-label" for="new-phrase">Phrase:</label>
                        <input class="form-check-input" name="new-phrase" type="text" id="new-phrase">
                    </div>
                </div>
                <div class="modal-footer">
                    <button id="modal-phrase-save" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
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
                               <p>Task Activation</p>
                            </a>
                        </li>
                        <li class="separator hidden-lg"></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="content">
            <div class="container-fluid">
                <div id="task-activation-task-group-index" style="display:none;">${groupIndex}</div>
                <div id="task-activation-task-index" style="display:none;">${taskIndex}</div>
                <div id="task-activation-constructor-id" style="display:none;">${taskActivationConstructorId}</div>
                <div class="row">
                    <span class="label label-warning">Moving tasks around when editting its activation may result in undefined behavior!</span>
                    <div><button id="button-save" type="button" class="btn btn-default btn-span">Save</button></div>
                </div>
                <div class="row top-buffer">
                    <span class="label label-default">Start pressing keys when enabled</span>
                    <div><button id="button-strokes" type="button" class="btn btn-default btn-span">None</button></div>
                </div>
                <div class="row top-buffer">
                    <div class="col-xs-6">
                        <button id="button-add-key-chain" type="button" class="btn btn-default btn-span" <#if activation.config.disableKeyChain> disabled </#if>>Add key chain</button>
                        <table id="table-key-chains" class="table">
                            <@key_chains_fragment.fragment/>
                        </table>
                    </div>
                    <div class="col-xs-6">
                        <button id="button-add-key-sequence" type="button" class="btn btn-default btn-span" <#if activation.config.disableKeySequence> disabled </#if>>Add key sequence</button>
                        <table id="table-key-sequences" class="table">
                            <@key_sequences_fragment.fragment/>
                        </table>
                    </div>
                </div>
                <div class="row">
                    <span class="label label-default">Phrases</span>
                    <button id="button-add-phrase" type="button" class="btn btn-default btn-span" <#if activation.config.disablePhrase> disabled </#if>>Add phrase</button>
                    <table id="table-phrases" class="table">
                        <@phrases_fragment.fragment/>
                    </table>
                </div>
                <div class="row">
                    <div class="form-group">
                        <span class="label label-default">Mouse gestures (hold shift to select more than one)</span>
                        <select id="mouse-gestures-selection" multiple class="form-control" size="9">
                            <#if !activation.config.disableMouseGesture>
                            <option <#if activation.mouseGestures.alpha> selected </#if>>Alpha</option>
                            <option <#if activation.mouseGestures.circleLeft> selected </#if>>Circle Left</option>
                            <option <#if activation.mouseGestures.gamma> selected </#if>>Gamma</option>
                            <option <#if activation.mouseGestures.greaterThan> selected </#if>>Greater than</option>
                            <option <#if activation.mouseGestures.hat> selected </#if>>Hat</option>
                            <option <#if activation.mouseGestures.lessThan> selected </#if>>Less than</option>
                            <option <#if activation.mouseGestures.n> selected </#if>>N</option>
                            <option <#if activation.mouseGestures.square> selected </#if>>Square</option>
                            <option <#if activation.mouseGestures.squareRoot> selected </#if>>Square root</option>
                            <option <#if activation.mouseGestures.tilda> selected </#if>>Tilda</option>
                            <option <#if activation.mouseGestures.triangle> selected </#if>>Triangle</option>
                            <option <#if activation.mouseGestures.z> selected </#if>>Z</option>
                            </#if>
                        </select>
                    </div>
                </div>
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
