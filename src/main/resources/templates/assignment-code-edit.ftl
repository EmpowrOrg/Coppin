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
<#-- @ftlvariable name="code" type="org.empowrco.coppin.models.portal.AssignmentCodeItem" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <#list code.languages as language>
        <script src="${language.url}" crossorigin="anonymous"></script>
    </#list>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script>
        $(document).ready(function() {
            const codemirror_config = {
                value: "${code.starterCode}",
                lineNumbers: true,
                mode: "${code.language.mime}",
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
            solution_config.value = "${code.solutionCode}"
            const solutionCodeTextArea = document.getElementById("solution-code");
            const solutionCodeCodeMirror = CodeMirror(function (elt) {
                solutionCodeTextArea.parentNode.replaceChild(elt, solutionCodeTextArea);
            }, solution_config);
            solutionCodeCodeMirror.setSize('100%');
            $("#create-assignment-code").submit( function(eventObj) {
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
                this.submit()
            });
            $("#language").change(function () {
                solutionCodeCodeMirror.setOption("mode", $(this).val());
            });
            document.getElementById("delete-code-button").onclick = function () {
                var result = confirm("Are you sure you want to delete this assignment code?");

                if (result) {
                    $("#delete-code-form").submit();
                } else {
                    // Do nothing; they cancelled
                }
            };
        });
    </script>
    <form id="delete-code-form" action="/assignments/${code.assignmentId}/codes/${code.id}/delete"
          method="post" hidden>
    </form>
    <div class="row" xmlns="http://www.w3.org/1999/html">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3">Create Assignment Code</h6>
                    </div>
                    <div class="col-sm align-content-end">
                        <button type="button" id="delete-code-button" class="btn bg-gradient-primary float-end"><i
                                    class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <form role="form" id="create-assignment-code" action="/assignments/${code.assignmentId}/codes/${code.id}"
                          method="post">
                        <div class="row">
                            <div class="col-sm form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="primary" name="primary" ${code.primary?string('checked','')}>
                                <label class="form-check-label" for="primary">Primary</label><br>

                            </div>
                            <select class="form-select" id="language" name="language" form="create-assignment-code">
                                <#list code.languages as language>
                                    <option value="${language.mime}">${language.name}</option>
                                </#list>
                            </select>
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
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="Save">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</@layout.header>
