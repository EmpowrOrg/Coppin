
<#-- @ftlvariable name="content" type="org.empowrco.coppin.languages.presenters.GetLanguagesResponse" -->
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
                const data = table.row(this).data();
                window.location = "/languages/" + data[2]
            });
        });
    </script>
<style>
    #languages-container {
        border-radius: 1rem;
        border: 0.125rem solid #DEE2E8;
        background: #FFF
    }

    #languages-header {
        display: flex;
        padding: 12px 24px;
        align-items: flex-start;
        gap: 8px;
    }

    .language-item {
        display: flex;
        padding: 1rem;
        flex-direction: column;
        align-items: flex-start;
        align-self: stretch;
        border-radius: 16px;
        background: #F8F9FA;
    }

    .language-detail {
        font-size: 14px !important;
        font-weight: 400 !important;
        line-height: 1.25rem !important;
        margin-bottom: 0.25rem !important;
    }
</style>
<div id="languages-container" class="row m-4 pb-4">
    <div id="languages-header" class="m-2 justify-content-between">
        <h6>All languages (${content.languagesCount})</h6>
        <a href="/languages/" class="btn btn-primary">+ ADD A LANGUAGE</a>
    </div>
    <div>
        <#list content.languages as language>
            <div class="language-item mt-2">
                <h6>Name: ${language.name}</h6>
                <p class="language-detail">
                    <b>Lasted edited:</b> ${language.lastModifiedDate}
                </p>
                <p class="language-detail">
                    <b>Mirror url:</b> ${language.url}
                </p>
                <p class="language-detail">
                    <b>Mime:</b> ${language.mime}
                </p>
            </div>
        </#list>
    </div>
    <div class="fixed-plugin">
        <a href="/languages/" class="fixed-plugin-button text-dark position-fixed px-3 py-2">
            <i class="material-icons py-2">add</i>
        </a>
    </div>
    </@layout.header>
