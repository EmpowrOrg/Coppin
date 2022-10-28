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
<#-- @ftlvariable name="codes" type="kotlin.collections.List<org.empowrco.coppin.models.portal.CodeListItem>" -->
<#-- @ftlvariable name="assignment" type="org.empowrco.coppin.models.portal.AssignmentItem" -->
<!-- Data Tables -->
<link rel="stylesheet" type="text/css"
      href="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.css"/>

<script type="text/javascript"
        src="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.js"></script>
<script>
    $(document).ready(function () {
        const table = $('#assignment-codes-table').DataTable({
            language: {search: ""},
            columnDefs: [
                {
                    target: 5,
                    visible: false,
                },
                {
                    target: 6,
                    visible: false,
                }
            ],
        });
        $('#assignment-codes-table_filter').find("input").addClass('form-control').attr("placeholder", "Search");
        $('#assignment-codes-table tbody').on('click', 'tr', function () {
            var data = table.row(this).data();
            window.location = "/assignments/" + data[6] + "/codes/" + data[5]
        });
    });
</script>
<div class="row">
    <div class="col-12">
        <div class="card my-4">
            <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                    <h6 class="text-white text-capitalize ps-3" style="display:inline-block;">Assignment Codes</h6>
                    <a href="/assignments/${assignment.id}/codes/" class="btn btn-lg bg-gradient-dark btn-lg mb-0">Create</a>
                </div>
            </div>
            <div class="card-body px-0 pb-2">
                <div class="table-responsive p-2">
                    <table class="table mb-0" id="assignment-codes-table">
                        <thead>
                        <tr>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Language
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Is Primary
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Has Starter Code
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Has Solution Code
                            </th>
                            <th class="text-secondary opacity-7"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list codes as code>
                            <tr>
                                <td>${code.language}</td>
                                <td>${code.primary}</td>
                                <td>${code.hasStarter}</td>
                                <td>${code.hasSolution}</td>
                                <td>
                                    <a href="javascript:;" class="text-secondary font-weight-bold text-xs"
                                       data-toggle="tooltip" data-original-title="Edit user">
                                        Edit
                                    </a>
                                </td>
                                <td >${code.id}</td>
                                <td>${code.assignmentId}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

