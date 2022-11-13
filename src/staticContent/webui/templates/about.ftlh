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
                               <p>About</p>
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
                    <div class="col-md-6 col-md-offset-3">
                        <h4 class="title">Repeat ${version}</h4>
                        <p>A tool to repeat yourself with some intelligence.</p>
                        <p>Created by HP Truong. Contact me at hptruong93@gmail.com.</p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-md-offset-3">
                        <h4 class="title">Powered by:</h4>
                         <ul>
                            <li><a href="https://github.com/repeats/SimpleNativeHooks">Simple Native Hooks</a></li>
                            <li><a href="http://argo.sourceforge.net">Argo JSON</a></li>
                            <li><a href="https://math.nist.gov/javanumerics/jama/">Jama - A Java matrix package</a></li>
                            <li><a href="https://commons.apache.org/proper/commons-lang">Apache Commons Lang</a></li>
                            <li><a href="https://commons.apache.org/proper/commons-text">Apache Commons Text</a></li>
                            <li><a href="https://hc.apache.org/httpcomponents-core-ga/index.html">Apache HttpComponents Core</a></li>
                            <li><a href="https://hc.apache.org/httpcomponents-client-ga/index.html">Apache HttpClient</a></li>
                            <li><a href="https://freemarker.apache.org/">FreeMarker Java Template Engine</a></li>
                            <li><a href="https://creative-tim.com/product/light-bootstrap-dashboard">Light Bootstrap Dashboard</a></li>
                            <li><a href="https://codemirror.net">CodeMirror: a versatile text editor in Javascript</a></li>
                            <li><a href="https://chartjs.org">Chart.js: simple yet flexible JavaScript charting</a></li>
                        </ul>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-md-offset-3">
                        <h4 class="title"><a href="https://github.com/repeats/Repeat/blob/master/LICENSE">License: Apache License 2.0</a></h4>
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
