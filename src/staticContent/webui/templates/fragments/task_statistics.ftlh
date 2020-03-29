<#macro fragment>
<#if task.hasStatistics == "true">
    <h1> Statistics </h1>
    <div class="row col-xs-12">
        <p><b>Created:</b> ${task.statistics.created}</p>
        <p><b>Last used:</b> ${task.statistics.lastUsed}</p>
        <p><b>Total execution time:</b> ${task.statistics.totalExecutionTime}</p>
        <p><b>Average execution time (success only):</b> ${task.statistics.averageExecutionTime}</p>
    </div>
    <div id="task-activation-breakdown" style="display:none;">${task.statistics.encodedTaskActivationBreakdown}</div>
    <div id="task-execution-data" style="display:none;">${task.statistics.encodedTaskExecutionInstances}</div>

    <div class="row col-xs-12">
        <h2> Invocation breakdown by task activation </h2>
        <canvas id="activationBreakdownChart" ></canvas>
    </div>

    <div class="row col-xs-12">
        <h2> Invocation run time </h2>
        <canvas id="pastRunChart" ></canvas>
    </div>

    <script src="/static/js/repeat/task-statistics.js"></script>
    <script>
        var activationBreakdownChart = _drawActivationBreakdown();
        var pastRunChart = _drawPastRun();
    </script>
</#if>
</#macro>

<@fragment/>