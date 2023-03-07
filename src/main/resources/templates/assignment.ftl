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
                value: "${assignment.instructions}",
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
            $("#edit-assignment").submit(function (eventObj) {
                eventObj.preventDefault()
                console.log('intercept')
                $("<input />")
                    .attr("name", "instructions")
                    .attr("type", "hidden")
                    .val(instructionsCodeMirror.getValue())
                    .appendTo(this);
                this.submit()
            });
            document.getElementById("delete-button").onclick = function () {
                var result = confirm("Are you sure you want to delete this assignment?");

                if (result) {
                    $("#delete-form").submit();
                } else {
                    // Do nothing; they cancelled
                }
            };
        });
    </script>
    <form id="delete-form" action="/assignments/${assignment.id}/delete" method="post" hidden>
    </form>
    <div class="row pb-4">
        <div class="page-header min-height-300 border-radius-xl mt-4"
             style="background-image: url('https://images.squarespace-cdn.com/content/v1/6115c8568ff9bd2eb31a6119/1629002711131-NZ8AN12RKLYEPOC56PXL/unsplash-image-AQ908FfdAMw.jpg&auto=format&fit=crop&w=1920&q=80');">
            <span class="mask  bg-gradient-primary  opacity-6"></span>
        </div>
        <div class="card card-body mx-3 mx-md-4 mt-n6 pb-2">
            <form role="form" id="edit-assignment" action="/assignments/${assignment.id}" method="post">
                <div class="row gx-4 mb-2">
                    <div class="col-md my-auto">
                        <div class="h-100">
                            <input id="title" name="title" class="mb-1" value="${assignment.title}"
                                   style="font-weight: bold;font-size: large">
                            <div class="form-group pt-2"
                                 style="display:flex; flex-direction: row; justify-content: left; align-items: center">
                                <label for="total-attempts" style="margin-right: 8px">Total Attempts: </label>
                                <input id="total-attempts" name="total-attempts" class="mb-0 font-weight-normal text-sm"
                                       type="number" value="${assignment.attempts}">
                            </div>
                            <p class="mb-0 font-weight-normal text-xs pt-2">
                                ${assignment.referenceId}
                            </p>
                        </div>
                    </div>
                    <div class="col-sm align-content-end">
                        <button type="button" id="delete-button" class="btn bg-gradient-primary float-end"><i
                                    class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="row pt-3">
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
                                  class="w-100 form-control"
                                  form="edit-assignment"
                                  style="resize: none">${assignment.successMessage}</textarea>
                        </div>
                    </div>
                    <div class="row pt-3 w-100">
                        <label for="failure-message" style="font-variant: small-caps;font-weight: bolder">Failure
                            Message.</label>
                        <div class="input-group input-group-outline mb-3">
                        <textarea id="failure-message"
                                  name="failure-message"
                                  class="w-100 form-control"
                                  form="edit-assignment"
                                  style="resize: none">${assignment.failureMessage}</textarea>
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
        </div>
    </div>
    <#include "assignment-codes.ftl">
    <#include "assignment-feedback.ftl">
</@layout.header>
