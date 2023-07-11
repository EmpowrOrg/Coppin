<#-- @ftlvariable name="content" type="org.empowrco.coppin.languages.presenters.GetLanguageResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <style>
        #language-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #language-header {
            display: flex;
            padding: 1.5rem 1rem 0 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }
    </style>
    <div class="container-fluid py-4">
        <form role="form" id="create-assignment"
              action="/languages/<#if content.id??>${content.id}</#if>"
              method="post">
            <div id="language-container" class="row m-3 p-4">
                <div id="language-header" class="justify-content-between">
                    <div></div>
                    <div>
                        <button class="btn btn-primary">Save</button>
                        <#if content.id??>
                            <button type="button" class="btn btn-danger" data-bs-toggle="modal"
                                    data-bs-target="#deleteModal">Delete
                            </button>
                        </#if>
                    </div>
                </div>
                <#include "error.ftl">
                <label for="name" class="form-label"><h6>Name</h6></label>
                <input name="name" id="name" type="text" class="form-control"
                       <#if content.name??>value="${content.name}" </#if>>
                <label for="url" class="form-label mt-4"><h6>CodeMirror Url</h6></label>
                <input name="url" id="url" type="url" class="form-control"
                       <#if content.url??>value="${content.url}" </#if>>
                <label for="mime" class="form-label mt-4"><h6>Mime</h6></label>
                <input name="mime" id="mime" type="text" class="form-control"
                       <#if content.mime??>value="${content.mime}" </#if>>
                <label for="regex" class="form-label mt-4"><h6>Unit Test Regex</h6></label>
                <input name="regex" id="regex" type="text" class="form-control"
                       <#if content.unitTestRegex??>value="${content.unitTestRegex}" </#if>>
            </div>
        </form>
        <#if content.id??>
            <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog"
                 aria-labelledby="deleteModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="deleteModalLabel">Delete Assignment</h5>
                        </div>
                        <div class="modal-body">
                            By clicking delete, you will permanently delete this language.
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary"
                                    data-bs-dismiss="modal">Close
                            </button>
                            <button type="submit" class="btn btn-primary"
                                    id="delete-confirm"
                            >
                                Delete
                            </button>

                        </div>
                    </div>
                </div>
            </div>
        </#if>
    </div>
    <#if content.id??>
        <script>
            $('#delete-confirm').on('click', async function () {
                $('#deleteModal').modal('hide')
                fetch('/languages/${content.id}', {
                    method: "DELETE",
                    headers: {'Content-Type': 'application/json'},
                }).then(async response => {
                    return await parseResponse(response)
                }).then(async res => {
                    if (res.error) {
                        throw new Error(res.error)
                    } else {
                        await new BsDialogs().ok('Language Deleted', 'Language was successfully deleted');
                        window.location.replace("/languages")
                    }
                }).catch(async error => {
                    await showError(error)
                });
            });
        </script>
    </#if>

</@layout.header>
