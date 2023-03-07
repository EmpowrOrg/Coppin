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
<#-- @ftlvariable name="assignment" type="org.empowrco.coppin.models.portal.AssignmentItem" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/markdown/markdown.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"
            integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script>
        $(document).ready(function () {
            const codemirror_config = {
                value: "",
                lineNumbers: true,
                mode: "text/x-markdown",
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
            }
            const instructionsTextArea = document.getElementById("instructions");
            const instructionsCodeMirror = CodeMirror(function (elt) {
                instructionsTextArea.parentNode.replaceChild(elt, instructionsTextArea);
            }, codemirror_config);
            instructionsCodeMirror.setSize('100%');
            const successTextArea = document.getElementById("success-message");
            const successCodeMirror = CodeMirror(function (elt) {
                successTextArea.parentNode.replaceChild(elt, successTextArea);
            }, codemirror_config);
            successCodeMirror.setSize('100%');
            const failureTextArea = document.getElementById("failure-message");
            const failureCodeMirror = CodeMirror(function (elt) {
                failureTextArea.parentNode.replaceChild(elt, failureTextArea);
            }, codemirror_config);
            failureCodeMirror.setSize('100%');
            $("#create-assignment").submit(function (eventObj) {
                console.log('create')
                eventObj.preventDefault()
                $("<input />")
                    .attr("name", "instructions")
                    .attr("type", "hidden")
                    .val(instructionsCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "success-message")
                    .attr("type", "hidden")
                    .val(successCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "failure-message")
                    .attr("type", "hidden")
                    .val(failureCodeMirror.getValue())
                    .appendTo(this);
                this.submit()
            });
        });
    </script>
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3">Create Assignment</h6>
                    </div>
                </div>
                <div class="card-body">
                    <form role="form" id="create-assignment" action="/assignments/create" method="post">
                        <div class="row">
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="title" class="form-control" placeholder="Title">
                            </div>
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="reference-id" class="form-control" placeholder="Reference Id">
                            </div>
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="number" name="total-attempts" class="form-control" min="0" max="10"
                                       placeholder="Total Attempts">
                            </div>
                        </div>
                        <label for="instructions">Instructions. Accepts Markdown Language</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="instructions"
                                                  name="instructions"
                                                  form="create-assignment"
                                                  class="form-control"
                                                  rows="5"
                                        ></textarea>
                        </div>
                        <label for="success-message">Success Message.</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="success-message"
                                                  name="success-message"
                                                  form="create-assignment"
                                                  class="form-control"
                                                  rows="2"></textarea>
                        </div>
                        <label for="failure-message">Failure Message.</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="failure-message"
                                                  name="failure-message"
                                                  form="create-assignment"
                                                  class="form-control"
                                                  rows="2"></textarea>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="Create">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</@layout.header>
