
<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetCourseResponse" -->
<#import "_layout.ftl" as layout />
<style>
    #course-container {
        border-radius: 1rem;
        border: 0.125rem solid #DEE2E8;
        background: #FFF
    }

    #course-header {
        display: flex;
        padding: 1.5rem 1rem;
        align-items: flex-start;
        gap: 0.5rem;
    }

</style>
<@layout.header >
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

        <div class="container-fluid py-4">
            <div id="course-container" class="row m-3 pb-4">
                <div id="course-header" class="justify-content-between">
                    <h6 class="mt-3 mb-3">Your Assignments (3)</h6>
                    <button class="assignments btn btn-primary">+ create an assignment</button>
                </div>
                <div class="assignments">
                    <div>
                        <table class="new-table">
                            <thead>
                            <tr>
                                <th>TITLE</th>
                                <th>SUCCESS RATE</th>
                                <th>COMPLETION RATE</th>
                                <th>LAST MODIFIED</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list content.assignments as assignment>
                                <tr>
                                    <td>${assignment.title}</td>
                                    <td>${assignment.successRate}</td>
                                    <td>${assignment.completionRate}</td>
                                    <td>${assignment.lastModified}</td>
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
        </div>
    </main>
    </body>
</@layout.header>
