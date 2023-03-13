
<#-- @ftlvariable name="content" type="tech.devezin.users.presenters.GetUsersResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script>
        $(document).ready(function () {
            const table = $('#users-table').DataTable({
                language: {search: ""},
                columnDefs: [
                    {
                        target: 4,
                        visible: false,
                    },
                ],
            });
            $('#users-table_filter').addClass('me-4').find("input").addClass('form-control').attr("placeholder", "Search");
            $('#users-table_length').addClass('form-group').addClass('ms-4').addClass('form-inline');
            $('#users-table_info').addClass('ms-4').addClass('text-sm')
            $('#users-table_paginate').addClass('me-4')
            $('#users-table tbody').on('click', 'tr', function () {
                const data = table.row(this).data();
                window.location = "/user/" + data[4]
            });
        });
    </script>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">
        <div class="container-fluid py-4">

            <div class="row mt-4">
                <div class="row mb-4">
                    <div class="mb-md-0 mb-4">
                        <div class="card">
                            <div class="card-header pb-0">
                                <div class="row">
                                    <div class="d-flex justify-content-start">
                                        <h6>Users</h6>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body px-0 pb-2">
                                <#include "error.ftl">
                                <div class="table-responsive">
                                    <table id="users-table" class="table align-items-center mb-0">
                                        <thead>
                                        <tr>
                                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Name
                                            </th>
                                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Email
                                            </th>
                                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Authorized
                                            </th>
                                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Type
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <#list content.users as user>
                                            <tr>
                                                <td class="text-sm">
                                                    <span class="text-xs font-weight-bold">${user.name}</span>
                                                </td>
                                                <td class="text-sm">
                                                    <span class="text-xs font-weight-bold">${user.email}</span>
                                                </td>
                                                <td class="text-sm">
                                                    <span class="text-xs font-weight-bold">${user.authorized?string("yes","no")}</span>
                                                </td>
                                                <td class="text-sm">
                                                    <span class="text-xs font-weight-bold">${user.type}</span>
                                                </td>
                                                <td class="align-middle">${user.id}</td>
                                            </tr>
                                        </#list>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>
