<#-- @ftlvariable name="content" type="org.empowrco.coppin.courses.presenters.GetUnlinkedCoursesResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
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

        .link-item {
            background: #F8F9FA;
            padding: 1.5rem;
            border-radius: 1rem;
        }

        .link-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            align-self: stretch;
        }

        .link-detail-row {
            display: flex;
            align-items: flex-start;
            gap: 2rem;
            margin-top: 1rem;
        }

        .link-key-text {
            color: #7B809A;
            font-size: 12px;
            font-style: normal;
            font-weight: 400;
            line-height: 140%;
        }

        .link-value-text {
            color: #344767;
            font-size: 12px;
            font-style: normal;
            font-weight: 700;
            line-height: 140%;
        }

        #save-row {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 16px;
            align-self: stretch;
        }
    </style>
    <body class="g-sidenav-show  bg-gray-200">
    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ">

        <div class="container-fluid">
            <div id="courses-container" class="row m-4 pb-4">
                <h4 class="mt-4 ms-4">Available courses (${content.count})</h4>

                <div>
                    <form method="post" action="/courses/link">
                        <fieldset>
                            <#list content.rows as row>
                                <div class="row p-3">
                                    <div class="col link-item m-2">
                                        <div class="link-header">
                                            <h6 class="m-0">${row.one.name}</h6>
                                            <input name="class" class="align-middle" type="checkbox"
                                                   value="${row.one.id}">
                                        </div>
                                        <div class="link-detail-row">
                                            <p class="link-key-text">Number:</p>
                                            <p class="link-value-text">${row.one.number}</p>
                                        </div>
                                        <div class="link-detail-row">
                                            <p class="link-key-text">Org:</p>
                                            <p class="link-value-text">${row.one.org}</p>
                                        </div>
                                        <div class="link-detail-row">
                                            <p class="link-key-text">Id:</p>
                                            <p class="link-value-text">${row.one.id}</p>
                                        </div>
                                        <div class="link-detail-row">
                                            <p class="link-key-text">Duration:</p>
                                            <p class="link-value-text">${row.one.dates}</p>
                                        </div>
                                    </div>
                                    <#if row.two??>
                                        <div class="col link-item m-2">
                                            <div class="link-header">
                                                <h6 class="m-0">${row.two.name}</h6>
                                                <input name="class" class="align-middle" type="checkbox"
                                                       value="${row.two.id}">
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Number:</p>
                                                <p class="link-value-text">${row.two.number}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Org:</p>
                                                <p class="link-value-text">${row.two.org}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Id:</p>
                                                <p class="link-value-text">${row.two.id}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Duration:</p>
                                                <p class="link-value-text">${row.two.dates}</p>
                                            </div>
                                        </div>
                                    <#else >
                                        <div class="col m-2"></div>
                                    </#if>
                                    <#if row.three??>
                                        <div class="col link-item m-2">
                                            <div class="link-header">
                                                <h6 class="m-0">${row.three.name}</h6>
                                                <input name="class" class="align-middle" type="checkbox"
                                                       value="${row.three.id}">
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Number:</p>
                                                <p class="link-value-text">${row.three.number}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Org:</p>
                                                <p class="link-value-text">${row.three.org}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Id:</p>
                                                <p class="link-value-text">${row.three.id}</p>
                                            </div>
                                            <div class="link-detail-row">
                                                <p class="link-key-text">Duration:</p>
                                                <p class="link-value-text">${row.three.dates}</p>
                                            </div>
                                        </div>
                                    <#else >
                                        <div class="col m-2"></div>
                                    </#if>
                                </div>
                            </#list>
                        </fieldset>
                        <div id="save-row">
                            <button type="submit" class="btn btn-outline-primary">Save</button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </main>
    </body>
</@layout.header>
