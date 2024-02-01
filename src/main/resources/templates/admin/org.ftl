<#-- @ftlvariable name="content" type="org.empowrco.coppin.admin.presenters.GetOrgSettingsResponse" -->
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
        document.getElementById("edxApiUrl").disabled = !enabled
        document.getElementById("userName").disabled = !enabled
        document.getElementById("edxClientSecret").disabled = !enabled
        document.getElementById("edxClientId").disabled = !enabled
        document.getElementById("doctorUrl").disabled = !enabled
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
                <h3 class="mt-3 ms-1">Org Settings</h3>
                <div style="display: flex">
                    <button id="edit-button" class="btn btn-primary" onclick="editClicked()">Edit</button>
                    <button type="submit" form="org-settings-form" id="save-button" class="btn btn-primary">Save
                    </button>
                </div>
            </div>
            <#include "../error.ftl">
            <div>
                <div class="card-container-information-row">
                    <form id="org-settings-form" name="org-settings-form" style="width: 100%" method="post"
                          action="/admin">
                        <h6>Doctor Settings</h6>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="doctorUrl" class="form-label">Url</label>
                                <input name="doctor-url" id="doctorUrl" type="url" class="form-control"
                                       value="${content.doctorUrl}">
                            </div>
                        </div>
                        <h6>Edx Settings</h6>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="edxApiUrl" class="form-label">Api Url</label>
                                <input name="edx-api-url" id="edxApiUrl" type="url" class="form-control"
                                       value="${content.edxApiUrl}">
                            </div>
                            <div class="my-3 col">
                                <label for="userName" class="form-label">Username</label>
                                <input name="username" id="userName" type="text" class="form-control"
                                       value="${content.edxUsername}">
                            </div>
                        </div>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="edxClientId" class="form-label">Client Id</label>
                                <input name="edx-client-id" id="edxClientId" type="text" class="form-control"
                                       value="${content.edxClientId}">
                            </div>
                            <div class="my-3 col">
                                <label for="edxClientSecret" class="form-label">Client Secret</label>
                                <input name="edx-client-secret" id="edxClientSecret" type="text" class="form-control"
                                       value="${content.edxClientSecretDisplay}">
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>
</@layout.header>
