<#macro fragment>
    <div class="form-check">
        <label id="label-run-selected" class="form-check-label" for="new-run-selected-repeat-value">Number of times repeated:</label>
        <input class="form-check-input" name="new-run-selected-repeat-value" type="text" id="new-run-selected-repeat-value" value="${runTaskConfig.repeatCount}">
    </div>
    <div class="form-check">
        <label class="form-check-label" for="new-run-selected-delay-value">Delay between repeated runs (milliseconds):</label>
        <input class="form-check-input" name="new-run-selected-delay-value" type="text" id="new-run-selected-delay-value" value="${runTaskConfig.delayMsBetweenRepeats}">
    </div>
</#macro>

<@fragment/>