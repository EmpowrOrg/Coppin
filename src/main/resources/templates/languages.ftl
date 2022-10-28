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
<#-- @ftlvariable name="languages" type="kotlin.collections.List<org.empowrco.coppin.models.portal.LanguageListItem>" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <!-- Data Tables -->
    <link rel="stylesheet" type="text/css"
          href="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.css"/>

    <script type="text/javascript"
            src="https://cdn.datatables.net/v/dt/dt-1.12.1/kt-2.7.0/r-2.3.0/datatables.min.js"></script>
    <script>
        $(document).ready(function () {
            const table = $('#languages-table').DataTable({
                language: {search: ""},
                columnDefs: [
                    {
                        target: 2,
                        visible: false,
                    },
                ],
            });
            $('#languages-table_filter').find("input").addClass('form-control').attr("placeholder", "Search");
            $('#languages-table tbody').on('click', 'tr', function () {
                var data = table.row(this).data();
                window.location = "/languages/" + data[2]
            });
        });
    </script>
    <style>
        .dataTables_info {
            padding-left: 16px;
            padding-right: 16px;
        }

        .dataTables_filter {
            padding-left: 16px;
            padding-right: 16px;
        }

        .dataTables_wrapper .dataTables_filter input {
            margin-left: 8px;
        }

        .dataTables_length {
            padding-left: 16px;
            padding-right: 16px;
        }
    </style>
    <div class="row">
        <div class="col-12">
            <div class="card my-4">
                <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3">Languages</h6>
                        <a href="/languages/create" class="btn btn-lg bg-gradient-dark btn-lg mb-0">Create</a>
                    </div>
                </div>
                <div class="card-body px-0 pb-2">
                    <div class="table-responsive p-0">
                        <table class="table align-items-center mb-0" id="languages-table">
                            <thead>
                            <tr>
                                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                    Name
                                </th>
                                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                    Mime
                                </th>
                                <th class="text-secondary opacity-7"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list languages as language>
                                <tr>
                                    <td>
                                        <div class="d-flex px-2 py-1">
                                            <div class="d-flex flex-column">
                                                <p class="mb-0 text-sm font-weight-bold">${language.name}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <p class="text-md mb-0">${language.mime}</p>
                                    </td>
                                    <td >${language.id}</td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@layout.header>
