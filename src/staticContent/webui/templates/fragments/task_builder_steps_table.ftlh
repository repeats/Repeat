<#macro fragment>
    <table id="manually-build-action-table-steps" class="table">
        <thead class="thead-dark">
            <tr>
                <th scope="col">Action steps (Double click to remove)</th>
            </tr>
        </thead>
        <tbody>
            <#list constructor.steps as step>
                <tr>
                    <td>${step}</td>
                </tr>
            </#list>
        </tbody>
    </table>
</#macro>
