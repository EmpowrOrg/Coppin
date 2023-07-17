
<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetCourseResponse" -->
<#import "_layout.ftl" as layout />
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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

        #summary-container {
            display: flex;
            width: 100%;
            align-items: center;
            gap: 1.75rem;
        }

        #summary-completion-rate-container {
            aspect-ratio: 1/1;
            border-radius: 1rem;
            background: #F0F2F5;
            display: flex;
            align-items: center;
        }

        #summary-completion-rate {
            display: inline-flex;
            flex-direction: column;
            align-items: center;
        }

        #summary-grade-container {
            width: 100%;
        }
    </style>
    <script>
        $(document).ready(function () {
            const courseTable = $('#course-table').DataTable({
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
                        target: 5,
                        visible: false,
                    },
                ],
            });
            $('#course-table tbody').on('click', 'tr', function () {
                const data = courseTable.row(this).data();
                window.location = "/courses/${content.id}/assignments/" + data[5]
            });

            const subjectsTable = $('#subjects-table').DataTable({
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
                        target: 3,
                        visible: false,
                    },
                ],
            });
            $('#subjects-table tbody').on('click', 'tr', function () {
                const data = subjectsTable.row(this).data();
                window.location = "/courses/${content.id}/subjects/" + data[3]
            });

            function hideSection(sectionName) {
                const divsToHide = document.getElementsByClassName(sectionName);
                for (let i = 0; i < divsToHide.length; i++) {
                    divsToHide[i].style.display = "none";
                    divsToHide[i].style.visibility = "hidden";
                }
            }

            function showSection(sectionName) {
                const divsToHide = document.getElementsByClassName(sectionName);
                for (let i = 0; i < divsToHide.length; i++) {
                    divsToHide[i].style.display = "inline";
                    divsToHide[i].style.visibility = "visible";
                }
            }

            if ($('#sc-course').is(':checked')) {
                hideSection("assignments")
                showSection("course")
                hideSection("subjects")
            } else if ($('#sc-subjects').is(':checked')) {
                hideSection("assignments")
                showSection("subjects")
                hideSection("course")
            } else {
                hideSection("course")
                showSection("assignments")
                hideSection("subjects")
            }
            $('input[type=radio][name=sc]').change(function () {
                if (this.value === 'sc-course') {
                    hideSection("assignments")
                    showSection("course")
                    hideSection("subjects")
                } else if (this.value === 'sc-assignments') {
                    hideSection("course")
                    showSection("assignments")
                    hideSection("subjects")
                } else if (this.value === 'sc-subjects') {
                    hideSection("assignments")
                    showSection("subjects")
                    hideSection("course")
                }
            });

            const data = {
                labels: [<#list content.chart.x.labels as label>'${label}', </#list>],
                datasets: [
                    <#list content.chart.x.lines as line>
                    {
                        label: '${line.name}',
                        data: [<#list line.points as point>${point}, </#list>],
                        borderColor: '${line.color}',
                        backgroundColor: '${line.color}',
                        fill: false,
                        tension: 0.4
                    },
                    </#list >
                ]
            }
            const config = {
                type: 'line',
                data: data,
                options: {
                    responsive: true,
                    plugins: {
                        title: {
                            display: true,
                            text: '${content.chart.title}'
                        },
                    },
                    interaction: {
                        intersect: false,
                    },
                    scales: {
                        x: {
                            display: true,
                            title: {
                                display: true
                            }
                        },
                        y: {
                            display: true,
                            title: {
                                display: true,
                                text: '${content.chart.y.label}'
                            },
                            suggestedMin: ${content.chart.y.min},
                            suggestedMax: ${content.chart.y.max},
                        }
                    }
                },
            }
            const ctx = document.getElementById('course-summary-chart');

            new Chart(ctx, config);
        });
    </script>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

        <div class="container-fluid py-4">
            <div id="course-container" class="row m-3 pb-4">
                <div id="course-header" class="justify-content-between">
                    <div class="pds-segmentedControl pds-segmentedControl-triple">
                        <input id="sc-course" name="sc" type="radio" data-gtm="filter" checked
                               data-gtm-label="second" value="sc-course"/>
                        <label for="sc-course"
                               style="white-space: nowrap">Course</label>
                        <input id="sc-assignments" name="sc" type="radio" data-gtm="filter"
                               data-gtm-label="second" value="sc-assignments"/>
                        <label for="sc-assignments"
                               style="white-space: nowrap">Assignments</label>
                        <input id="sc-subjects" name="sc" type="radio"
                               data-gtm="filter" data-gtm-label="first" value="sc-subjects"/>
                        <label for="sc-subjects">Subjects</label>
                    </div>
                    <a href="/courses/${content.id}/assignments/" class="assignments btn btn-primary">+ create an
                        assignment</a>
                </div>
                <#include "error.ftl">
                <div class="course">
                    <div id="summary-container" class="mt-3">
                        <div id="summary-completion-rate-container" class="justify-content-center">
                            <div id="summary-completion-rate" class="align-middle">
                                <h1 class="text-center" style="color: #2B5198;">${content.completionRate}%</h1>
                                <h4 class="text-center ms-4 me-4" style="color: #2B5198;">Completion Rate</h4>
                            </div>
                        </div>
                        <div id="summary-grade-container">
                            <canvas id="course-summary-chart"></canvas>
                        </div>
                    </div>
                </div>
                <div class="assignments">
                    <div class="table-responsive coppin-table">
                        <table id="course-table" class="stripe hover row-border order-column" style="width: 100%">
                            <thead>
                            <tr>
                                <th>TITLE</th>
                                <th>SUBJECT</th>
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
                                    <td>${assignment.subject}</td>
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
                <div class="subjects">
                    <div class="table-responsive coppin-table">
                        <table id="subjects-table" class="stripe hover row-border order-column" style="width: 100%">
                            <thead>
                            <tr>
                                <th>NAME</th>
                                <th>NO. OF ASSIGNMENTS</th>
                                <th>LAST MODIFIED</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list content.subjects as subject>
                                <tr>
                                    <td>${subject.name}</td>
                                    <td>${subject.assignments}</td>
                                    <td>${subject.lastModified}</td>
                                    <td>${subject.id}</td>
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
