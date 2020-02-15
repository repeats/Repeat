<#macro fragment>
	<div id="modal-language-config-language-name" style="display:none;">Java</div>
	<div class="form-check-label">It is advisable to restart the program if you remove a path from the class path list.</div>
	<div class="form-check-label">Class paths (1 path per row)</div>
	<div class="form-check-label">E.g. (Windows) D:\path\to\dir\the_jar.jar</div>
	<div class="form-check-label">E.g. (Unix/Linux) /path/to/dir/the_jar.jar</div>
    <textarea class="form-control" rows="30" id="new-java-compiler-class-paths">${classPaths}</textarea>
</#macro>

<@fragment/>