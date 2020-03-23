<#macro fragment>
    <thead class="thead-dark">
        <tr>
            <th scope="col" title="Use 'all' on namespace for all variables, or 'all' on variable name for all variables in a namespace.">Shared variables (Double click to remove) (?).</th>
        </tr>
    </thead>
    <tbody>
        <#list task.activation.sharedVariables.variables as variable>
            <tr>
                <td>(${variable.namespace}) - (${variable.name})</td>
            </tr>
        </#list>
    </tbody>
</#macro>

<@fragment/>