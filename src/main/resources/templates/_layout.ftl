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
        <link id="pagestyle" href="/css/material-dashboard.css?v=3.0.4" rel="stylesheet"/>
        <link href="/css/pds.min.css" rel="stylesheet"/>
        <link id="pagestyle" href="/css/coppin.css" rel="stylesheet"/>
        <script src="https://code.jquery.com/jquery-3.6.1.min.js"
                integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    </head>

    <body class="g-sidenav-show  bg-gray-200 p-0">
    <#if hideSideNav??>
    <#else>
        <#include "sidenav.ftl">
    </#if>

    <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg">
        <div class="container-fluid" id="body-content" style="padding-right: 0; padding-left: 0;">
            <#nested>
        </div>
    </main>

    </body>
    <!--   Core JS Files   -->

    <script src="/js/core/popper.min.js"></script>
    <script src="/js/core/bootstrap.min.js"></script>
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
