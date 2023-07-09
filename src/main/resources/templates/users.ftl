
<#-- @ftlvariable name="content" type="org.empowrco.coppin.users.presenters.GetUsersResponse" -->
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
    <style>
        #users-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }
    </style>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">
        <div class="container-fluid py-4">
            <div id="users-container" class="row m-4 pb-4">
                <h6 class="mt-3 mb-3">All teachers (${content.usersCount})</h6>
                <#include "error.ftl">
                <div>
                    <table class="table-striped new-table table-striped">
                        <thead>
                        <tr>
                            <th>TEACHER NAME</th>
                            <th>STATUS</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list content.users as user>
                            <tr>
                                <td>${user.name}</td>
                                <td>${user.authorized?string("yes","no")}</td>
                            </tr>
                        </#list>
                        <tr>
                            <td colspan="2"></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>
