
<#-- @ftlvariable name="content" type="org.empowrco.coppin.users.presenters.GetUserResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
<style>
    #profile-container {
        border-radius: 1rem;
        border: 0.125rem solid #DEE2E8;
        background: #FFF
    }

    #profile-header {
        display: flex;
        padding: 1.5rem 1rem;
        align-items: flex-start;
        gap: 0.5rem;
    }

    .profile-subheader {
        padding: 0;
        margin-left: 1rem;
    }

    .profile-information-row {
        display: flex;
        padding: 1rem;
        flex-direction: column;
        align-items: flex-start;
        align-self: stretch;
    }

    .info-row {
        display: flex;
        align-items: flex-start;
        gap: 2rem;
    }

    .profile-information-row label {
        margin-bottom: 0;
        margin-left: 0;
    }

    #keys-header {
        display: flex;
        padding: 1rem;
        align-items: flex-start;
        gap: 0.5rem;
    }

    .keys-item-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .keys-item {
        display: flex;
        padding: 1rem;
        margin: 1rem;
        flex-direction: column;
        align-items: flex-start;
        align-self: stretch;
        border-radius: 16px;
        background: #F8F9FA;
    }

    .keys-detail {
        font-size: 14px !important;
        font-weight: 400 !important;
        line-height: 1.25rem !important;
        margin-bottom: 0 !important;
    }

</style>
<body class="bg-gray-200">
<main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg mt-4">
    <div class="container-fluid py-4">
        <div id="profile-container" class="row m-3 pb-4">

            <div id="profile-header" class="justify-content-between">
                <h3 class="mt-3 ms-1">${content.firstName} ${content.lastName}</h3>
                <button type="submit" form="profile-form" class="btn btn-primary">Save</button>
            </div>
            <#include "error.ftl">
            <div>
                <div class="profile-information-row">
                    <form id="profile-form" style="width: 100%" role="form" class="text-start" method="post"
                          action="/user/${content.id}">
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="firstName" class="form-label"><h6>First Name</h6></label>
                                <input name="firstName" id="firstName" type="text" class="form-control"
                                       value="${content.firstName}">
                            </div>
                            <div class="my-3 col">
                                <label for="lastName" class="form-label"><h6>Last Name</h6></label>
                                <input name="lastName" id="lastName" type="text" class="form-control"
                                       value="${content.lastName}">
                            </div>
                        </div>
                        <div class="col-2 info-row" style="width: 100%">
                            <div class="my-3 col">
                                <label for="email" class="form-label"><h6>Email</h6></label>
                                <input name="email" id="email" type="email" class="form-control"
                                       value="${content.email}">
                            </div>
                            <div class="my-3 col">
                                <label for="authorized" class="form-label"><h6>Authorized</h6></label>
                                <select class="form-select mb-3" id="authorized" name="authorized"
                                        aria-label=".form-select-lg example">
                                    <option value="true" <#if content.authorized>selected</#if>>Yes</option>
                                    <option value="false" <#if content.authorized><#else >selected</#if>>No</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-2 info-row" style="width: 100%">

                            <div class="my-3 col">
                                <label for="type" class="form-label"><h6>Role</h6></label>
                                <select class="form-select mb-3" id="type" name="type"
                                        aria-label=".form-select-lg example">
                                    <option value="Teacher" <#if content.type == "Teacher"> selected</#if>>Teacher
                                    </option>
                                    <option value="Admin" <#if content.type == "Admin"> selected</#if>>Admin</option>
                                </select>
                            </div>
                            <div class="my-3 col">
                            </div>
                        </div>

                    </form>
                </div>
            </div>
            <div id="snackbar">Copied Access Key</div>
            <div id="keys-header" class="justify-content-between">
                <h4>Access Keys</h4>
                <button id="create-key-button" class="btn btn-primary">Create Access Key</button>
            </div>
            <div>
                <#list content.keys as key>
                    <div class="keys-item row">
                        <div class="keys-item-header justify-content-between mb-2">
                            <h6 class="align-middle mb-0">${key.name}</h6>
                            <div class="row">
                                <button id="copy" class="copy btn col-sm text-black-50 me-3 mb-0"
                                        onclick="copyAccessKey('${key.key}')"><i class="material-icons opacity-10">content_copy</i>
                                </button>
                                <button id="delete" class="delete btn col-sm text-black-50 mb-0"
                                        onclick="deleteKey('${key.id}')"><i class="material-icons opacity-10">delete</i>
                                </button>
                            </div>

                        </div>

                        <p class="keys-detail" style="word-break: break-all">
                            <b>Key: </b>${key.key}
                        </p>
                        <p class="keys-detail">
                            <b>Created On:</b> ${key.createdAt}
                        </p>
                    </div>
                </#list>

            </div>
        </div>
    </div>
</main>
<script>
    async function createAccessKey() {
        const frm = `<form>
                         <div>Access Keys allow applications (Xblocks) to talk to the server.
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="name" name="name" id="name" type="text"
                                    class="form-control" placeholder="Key Name" required>
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="password" name="password" id="password" type="password"
                                    class="form-control" placeholder="Password" required>
                         </div>
</form>`

        let dlg = new BsDialogs({
            close: true
        })
        dlg.form('Create Access Key', 'Create', frm)
        let result = await dlg.onsubmit()
        if (result === undefined) {
            return
        }
        const password = result.password
        const body = JSON.stringify({
            password: password,
            id: '${content.id}',
            name: result.name,
        })
        fetch('/user/${content.id}/keys', {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: body
        }).then(async response => {
            return await parseResponse(response)
        }).then(async res => {
            if (res.error) {
                throw new Error(res.error)
            } else {
                await new BsDialogs().ok('Access Key Created', 'New access key created');
                window.location.replace("/user/${content.id}")
            }
        }).catch(async error => {
            await showError(error)
        });
    }

    document.getElementById("create-key-button").addEventListener("click", createAccessKey);
</script>
<script>
    function copyAccessKey(key) {
        navigator.clipboard.writeText(key);
        const snackbar = document.getElementById("snackbar");
        snackbar.innerText
        snackbar.className = "show";
        setTimeout(function () {
            snackbar.className = snackbar.className.replace("show", "");
        }, 3000);
    }
</script>
<script>
    async function deleteKey(keyId) {
        const frm = `<form>
                         <div>This is a permanent action. Once you delete this access
                             key, all applications and Xblocks utilizing this access key will no longer
                             work.
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="password" name="password" id="password" type="password"
                                    class="form-control" placeholder="Password" required>
                         </div>
</form>`
        let dlg = new BsDialogs({close: true})
        dlg.form('Delete Access Key', 'Delete', frm)
        let result = await dlg.onsubmit()
        if (result === undefined) {
            return
        }
        const password = result.password
        deleteAccessKey(keyId, password)
    }
</script>
<script>
    function deleteAccessKey(keyId, password) {
        const body = JSON.stringify({
            password: password,
            userId: '${content.id}',
            id: keyId,
        })
        fetch('/user/${content.id}/keys/' + keyId, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
            body: body
        }).then(async response => {
            return await parseResponse(response)
        }).then(async res => {
            if (res.error) {
                throw new Error(res.error)
            } else {
                await new BsDialogs().ok('Access Key Deleted', 'All applications and xblocks using this access key will no longer work.');
                window.location.replace("/user/${content.id}")
            }
        }).catch(async error => {
            await showError(error)
        });
    }
</script>


</@layout.header>
