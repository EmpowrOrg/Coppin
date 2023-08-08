
<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetCoursesResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script>
        $(document).ready(function () {
            const table = $('#courses-table').DataTable({
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
                        target: 4,
                        visible: false,
                    },
                ],
            });
            $('#courses-table tbody').on('click', 'tr', function () {
                const data = table.row(this).data();
                window.location = "/courses/" + data[4]
            });
        });
    </script>
    <style>
        #courses-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #courses-header {
            display: flex;
            padding: 1.5rem 1rem 0 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }

    </style>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">
        <div class="container-fluid">
            <div id="courses-container" class="row m-4 pb-4">
                <div id="courses-header" class="justify-content-between">
                    <h6 class="mt-3 mb-3">Your courses (${content.coursesCount})</h6>
                    <a href="/courses/link" class="btn btn-primary">+ Manage Courses</a>
                </div>
                <#include "error.ftl">
                <div class="table-responsive coppin-table">
                    <table id="courses-table" class="stripe hover row-border order-column" style="width: 100%">
                        <thead>
                        <tr>
                            <th>COURSE NAME</th>
                            <th>NUMBER</th>
                            <th>START DATE</th>
                            <th>END DATE</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list content.courses as course>
                            <tr>
                                <td>${course.name}</td>
                                <td>${course.number}</td>
                                <td>${course.startDate}</td>
                                <td>${course.endDate}</td>
                                <td style="display: none;">${course.id}</td>
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
