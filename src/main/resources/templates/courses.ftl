
<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetCoursesResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script>
        function addRowHandlers() {
            const table = document.getElementById("courses-table").getElementsByTagName("tbody")[0];
            const rows = table.getElementsByTagName("tr");
            for (let i = 0; i < rows.length; i++) {
                const currentRow = rows[i];
                const createClickHandler =
                    function (row) {
                        return function () {
                            const elements = row.getElementsByTagName("td")
                            if (elements.length !== 5) {
                                return
                            }
                            const cell = row.getElementsByTagName("td")[4];
                            const id = cell.innerHTML;
                            window.location = '/courses/' + id
                        };
                    };

                currentRow.onclick = createClickHandler(currentRow);
            }
        }

        $(document).ready(function () {
            addRowHandlers()
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
            padding: 1.5rem 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }
    </style>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

        <div class="container-fluid py-4">
            <div id="courses-container" class="row m-4 pb-4">
                <div id="courses-header" class="justify-content-between">
                    <h6 class="mt-3 mb-3">Your courses (${content.coursesCount})</h6>
                    <button class="btn btn-primary">+ Link Course</button>
                </div>
                <#include "error.ftl">
                <div class="table-responsive">
                    <table id="courses-table" class="new-table">
                        <thead>
                        <tr>
                            <th>COURSE NAME</th>
                            <th>NUMBER</th>
                            <th>START DATE</th>
                            <th>END DATE</th>
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

                        <tr>
                            <td colspan="4"></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>