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
<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetAssignmentPortalResponse" -->

<!-- Data Tables -->
<link rel="stylesheet" type="text/css"
      href="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.css"/>

<script type="text/javascript"
        src="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.js"></script>
<script>
    $(document).ready(function () {
        const table = $('#assignment-feedback-table').DataTable({
            language: {
                search: "",
                emptyTable: "It is recommended to always give student feedback"
            },
            columnDefs: [
                {
                    target: 3,
                    visible: false,
                },
                {
                    target: 4,
                    visible: false,
                }
            ],
        });
        $('#assignment-feedback-table_filter').find("input").addClass('form-control').attr("placeholder", "Search");
        $('#assignment-feedback-table tbody').on('click', 'tr', function () {
            const data = table.row(this).data();
            window.location = "/assignments/" + data[4] + "/feedback/" + data[3]
        });
    });
</script>
<div class="row">
    <div class="col-12">
        <div class="card my-4">
            <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                    <h6 class="text-white text-capitalize ps-3" style="display:inline-block;">Feedback</h6>
                    <a class="btn btn-lg bg-gradient-dark btn-lg mb-0"
                       href="/assignments/${content.assignment.id}/feedback"
                       style="display:inline-block;margin-left: 20px">Create
                    </a>
                </div>
            </div>
            <div class="card-body px-0 pb-2">
                <div class="table-responsive p-2">
                    <table class="table align-items-center mb-0" id="assignment-feedback-table">
                        <thead>
                        <tr>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Feedback
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Regex
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Attempt
                            </th>
                            <th class="text-secondary opacity-7"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list content.feedback as feedback>
                            <tr>
                                <td>${feedback.feedback}</td>
                                <td>${feedback.regex}</td>
                                <td>${feedback.attempt}</td>
                                <td>${feedback.id}</td>
                                <td>${feedback.assignmentId}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

