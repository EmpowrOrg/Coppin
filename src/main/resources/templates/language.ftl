<#-- @ftlvariable name="content" type="org.empowrco.coppin.languages.presenters.GetLanguageResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/markdown/markdown.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"
            integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3"><#if content.name??>${content.name}<#else>Create Language</#if></h6>
                    </div>
                </div>
                <div class="card-body">
                    <#include "error.ftl">
                    <form role="form" id="create-assignment"
                          action="/languages/<#if content.id??>${content.id}</#if>"
                          method="post">
                        <div class="row">
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="name" class="form-control" placeholder="Name"
                                       <#if content.name??>value="${content.name}" </#if>>
                            </div>
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="mime" class="form-control" placeholder="Mime"
                                       <#if content.mime??>value="${content.mime}" </#if>>
                            </div>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="text" name="url" class="form-control" placeholder="CodeMirror Url"
                                   <#if content.url??>value="${content.url}" </#if>>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="<#if content.id??>Save<#else>Create</#if>">
                        </div>
                    </form>
                    <#if content.id??>
                        <div class="mt-3 d-flex justify-content-center">
                            <button id="delete-assignment" onclick="$('#deleteModal').modal('show')"
                                    data-toggle="modal" data-target="#assignModal"
                                    class="btn btn-lg btn-outline-danger" style="--bs-btn-border-color: transparent;">
                                Delete
                            </button>
                        </div>
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
            </div>
        </div>
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
