
<#-- @ftlvariable name="content" type="org.empowrco.coppin.admin.presenters.GetSecuritySettingsResponse" -->
<#import "../_layout.ftl" as layout />
<@layout.header >
<style>
    #admin-security-container {
        border-radius: 1rem;
        border: 0.125rem solid #DEE2E8;
        background: #FFF
    }

    #admin-security-header {
        display: flex;
        padding: 1.5rem 1rem;
        align-items: flex-start;
        gap: 0.5rem;
    }

</style>
<body class="bg-gray-200">
<main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">
    <div class="container-fluid pb-4">
        <div id="admin-security-container" class="row m-3 pb-4">
            <div id="admin-security-header" class="justify-content-between">
                <h3 class="mt-3 ms-1">Simple Sign On (SSO)</h3>
                <button id="ssoSaveButton" class="btn btn-primary">Save</button>
            </div>
            <div>
                <div class="admin-security-row">
                    <form style="width: 100%">
                        <h6 class="pb-0 mb-0">Okta Enabled</h6>
                        <div class="form-check form-switch pb-3">
                            <input class="form-check-input" name="oktaEnabled" type="checkbox" role="switch"
                                   id="oktaEnabled" <#if content.oktaEnabled>checked</#if>>
                        </div>
                        <div class="my-3 col">
                            <label for="oktaDomain" class="form-label"><h6>Okta Domain</h6></label>
                            <input name="okta-domain" id="oktaDomain" type="text" class="form-control"
                                   value="${content.oktaDomain}" <#if !content.oktaEnabled>disabled</#if>>
                        </div>
                        <div class="my-3 col">
                            <label for="oktaClientId" class="form-label"><h6>Client ID</h6></label>
                            <input name="okta-client-id" id="oktaClientId" type="text" class="form-control"
                                   value="${content.clientId}" <#if !content.oktaEnabled>disabled</#if>>
                        </div>
                        <div class="my-3 col">
                            <label for="oktaClientSecret" class="form-label"><h6>Client Secret</h6></label>
                            <input name="okta-client-secret" id="oktaClientSecret" type="text" class="form-control"
                                   value="${content.clientSecret}" <#if !content.oktaEnabled>disabled</#if>>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>
<script>
    document.getElementById("oktaEnabled").addEventListener('change', function () {
        document.getElementById("oktaDomain").disabled = !this.checked
        document.getElementById("oktaClientId").disabled = !this.checked
        document.getElementById("oktaClientSecret").disabled = !this.checked
        if (this.checked) {

        }
    });
</script>
<script>
    async function saveSecuritySettings() {
        const frm = `<form>
                         <div>Update security settings
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="password" name="password" id="password" type="password"
                                    class="form-control" placeholder="Password" required>
                         </div>
</form>`

        let dlg = new BsDialogs({
            close: true
        })
        dlg.form('Update Security Settings', 'Save', frm)
        let result = await dlg.onsubmit()
        if (result === undefined) {
            return
        }
        const password = result.password
        const oktaEnabled = document.getElementById("oktaEnabled").checked
        const oktaDomain = document.getElementById("oktaDomain").value
        const clientId = document.getElementById("oktaClientId").value
        const secret = document.getElementById("oktaClientSecret").value
        const body = JSON.stringify({
            oktaDomain: oktaDomain,
            clientId: clientId,
            clientSecret: secret,
            password: password,
            enableOkta: oktaEnabled,
            userId: "${content.userId}",
        })
        fetch('/admin/security', {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: body
        }).then(async response => {
            return await parseResponse(response)
        }).then(async res => {
            if (res.error) {
                throw new Error(res.error)
            } else {
                await new BsDialogs().ok('Settings Updated', '');
                window.location.replace("/admin/security")
            }
        }).catch(async error => {
            await showError(error)
        });
    }

    document.getElementById("ssoSaveButton").addEventListener("click", saveSecuritySettings);
</script>
</@layout.header>
