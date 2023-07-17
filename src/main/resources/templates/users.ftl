
<#-- @ftlvariable name="content" type="org.empowrco.coppin.users.presenters.GetUsersResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script>
        $(document).ready(function () {
            const table = $('#users-table').DataTable({
                language: {
                    search: "",
                    searchPlaceholder: "Search...",
                    paginate: {
                        next: `<i class="material-icons opacity-10">arrow_forward_ios</i>`,
                        previous: `<i class="material-icons opacity-10">arrow_back_ios</i>`,
                    }
                },
                responsive: true,
                columnDefs: [
                    {
                        target: 2,
                        visible: false,
                    },
                ],
            });
            $('#users-table tbody').on('click', 'tr', function () {
                const data = table.row(this).data();
                window.location = "/user/" + data[2]
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
        <div class="container-fluid">
            <div id="users-container" class="row m-4 pb-4">
                <h6 class="mt-3 mb-3">All teachers (${content.usersCount})</h6>
                <#include "error.ftl">
                <div class="table-responsive coppin-table">
                    <table id="users-table" class="stripe hover row-border order-column" style="width: 100%">
                        <thead>
                        <tr>
                            <th>TEACHER NAME</th>
                            <th>STATUS</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list content.users as user>
                            <tr>
                                <td>${user.name}</td>
                                <td>${user.authorized?string("yes","no")}</td>
                                <td>${user.id}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>
