<#-- @ftlvariable name="breadcrumbs" type="org.empowrco.coppin.utils.routing.Breadcrumbs" -->
<#macro header>


    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="icon" type="image/png" href="/img/favicon.png">
        <title>
            Coppin
        </title>
        <!--     Fonts and icons     -->
        <link rel="stylesheet" type="text/css"
              href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,900|Roboto+Slab:400,700"/>
        <!-- Nucleo Icons -->
        <link href="/css/nucleo-icons.css" rel="stylesheet"/>
        <link href="/css/nucleo-svg.css" rel="stylesheet"/>
        <!-- Font Awesome Icons -->
        <script src="https://kit.fontawesome.com/42d5adcbca.js" crossorigin="anonymous"></script>
        <!-- Material Icons -->
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
        <!-- CSS Files -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css"
              crossorigin="anonymous">
        <!-- Font Awesome 5.x Icon library (check themes to change this) -->
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.9.0/css/all.css">
        <!-- Krajee Markdown Editor Main Library Default Style -->
        <link href="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/css/markdown-editor.css"
              media="all" rel="stylesheet" type="text/css"/>
        <!-- Highlight JS style provided with plugin for code styling -->
        <link href="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/highlight/highlight.min.css"
              media="all" rel="stylesheet" type="text/css"/>
        <!-- jQuery JS Library -->
        <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <!-- Include DOM purify plugin if you need to purify HTML output (needed only if markdown-it HTML input
           is allowed). This must be loaded before markdown-editor.js. -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/purify/purify.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Main Library -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Definition List Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-deflist.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Footnote Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-footnote.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Abbreviation Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-abbr.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Subscript Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-sub.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Superscript Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-sup.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Underline/Inserted Text Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-ins.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Mark Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-mark.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT SmartArrows Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-smartarrows.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Checkbox Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-checkbox.min.js"
                type="text/javascript"></script>
        <!-- Markdown IT Emoji Plugin -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/markdown-it/markdown-it-emoji.min.js"
                type="text/javascript"></script>
        <!-- Highlight JS Main Plugin Library for code styling -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/plugins/highlight/highlight.min.js"
                type="text/javascript"></script>
        <!-- Bootstrap Complete Bundle Library (including Popper) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js"
                type="text/javascript"></script>
        <!-- Krajee Markdown Editor Main Library -->
        <script src="https://cdn.jsdelivr.net/gh/kartik-v/krajee-markdown-editor@1.0.0/js/markdown-editor.js"
                type="text/javascript"></script>
        <link id="pagestyle" href="/css/material-dashboard.css?v=3.0.4" rel="stylesheet"/>
        <link href="/css/pds.min.css" rel="stylesheet"/>
        <link id="pagestyle" href="/css/coppin.css" rel="stylesheet"/>
    </head>

    <body class="g-sidenav-show  bg-gray-200 p-0">
    <#if hideSideNav??>
    <#else>
        <#if isAdminPanel??>
            <#include "admin-sidenav.ftl">
        <#else>
            <#include "sidenav.ftl">
        </#if>
    </#if>

    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg">
        <div class="to-header ms-2">
            <a href="javascript:;" class="nav-link text-body p-0" id="iconNavbarSidenav">
                <i class="material-icons" style="padding: 1rem">menu</i>
            </a>
        </div>
        <div class="container-fluid" id="body-content" style="padding-right: 0; padding-left: 0;">
            <#if breadcrumbs??>
                <div id="bcrumbs">
                    <#list breadcrumbs.crumbs as crumb>
                        <div class="crumb">
                            <#if crumb.icon??>
                                <div class="crumb-text me-1">
                                    <i class="material-icons crumb-icon">${crumb.icon}</i>
                                </div>
                            </#if>
                            <#if crumb.url??>
                                <a class="crumb-text" href="${crumb.url}"><#if crumb?is_last>
                                        <b>${crumb.name}</b><#else>${crumb.name}</#if></a>
                            <#else >
                                <p class="crumb-text"><#if crumb?is_last><b>${crumb.name}</b><#else>${crumb.name}</#if>
                                </p>
                            </#if>
                            <#if crumb?is_last == false>
                                <p class="crumb-text mx-2">/</p>
                            </#if>
                        </div>
                    </#list>
                </div>

            </#if>
            <#nested>
        </div>
    </main>

    </body>
    <!--   Core JS Files   -->

    <script src="/js/plugins/bs_dialog.js"></script>
    <script src="/js/plugins/perfect-scrollbar.min.js"></script>
    <script src="/js/plugins/smooth-scrollbar.min.js"></script>
    <script>
        const win = navigator.platform.indexOf('Win') > -1;
        if (win && document.querySelector('#sidenav-scrollbar')) {
            const options = {
                damping: '0.5'
            }
            Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
        }
    </script>
    <script>
        async function getTextFromStream(readableStream) {
            let reader = readableStream.getReader();
            let utf8Decoder = new TextDecoder();
            let nextChunk;

            let resultStr = '';
            while (!(nextChunk = await reader.read()).done) {
                let partialData = nextChunk.value;
                resultStr += utf8Decoder.decode(partialData);
            }
            return resultStr;
        }

        async function showError(error) {
            let message;
            if (error.error) {
                message = error.error
            } else if (error.message) {
                message = error.message
            } else {
                message = error.toString()
            }
            await new BsDialogs().ok('Error', message);
        }

        async function parseResponse(response) {
            const body = response.body
            const bodyString = await getTextFromStream(body)
            try {
                return JSON.parse(bodyString)
            } catch (e) {
                let errorMessage = 'Status: ' + response.status
                if (bodyString) {
                    errorMessage = errorMessage + ', Body: ' + bodyString
                }
                throw new Error(errorMessage)
            }
        }
    </script>
    <link href="https://cdn.datatables.net/v/bs5/dt-1.13.5/b-2.4.1/b-colvis-2.4.1/r-2.5.0/sc-2.2.0/sp-2.2.0/datatables.min.css"
          rel="stylesheet"/>

    <script src="https://cdn.datatables.net/v/bs5/dt-1.13.5/b-2.4.1/b-colvis-2.4.1/r-2.5.0/sc-2.2.0/sp-2.2.0/datatables.min.js"></script>
    <!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
    <script src="/js/material-dashboard.min.js?v=3.0.4"></script>
    <script type="text/javascript" src="/js/plugins/chartjs.min.js"></script>

</html>
    <script>
        $(document).ready(function () {
            const formElements = new Array();
            $("form :input").each(function () {
                const containsFormControl = this.classList.contains("form-control")
                if (containsFormControl) {
                    formElements.push(this)
                }
            });
            for (const element of formElements) {
                if (element.value != null && !element.value.isEmpty) {
                    const event = new Event('focusout');
                    element.dispatchEvent(event)
                }
            }

        });
    </script>
</#macro>
