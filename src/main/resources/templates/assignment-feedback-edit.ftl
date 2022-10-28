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
<#-- @ftlvariable name="feedback" type="org.empowrco.coppin.models.portal.FeedbackItem" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"
            integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script>
        $(document).ready(function () {
            document.getElementById("delete-feedback-button").onclick = function () {
                var result = confirm("Are you sure you want to delete this feedback?");

                if (result) {
                    $("#delete-feedback-form").submit();
                } else {
                    // Do nothing; they cancelled
                }
            };
        });
    </script>
    <form id="delete-feedback-form" action="/assignments/${feedback.assignmentId}/feedback/${feedback.id}/delete"
          method="post" hidden>
    </form>
    <div class="row" xmlns="http://www.w3.org/1999/html">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3"><#if feedback??><#else>Create </#if>Assignment
                            Feedback</h6>
                    </div>
                    <#if feedback??>
                        <div class="align-content-end">
                            <button type="button" id="delete-feedback-button" class="btn bg-gradient-primary float-end">
                                <i
                                        class="fa fa-trash"></i>
                            </button>
                        </div>
                    </#if>
                </div>
                <div class="card-body">
                    <form role="form" id="create-assignment-feedback"
                          action="/assignments/${feedback.assignmentId}/feedback<#if feedback??>/${feedback.id}</#if>"
                          method="post">
                        <div class="row">
                            <div class="form-group col-sm input-group input-group-outline"
                                 style="display:flex; flex-direction: row; justify-content: left; align-items: center">
                                <label for="attempt" style="margin-right: 8px">Attempt: </label>
                                <input id="attempt" name="attempt" class="form-control mb-0 font-weight-normal text-sm"
                                       type="number" value="<#if feedback??>${feedback.attempt}</#if>">
                            </div>
                            <div class="col-sm input-group input-group-outline">
                                <input class="form-control" type="text" id="regex" name="regex" placeholder="Regex"
                                       value="<#if feedback??>${feedback.regex}</#if>">
                            </div>
                        </div>
                        <label for="feedback">Feedback</label>
                        <div class="input-group input-group-outline mb-3">
                                        <textarea id="feedback"
                                                  name="feedback"
                                                  form="create-assignment-feedback"
                                                  class="form-control"
                                                  rows="5"
                                        ><#if feedback??>${feedback.feedback}</#if></textarea>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="<#if feedback??>Save<#else>Create</#if>">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</@layout.header>
