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
                               <p>Task Name</p>
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
                    <div class="col-xs-6">
                        <div><button type="button" class="btn btn-default btn-span">Save</button></div>
                    </div>
                    <div class="col-xs-6">
                        <div><a href="/"><button type="button" class="btn btn-default btn-span">Cancel</button></a></div>
                    </div>
                </div>
                <div class="row top-buffer">
                    <span class="label label-default">Start pressing keys</span>
                    <div><button type="button" class="btn btn-default btn-span">A+S+D</button></div>
                </div>
                <div class="row top-buffer">
                    <div class="col-xs-6">
                        <button type="button" class="btn btn-default btn-span">Add key chain</button>
                        <table class="table">
                            <thead class="thead-dark">
                                <tr>
                                    <th scope="col">Key chains (Double click to remove)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <@key_chains_fragment.fragment/>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-xs-6">
                        <button type="button" class="btn btn-default btn-span">Add key sequence</button>
                        <table class="table">
                            <thead class="thead-dark">
                                <tr>
                                    <th scope="col">Key sequences (Double click to remove)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <@key_sequences_fragment.fragment/>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="row">
                    <span class="label label-default">Phrases</span>
                    <button type="button" class="btn btn-default btn-span">Add phrase</button>
                    <table class="table">
                        <thead class="thead-dark">
                            <tr>
                                <th scope="col">Phrases (Double click to remove)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <@phrases_fragment.fragment/>
                        </tbody>
                    </table>
                </div>
                <div class="row">
                    <div class="form-group">
                        <span class="label label-default">Mouse gestures (hold shift to select more than one)</span>
                        <select multiple class="form-control" size="9">
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

    <script type="text/javascript">
    $(document).ready(function(){
    });
    </script>

</html>
