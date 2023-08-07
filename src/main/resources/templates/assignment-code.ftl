<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetCodeResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.js"
            integrity="sha512-8RnEqURPUc5aqFEN04aQEiPlSAdE0jlFS/9iGgUyNtwFnSKCXhmB6ZTNl7LnDtDWKabJIASzXrzD0K+LYexU9g=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.css"
          integrity="sha512-uf06llspW44/LZpHzHT6qBOIVODjWtv4MxCricRxkzvopAlSWnTf6hpZTFxuuZcuNE9CBQhqE0Seu1CoRk84nQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <#list content.languages as language>
        <script src="${language.url}" crossorigin="anonymous"></script>
    </#list>
    <script>
        $(document).ready(function () {
            const codeMirrorConfig = {
                lineNumbers: true,
                mode: "${content.language.mime}",
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
            }
            <#if content.starterCode??>
            codeMirrorConfig.value = "${content.starterCode}"
            </#if>
            const starterCodeTextArea = document.getElementById("starter-code-editor");
            const starterCodeCodeMirror = CodeMirror(function (elt) {
                starterCodeTextArea.parentNode.replaceChild(elt, starterCodeTextArea);
            }, codeMirrorConfig);
            starterCodeCodeMirror.setSize('100%');
            const solutionConfig = codeMirrorConfig
            <#if content.solutionCode??>
            solutionConfig.value = "${content.solutionCode}"
            </#if>
            const solutionCodeTextArea = document.getElementById("solution-code-editor");
            const solutionCodeCodeMirror = CodeMirror(function (elt) {
                solutionCodeTextArea.parentNode.replaceChild(elt, solutionCodeTextArea);
            }, solutionConfig);
            solutionCodeCodeMirror.setSize('100%');
            const unitTestConfig = codeMirrorConfig
            <#if content.unitTest??>
            unitTestConfig.value = "${content.unitTest}"
            </#if>
            const unitTestTextArea = document.getElementById("unit-tests-editor");
            const unitTestCodeMirror = CodeMirror(function (elt) {
                unitTestTextArea.parentNode.replaceChild(elt, unitTestTextArea);
            }, unitTestConfig);
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
    <style>
        #code-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #code-header {
            display: flex;
            padding: 1.5rem 1rem 0 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }
    </style>
    <div class="container-fluid">
        <form role="form"
              action="/courses/${content.courseId}/assignments/${content.assignmentId}/codes/<#if content.id??>${content.id}</#if>"
              id="create-assignment-code" method="post">
            <div id="code-container" class="row m-3 p-4">
                <div id="code-header" class="justify-content-between m-0 p-0">
                    <h3>Assignment Code</h3>
                    <div>
                        <button class="btn btn-primary">Save</button>
                        <#if content.id??>
                            <button type="button" class="ms-4 instructions btn btn-danger"
                                    data-bs-toggle="modal" data-bs-target="#deleteModal">Delete
                            </button>
                        </#if>
                    </div>
                </div>
                <#include "error.ftl">
                <select class="form-select mb-3 p-2" name="language" aria-label=".form-select-lg example"
                        style="max-width: 400px">
                    <option selected disabled>Select a language</option>
                    <#list content.languages as language>
                        <option value="${language.mime}"
                                <#if language.selected>selected</#if>>${language.name}</option>
                    </#list>
                </select>
                <div class="form-check form-switch">
                    <input class="form-check-input" name="primary" type="checkbox" role="switch"
                           id="primary" ${content.primary?string('checked','')}>
                    <label class="form-check-label" for="primary">Primary Language</label>
                </div>
                <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" name="injectable" role="switch"
                           id="injectable" ${content.injectable?string('checked','')}>
                    <label class="form-check-label" for="injectable">Injectable</label>
                </div>
                <h6 class="mt-3 ms-0 ps-0">Starter Code</h6>
                <div id="starter-code" class="ms-0 ps-0">
                <textarea id="starter-code-editor"
                          class="form-control"
                          rows="5"></textarea>
                </div>
                <h6 class="mt-3 ms-0 ps-0 required-field">Solution Code</h6>
                <div id="solution-code" class="ms-0 ps-0">
                <textarea id="solution-code-editor"
                          class="form-control"
                          rows="5"></textarea>
                </div>
                <h6 class="mt-3 ms-0 ps-0 required-field">Unit Test Class</h6>
                <div id="unit-tests" class="ms-0 ps-0">
                <textarea id="unit-tests-editor"
                          class="form-control"
                          rows="5"></textarea>
                </div>

            </div>
        </form>
        <#if content.id??>
            <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog"
                 aria-labelledby="deleteModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
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
    <#if content.id??>
        <script>
            $('#delete-confirm').on('click', async function () {
                $('#deleteModal').modal('hide')
                fetch('/courses/${content.courseId}/assignments/${content.assignmentId}/codes/${content.id}', {
                    method: "DELETE",
                    headers: {'Content-Type': 'application/json'},
                }).then(async response => {
                    return await parseResponse(response);
                }).then(async res => {
                    if (res.error) {
                        throw new Error(res.error)
                    } else {
                        await new BsDialogs().ok('Code Deleted', 'Code was successfully deleted');
                        window.location.replace("/course/${content.courseId}/assignments/${content.assignmentId}")
                    }
                }).catch(async error => {
                    await showError(error);
                });
            });
        </script>
    </#if>

</@layout.header>
