<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetAssignmentPortalResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/markdown/markdown.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <script>
        $(document).ready(function () {
            const baseCodeMirrorConfig = {
                lineNumbers: true,
                mode: "text/x-markdown",
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
            }
            const instructionsConfig = baseCodeMirrorConfig
            <#if content.instructions??>
            instructionsConfig.value = "${content.instructions}"
            </#if>
            const instructionsTextArea = document.getElementById("instructions");
            const instructionsCodeMirror = CodeMirror(function (elt) {
                instructionsTextArea.parentNode.replaceChild(elt, instructionsTextArea);
            }, instructionsConfig);
            instructionsCodeMirror.setSize('100%');
            const successConfig = baseCodeMirrorConfig
            <#if content.successMessage??>
            successConfig.value = "${content.successMessage}"
            </#if>
            const successMessageTextArea = document.getElementById("success-message");
            const successMessageCodeMirror = CodeMirror(function (elt) {
                successMessageTextArea.parentNode.replaceChild(elt, successMessageTextArea);
            }, successConfig);
            successMessageCodeMirror.setSize('100%');
            const failureConfig = baseCodeMirrorConfig
            <#if content.failureMessage??>
            failureConfig.value = "${content.failureMessage}"
            </#if>
            const failureMessageTextArea = document.getElementById("failure-message");
            const failureMessageCodeMirror = CodeMirror(function (elt) {
                failureMessageTextArea.parentNode.replaceChild(elt, failureMessageTextArea);
            }, failureConfig);
            failureMessageCodeMirror.setSize('100%');
            $("#edit-assignment").submit(function (eventObj) {
                eventObj.preventDefault()
                console.log('intercept')
                $("<input />")
                    .attr("name", "instructions")
                    .attr("type", "hidden")
                    .val(instructionsCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "success-message")
                    .attr("type", "hidden")
                    .val(successMessageCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "failure-message")
                    .attr("type", "hidden")
                    .val(failureMessageCodeMirror.getValue())
                    .appendTo(this);
                this.submit()
            });
        });
    </script>
<#if content.id??>
    <form id="delete-form" action="/assignments/${content.id}/delete" method="post" hidden>
        </#if>
    </form>
    <div class="row pb-4">
        <div class="page-header min-height-300 border-radius-xl mt-4"
             style="background-image: url('https://images.squarespace-cdn.com/content/v1/6115c8568ff9bd2eb31a6119/1629002711131-NZ8AN12RKLYEPOC56PXL/unsplash-image-AQ908FfdAMw.jpg&auto=format&fit=crop&w=1920&q=80');">
            <span class="mask  bg-gradient-primary  opacity-6"></span>
        </div>
        <div class="card card-body mx-3 mx-md-4 mt-n6 pb-2">
            <form role="form" id="edit-assignment"
                  action="/assignments/<#if content.id??>${content.id}<#else >create</#if>"
                  method="post">
                <div class="mt-3 d-flex justify-content-center">
                    <#include "error.ftl">
                </div>
                <div class="row col-lg-12">
                    <div class="col-6">
                        <div class="input-group input-group-outline my-3">
                            <label for="title" class="form-label">Title</label>
                            <input name="title" id="title" type="text" class="form-control"
                                   value="<#if content.title??>${content.title}</#if>">
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="input-group input-group-outline my-3">
                            <label for="reference-id" class="form-label">Reference Id</label>
                            <input name="reference-id" id="reference-id" type="text" class="form-control"
                                   value="<#if content.referenceId??>${content.referenceId}</#if>">
                        </div>
                    </div>
                </div>
                <div class="row col-lg-3">
                    <div class="col-6">
                        <div class="input-group input-group-outline my-3">
                            <label for="total-attempts" class="form-label">Total Attempts</label>
                            <input name="total-attempts" id="total-attempts" type="number" class="form-control"
                                   value="<#if content.attempts??>${content.attempts}</#if>">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <p style="font-variant: all-petite-caps">All fields below accept Markdown</p>
                    <div class="row">
                        <label for="instructions"
                               style="font-variant: small-caps;font-weight: bolder">Instructions.</label>
                        <div class="input-group input-group-outline mb-3">
                        <textarea id="instructions"
                                  form="edit-assignment"
                                  rows="5">
                        </textarea>
                        </div>
                    </div>
                    <div class="row pt-3 w-100">
                        <label for="success-message" style="font-variant: small-caps;font-weight: bolder">Success
                            Message.</label>
                        <div class="input-group input-group-outline mb-3">
                        <textarea id="success-message"
                                  name="success-message"
                                  form="edit-assignment"></textarea>
                        </div>
                    </div>
                    <div class="row pt-3 w-100">
                        <label for="failure-message" style="font-variant: small-caps;font-weight: bolder">Failure
                            Message.</label>
                        <div class="input-group input-group-outline mb-3">
                        <textarea id="failure-message"
                                  name="failure-message"
                                  form="edit-assignment"></textarea>
                        </div>
                    </div>
                    <div class="col-sm input-group input-group-outline mb-3">
                        <input
                                type="submit"
                                class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                value="Save">
                    </div>

                </div>

            </form>
            <div class="mt-3 d-flex justify-content-center">
                <button id="delete-assignment" onclick="$('#deleteModal').modal('show')"
                        data-toggle="modal" data-target="#assignModal"
                        class="btn btn-lg btn-outline-danger" style="--bs-btn-border-color: transparent;">Delete
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
                            By clicking delete, you will permanently delete this assignment. Any Xblock's
                            referencing this assignment will cease to work.
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
        </div>
    </div>

    <#if content.id??>
        <#include "assignment-codes.ftl">
        <#include "assignment-feedback.ftl">
        <script>
            $('#delete-confirm').on('click', async function () {
                $('#deleteModal').modal('hide')
                fetch('/assignments/${content.id}', {
                    method: "DELETE",
                    headers: {'Content-Type': 'application/json'},
                }).then(async response => {
                    return await parseResponse(response)
                }).then(async res => {
                    if (res.error) {
                        throw new Error(res.error)
                    } else {
                        await new BsDialogs().ok('Assignment Deleted', 'Assignment was successfully deleted');
                        window.location.replace("/assignments")
                    }
                }).catch(async error => {
                    await showError(error)
                });
            });
        </script>
    </#if>

</@layout.header>
