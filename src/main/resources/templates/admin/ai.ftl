<#-- @ftlvariable name="content" type="org.empowrco.coppin.admin.presenters.GetAiSettingsResponse" -->
<#import "../_layout.ftl" as layout />
<@layout.header >
<body class="bg-gray-200">
<script>
    function editClicked() {
        toggleElements(true)
        document.getElementById("edit-button").hidden = true
        document.getElementById("save-button").hidden = false
    }

    function toggleElements(enabled) {
        document.getElementById("aiModel").disabled = !enabled
        document.getElementById("orgKey").disabled = !enabled
        document.getElementById("key").disabled = !enabled
        document.getElementById("prePrompt").disabled = !enabled
    }

    $(document).ready(function () {
        document.getElementById("save-button").hidden = true
        toggleElements(false)
    })
</script>
<main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

    <div class="container-fluid pb-4">
        <div id="card-page-container" class="row m-3 pb-4">

            <div id="card-page-header" class="justify-content-between">
                <h3 class="mt-3 ms-1">Ai Settings</h3>

                <div style="display: flex">
                    <button id="edit-button" class="btn btn-primary" onclick="editClicked()">Edit</button>
                    <button type="submit" form="ai-settings-form" id="save-button" class="btn btn-primary">Save
                    </button>
                </div>
            </div>
            <p class="ms-2">Adding OpenAi keys enables your teachers to use ChatGPT to auto-create assignment
                instructions.</p>
            <#include "../error.ftl">
            <div>
                <div class="card-container-information-row">
                    <form id="ai-settings-form" name="ai-settings-form" style="width: 100%" method="post"
                          action="/admin/ai">
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="aiModel" class="form-label">Ai Model</label>
                                <input name="ai-model" id="aiModel" type="text" class="form-control"
                                       value="${content.model}">
                            </div>
                        </div>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="orgKey" class="form-label">Org Key</label>
                                <input name="org-key" id="orgKey" type="text" class="form-control"
                                       value="${content.org}">
                            </div>
                            <div class="my-3 col">
                                <label for="key" class="form-label">Api Key</label>
                                <input name="key" id="key" type="text" class="form-control" value="${content.key}">
                            </div>
                        </div>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="Pre Prompt" class="form-label">Pre Prompt</label>
                                <textarea name="pre-prompt" id="prePrompt" type="text"
                                          class="form-control">${content.prePrompt}</textarea>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

</@layout.header>
