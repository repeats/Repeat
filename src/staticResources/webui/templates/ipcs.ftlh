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
                    <button title="${tooltips.run}" type="button" class="btn btn-default repeat-btn-replay">&nbsp</button>
                    <button title="${tooltips.stop}" type="button" class="btn btn-default repeat-btn-stop">&nbsp</button>
                </div>
                <div class="row">
                    <table class="table">
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

    <script type="text/javascript">
    $(document).ready(function(){
        $(function() {
            /* Get all rows from your 'table' but not the first one 
            * that includes headers. */
            var rows = $('tr').not(":first");

            /* Create 'click' event handler for rows */
            rows.on('click', function(e) {
                /* Get current row */
                var row = $(this);

                /* Otherwise just highlight one row and clean others */
                rows.removeClass('table-highlight');
                row.addClass('table-highlight');
            });
        });
    });
    </script>

</html>
