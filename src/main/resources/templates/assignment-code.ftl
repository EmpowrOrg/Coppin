<!--
=========================================================
* Material Dashboard 2 - v3.0.4
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard
* Copyright 2022 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://www.creative-tim.com/license)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
-->
<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetCodeResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <#list content.languages as language>
        <script src="${language.url}" crossorigin="anonymous"></script>
    </#list>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"
            integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script>
        $(document).ready(function () {
            const codemirror_config = {
                value: "${content.starterCode}",
                lineNumbers: true,
                mode: "${content.language.mime}",
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
            }
            const starterCodeTextArea = document.getElementById("starter-code");
            const starterCodeCodeMirror = CodeMirror(function (elt) {
                starterCodeTextArea.parentNode.replaceChild(elt, starterCodeTextArea);
            }, codemirror_config);
            starterCodeCodeMirror.setSize('100%');
            const solution_config = codemirror_config
            solution_config.value = "${content.solutionCode}"
            const solutionCodeTextArea = document.getElementById("solution-code");
            const solutionCodeCodeMirror = CodeMirror(function (elt) {
                solutionCodeTextArea.parentNode.replaceChild(elt, solutionCodeTextArea);
            }, solution_config);
            solutionCodeCodeMirror.setSize('100%');
            const unit_test_config = codemirror_config
            unit_test_config.value = "${content.unitTest}"
            const unitTestTextArea = document.getElementById("unit-test-code");
            const unitTestCodeMirror = CodeMirror(function (elt) {
                unitTestTextArea.parentNode.replaceChild(elt, unitTestTextArea);
            }, unit_test_config);
            unitTestCodeMirror.setSize('100%');
            $("#create-assignment-code").submit(function (eventObj) {
                eventObj.preventDefault()
                $("<input />")
                    .attr("name", "starter-code")
                    .attr("type", "hidden")
                    .val(starterCodeCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "solution-code")
                    .attr("type", "hidden")
                    .val(solutionCodeCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "unit-test-code")
                    .attr("type", "hidden")
                    .val(unitTestCodeMirror.getValue())
                    .appendTo(this);
                this.submit()
            });
            $("#language").change(function () {
                solutionCodeCodeMirror.setOption("mode", $(this).val());
                starterCodeCodeMirror.setOption("mode", $(this).val());
                unitTestCodeMirror.setOption("mode", $(this).val());
            });
        });
    </script>
    <form id="delete-code-form" action="/assignments/${content.assignmentId}/codes/${content.id}/delete"
          method="post" hidden>
    </form>
    <div class="row" xmlns="http://www.w3.org/1999/html">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3">Create Assignment Code</h6>
                    </div>
                </div>
                <div class="card-body">
                    <form role="form" id="create-assignment-code"
                          action="/assignments/${content.assignmentId}/codes/${content.id}"
                          method="post">
                        <div class="row col-lg-12 align-items-center mb-3">
                            <#if content.languages?has_content>
                                <div class="col">
                                    <div class="form-check form-check-inline">
                                        <label class="d-inline-block me-2" for="language">Language</label>
                                        <select class="d-inline-block form-select" id="language" name="language"
                                                style="width: auto; min-width: 200px" form="create-assignment-code">
                                            <#list content.languages as language>
                                                <option value="${language.mime}">${language.name}</option>
                                            </#list>
                                        </select>
                                    </div>
                                </div>
                            </#if>
                            <div class="col-2">
                                <div class="align-items-center ">
                                    <div class="d-inline-block form-check-label">Primary Language</div>
                                    <div class="ms-2 form-check form-switch d-inline-block align-items-center">
                                        <input class="form-check-input mt-2 d-inline-block" type="checkbox" id="primary"
                                               name="primary" ${content.primary?string('checked','')}>
                                        <label class="form-check-label" for="primary"></label>
                                    </div>

                                </div>
                            </div>
                        </div>
                        <label for="starter-code">Starter Code.</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="starter-code"
                                                  name="starter-code"
                                                  form="create-assignment-code"
                                                  class="form-control"
                                                  rows="5"
                                        ></textarea>
                        </div>
                        <label for="solution-code">Solution Code.</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="solution-code"
                                                  name="solution-code"
                                                  form="create-assignment-code"
                                                  class="form-control"
                                                  rows="5"
                                        ></textarea>
                        </div>
                        <label for="unit-test-code">Unit Tests (Include Test Class)</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="unit-test-code"
                                                  name="unit-test-code"
                                                  form="create-assignment-code"
                                                  class="form-control"
                                                  rows="5"
                                        ></textarea>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="Save">
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
                                        <h5 class="modal-title" id="deleteModalLabel">Delete Assignment Code</h5>
                                    </div>
                                    <div class="modal-body">
                                        By clicking delete, you will permanently delete this assignment code.
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
    <script>
        $('#delete-confirm').on('click', async function () {
            $('#deleteModal').modal('hide')
            fetch('/assignments/${content.assignmentId}/codes/${content.id}', {
                method: "DELETE",
                headers: {'Content-Type': 'application/json'},
            }).then(async response => {
                return await parseResponse(response);
            }).then(async res => {
                if (res.error) {
                    throw new Error(res.error)
                } else {
                    await new BsDialogs().ok('Code Deleted', 'Code was successfully deleted');
                    window.location.replace("/assignments/${content.assignmentId}")
                }
            }).catch(async error => {
                await showError(error);
            });
        });
    </script>
</@layout.header>
