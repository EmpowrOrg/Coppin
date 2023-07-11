
<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetCourseResponse" -->
<#import "_layout.ftl" as layout />

<@layout.header >
    <style>
        #course-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #course-header {
            display: flex;
            padding: 1.5rem 1rem 0 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }

    </style>
    <script>
        $(document).ready(function () {
            const table = $('#course-table').DataTable({
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
            $('#course-table tbody').on('click', 'tr', function () {
                const data = table.row(this).data();
                window.location = "/courses/${content.id}/assignments/" + data[4]
            });
        });
    </script>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

        <div class="container-fluid py-4">
            <div id="course-container" class="row m-3 pb-4">
                <div id="course-header" class="justify-content-between">
                    <h6 class="mt-3 mb-3">Your Assignments (${content.assignments?size})</h6>
                    <a href="/courses/${content.id}/assignments/" class="assignments btn btn-primary">+ create an
                        assignment</a>
                </div>
                <#include "error.ftl">
                <div class="assignments">
                    <div class="table-responsive coppin-table">
                        <table id="course-table" class="stripe hover row-border order-column" style="width: 100%">
                            <thead>
                            <tr>
                                <th>TITLE</th>
                                <th>SUCCESS RATE</th>
                                <th>COMPLETION RATE</th>
                                <th>LAST MODIFIED</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list content.assignments as assignment>
                                <tr>
                                    <td>${assignment.title}</td>
                                    <td>${assignment.successRate}</td>
                                    <td>${assignment.completionRate}</td>
                                    <td>${assignment.lastModified}</td>
                                    <td>${assignment.id}</td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>
