<#macro fragment>
<#if task.hasSourceHistory == "true">
    <h1> Source history </h1>
    <table>
        <tbody>
            <#list task.sourceHistory.entries as entry>
                <tr>
                    <td><a href="/internals/get/task-source?id=${entry.taskId}&timestamp=${entry.createdTimeMillis}"> ${entry.createdTime} </a></td>
                </tr>
            </#list>
        </tbody>
    </table>
</#if>
</#macro>

<@fragment/>