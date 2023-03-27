
<#-- @ftlvariable name="content" type="org.empowrco.coppin.users.presenters.GetUserResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >

<body class="bg-gray-200">
<main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg mt-4">
    <div class="container-fluid py-4 pe-2">
        <div class="row">
            <div class="col-lg-12 col-md-12 col-12 mx-auto">
                <div class="card z-index-0 fadeIn3 fadeInBottom">
                    <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                        <div class="bg-gradient-primary shadow-primary border-radius-lg py-3 pe-1">
                            <h4 class="text-white font-weight-bolder text-center mt-2 mb-0">Edit User</h4>
                        </div>
                    </div>
                    <div class="card-body">
                        <#include "error.ftl">
                        <form role="form" class="text-start" method="post" action="/user/${content.id}">
                            <div class="row">
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">First Name</label>
                                    <input name="firstName" type="text" class="form-control"
                                           value="${content.firstName}" readonly>
                                </div>
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">Last Name</label>
                                    <input name="lastName" type="text" class="form-control"
                                           value="${content.lastName}" readonly>
                                </div>
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">Email</label>
                                    <input name="email" type="email" class="form-control"
                                           value="${content.email}" readonly>
                                </div>
                            </div>
                            <div class="row">
                                <div class="form-group col-md">
                                    <label for="authorized">Authorized</label>
                                    <select name="authorized" id="authorized" class="form-select">
                                        <option value="true"
                                                <#if content.authorized>selected</#if>>Yes
                                        </option>
                                        <option value="false"
                                                <#if !content.authorized>selected</#if>>No
                                        </option>
                                    </select>
                                </div>
                                <div class="form-group col-md">
                                    <label for="type">Role</label>
                                    <select name="type" id="type" class="form-select">
                                        <option value="Admin"
                                                <#if content.type == "Admin">selected</#if>>Admin
                                        </option>
                                        <option value="Teacher"
                                                <#if content.type == "Teacher">selected</#if>>Teacher
                                        </option>
                                    </select>
                                </div>
                            </div>


                            <#if content.id??>
                                <input type="hidden" name="id" value="${content.id}">
                            </#if>
                            <div class="text-center col-sm">
                                <button type="submit" class="btn bg-gradient-primary my-4 mb-2 px-6">Save
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <#if content.keys?has_content>
            <script>
                $(document).ready(function () {
                    const table = $('#keys-table').DataTable({
                        language: {search: ""},
                        columnDefs: [
                            {
                                targets: -2,
                                data: null,
                                defaultContent: '<div class ="row"><div id="copy" class="copy col-sm text-black-50"> <i class="material-icons opacity-10">content_copy</i> </div> <div id="delete" class="delete col-sm text-black-50"> <i class="material-icons opacity-10">delete</i> </div></div>'
                            },
                            {
                                target: 2,
                                visible: false,
                            }
                        ],
                    });
                    $('#keys-table_filter').addClass('me-4').find("input").addClass('form-control').attr("placeholder", "Search");
                    $('#keys-table_length').addClass('form-group').addClass('ms-4').addClass('form-inline');
                    $('#keys-table_info').addClass('ms-4').addClass('text-sm')
                    $('#keys-table_paginate').addClass('me-4')
                    $("#keys-table tbody tr").on("click", "#copy", function () {
                        const data = table.row($(this).parents('tr')).data()[0];
                        navigator.clipboard.writeText(data);
                        const snackbar = document.getElementById("snackbar");
                        snackbar.innerText
                        snackbar.className = "show";
                        setTimeout(function () {
                            snackbar.className = snackbar.className.replace("show", "");
                        }, 3000);
                    });
                    $("#keys-table tbody tr").on("click", "#delete", async function () {
                        const keyId = table.row($(this).parents('tr')).data()[2];
                        const frm = `<form>
                         <div>This is a permanent action. Once you delete this access
                             key, all Xblocks utilizing this access key will no longer
                             work.
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="password" name="password" id="password" type="password"
                                    class="form-control" placeholder="Password" required>
                         </div>
</form>`
                        let dlg = new BsDialogs()
                        dlg.form('Delete Access Key', 'Delete', frm)
                        let result = await dlg.onsubmit()
                        const password = result.password
                        deleteAccessKey(keyId, password)

                    });

                });
            </script>
            <div id="snackbar">Copied Access Key</div>
            <div class="row mt-6">
                <div class="col-lg-12 col-md-12 col-12 mx-auto">
                    <div class="card z-index-0 fadeIn3 fadeInBottom">
                        <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                            <div class="bg-gradient-primary shadow-primary border-radius-lg py-3 pe-1">
                                <h4 class="text-white font-weight-bolder text-center mt-2 mb-0">Keys</h4>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table id="keys-table" class="table align-items-center mb-0">
                                    <thead>
                                    <tr>
                                        <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                            Key
                                        </th>
                                        <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                            Actions
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <#list content.keys as key>
                                        <tr>
                                            <td class="text-sm">
                                                ${key.key}
                                            </td>
                                            <td></td>
                                            <td class="align-middle">${key.id}</td>
                                        </tr>

                                    </#list>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        <#else >
            <div class="text-center col-sm">
                <button type="button" data-toggle="modal" onclick="createAccessKey()"
                        data-target="#createKeyModal" class="btn bg-gradient-primary my-4 mb-2 px-6">Create Access Key
                </button>
            </div>
        </#if>
        <div class="fixed-plugin">
            <button id="create-button" type="button" onclick="createAccessKey()"
                    class="fixed-plugin-button text-dark position-fixed px-3 py-2">
                <i class="material-icons py-2">add</i>
            </button>
        </div>
    </div>
</main>
<script>
    async function createAccessKey() {
        const frm = `<form>
                         <div>Access Keys allow applications (Xblocks) to talk to the server.
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
        const password = result.password
        const body = JSON.stringify({
            password: password,
            id: '${content.id}'
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

    document.getElementById("create-button").addEventListener("click", createAccessKey);
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
                await new BsDialogs().ok('Access Key Deleted', 'All applications using this access key will no longer work.');
                window.location.replace("/user/${content.id}")
            }
        }).catch(async error => {
            await showError(error)
        });
    }
</script>


</@layout.header>
