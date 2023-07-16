<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetAssignmentPortalResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <style>
        #assignment-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #assignment-header {
            display: flex;
            padding: 1.5rem 1rem 0 1rem;
            align-items: flex-start;
            gap: 0.5rem;
        }

        #markdown-row {
            display: flex;
            align-items: flex-start;
            width: 100%;
            gap: 1rem;
            min-height: 600px;
        }

        #success-row {
            display: flex;
            align-items: flex-start;
            width: 100%;
            gap: 1rem;
            min-height: 300px;
        }

        #failure-row {
            display: flex;
            align-items: flex-start;
            width: 100%;
            gap: 1rem;
            min-height: 300px;
        }

        .CodeMirror {
            height: 100% !important;
            max-height: 600px;
        }

        #markdown-preview {
            height: 100% !important;
            max-height: 600px;
        }

        #success-preview {
            height: 100% !important;
            max-height: 600px;
        }

        #failure-preview {
            height: 100% !important;
            max-height: 600px;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.js"
            integrity="sha512-8RnEqURPUc5aqFEN04aQEiPlSAdE0jlFS/9iGgUyNtwFnSKCXhmB6ZTNl7LnDtDWKabJIASzXrzD0K+LYexU9g=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.css"
          integrity="sha512-uf06llspW44/LZpHzHT6qBOIVODjWtv4MxCricRxkzvopAlSWnTf6hpZTFxuuZcuNE9CBQhqE0Seu1CoRk84nQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/mode/markdown/markdown.js"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/showdown/2.1.0/showdown.min.js"
            integrity="sha512-LhccdVNGe2QMEfI3x4DVV3ckMRe36TfydKss6mJpdHjNFiV07dFpS2xzeZedptKZrwxfICJpez09iNioiSZ3hA=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script>
        const showdownOptions = {
            tables: true,
            emoji: true,
            tasklists: true,
            strikethrough: true,
            parseImgDimensions: true,
            openLinksInNewWindow: true
        }
        const converter = new showdown.Converter(showdownOptions);

        function setPreview(id, markdown) {
            let html = converter.makeHtml(markdown);
            const regex = /<table>/g;
            html = html.replace(regex, '<table class="table table-striped table-sm">');
            document.getElementById(id).innerHTML = `<div class="class table-responsive" style="width: auto; max-height: 600px">` + html + `</div>`

        }

        $(document).ready(function () {


            const codeMirrorConfig = {
                lineNumbers: true,
                mode: "text/x-markdown",
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
            }
            const instructionsTextArea = document.getElementById("instructions-editor");
            const instructionsConfig = codeMirrorConfig
            <#if content.instructions??>
            instructionsConfig.value = "${content.instructions}"
            setPreview("markdown-preview", "${content.instructions}")
            </#if>
            const instructionsCodeMirror = CodeMirror(function (elt) {
                instructionsTextArea.parentNode.replaceChild(elt, instructionsTextArea);
            }, instructionsConfig);

            instructionsCodeMirror.on('change', editor => {
                setPreview("markdown-preview", editor.getValue())
            })
            instructionsCodeMirror.setSize('100%');

            const successTextArea = document.getElementById("success-editor");
            const successConfig = codeMirrorConfig
            <#if content.successMessage??>
            successConfig.value = "${content.successMessage}"
            setPreview("success-preview", "${content.successMessage}")
            </#if>
            const successCodeMirror = CodeMirror(function (elt) {
                successTextArea.parentNode.replaceChild(elt, successTextArea);
            }, successConfig);
            successCodeMirror.on('change', editor => {
                setPreview("success-preview", editor.getValue())
            })
            successCodeMirror.setSize('100%');

            const failureTextArea = document.getElementById("failure-editor");
            const failureConfig = codeMirrorConfig
            <#if content.failureMessage??>
            failureConfig.value = "${content.failureMessage}"
            setPreview("failure-preview", "${content.failureMessage}")
            </#if>
            const failureCodeMirror = CodeMirror(function (elt) {
                failureTextArea.parentNode.replaceChild(elt, failureTextArea);
            }, failureConfig);
            failureCodeMirror.on('change', editor => {
                setPreview("failure-preview", editor.getValue())
            })
            failureCodeMirror.setSize('100%');

            $("#edit-assignment").submit(function (eventObj) {
                eventObj.preventDefault()
                $("<input />")
                    .attr("name", "instructions")
                    .attr("type", "hidden")
                    .val(instructionsCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "success-message")
                    .attr("type", "hidden")
                    .val(successCodeMirror.getValue())
                    .appendTo(this);
                $("<input />")
                    .attr("name", "failure-message")
                    .attr("type", "hidden")
                    .val(failureCodeMirror.getValue())
                    .appendTo(this);
                this.submit()
            });

            function hideSection(sectionName) {
                const divsToHide = document.getElementsByClassName(sectionName); //divsToHide is an array
                for (let i = 0; i < divsToHide.length; i++) {
                    divsToHide[i].style.display = "none"; // depending on what you're doing
                    divsToHide[i].style.visibility = "hidden"; // or
                }
            }

            function showSection(sectionName) {
                const divsToHide = document.getElementsByClassName(sectionName);
                for (let i = 0; i < divsToHide.length; i++) {
                    if (divsToHide[i].id === "markdown-row" || divsToHide[i].id === "success-row" || divsToHide[i].id === "failure-row") {
                        divsToHide[i].style.display = "flex";
                    } else {
                        divsToHide[i].style.display = "inline";
                    }

                    divsToHide[i].style.visibility = "visible";
                }
            }

            if ($('#sc-codes').is(':checked')) {
                hideSection("instructions")
                showSection("codes")
            } else {
                hideSection("codes")
                showSection("instructions")
            }
            $('input[type=radio][name=sc]').change(function () {
                if (this.value === 'sc-codes') {
                    hideSection("instructions")
                    showSection("codes")
                } else if (this.value === 'sc-instructions') {
                    hideSection("codes")
                    showSection("instructions")
                }
            });
            <#if content.id??>
            $('#archive-confirm').on('click', async function () {
                $('#archiveModal').modal('hide')
                fetch('/courses/${content.courseId}/assignments/${content.id}', {
                    method: "DELETE",
                    headers: {'Content-Type': 'application/json'},
                }).then(async response => {
                    return await parseResponse(response)
                }).then(async res => {
                    if (res.error) {
                        throw new Error(res.error)
                    } else {
                        window.location.replace("/courses/${content.courseId}")
                    }
                }).catch(async error => {
                    await showError(error)
                });
            });
            const table = $('#codes-table').DataTable({
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
            $('#codes-table tbody').on('click', 'tr', function () {
                const data = table.row(this).data();
                window.location = "/courses/${content.courseId}/assignments/${content.id}/codes/" + data[4]
            });
            </#if>
        });
    </script>
    <div class="container-fluid py-4">
        <form role="form" id="edit-assignment" method="post"
              action="/courses/${content.courseId}/assignments/<#if content.id??>${content.id}<#else >create</#if>">
            <div id="assignment-container" class="row m-3 pb-4">
                <div id="assignment-header" class="justify-content-between">
                    <div style='max-width: 400px;'>
                        <#if content.id??>
                            <div class="pds-segmentedControl">
                                <input id="sc-instructions" name="sc" type="radio" checked
                                       data-gtm="filter" data-gtm-label="first" value="sc-instructions"/>
                                <label for="sc-instructions">Instructions</label>
                                <input id="sc-codes" name="sc" type="radio" data-gtm="filter"
                                       data-gtm-label="second" value="sc-codes"/>
                                <label for="sc-codes" style="padding-right: 0.5rem">Codes</label>
                            </div>
                        </#if>


                    </div>
                    <#if content.id??>
                        <a href="/courses/${content.courseId}/assignments/${content.id}/codes"
                           class="codes btn btn-primary">+ add code</a>
                    </#if>

                    <div class="instructions">
                        <button type="submit" class="instructions btn btn-primary">Save</button>
                        <#if content.id??>
                            <button type="button" class="ms-4 instructions btn btn-danger"
                                    data-bs-toggle="modal" data-bs-target="#archiveModal">Archive
                            </button>
                        </#if>

                    </div>

                </div>
                <#include "error.ftl">
                <div class="instructions">
                    <div id="instructions-details-container">
                        <div class="row">
                            <div class="col mb-3">
                                <label for="title" class="form-label"><h6>Title</h6></label>
                                <input name="title" id="title" <#if content.title??>value="${content.title}" </#if>
                                       type="text" class="form-control">
                            </div>
                            <div class="row align-items-center">
                                <div class="col-auto row align-items-center mt-2">
                                    <div class="col-auto">
                                        <label for="reference-id" class="col-form-label"><h6>Reference Id</h6></label>
                                    </div>
                                    <div class="col-auto">
                                        <input name="reference-id" type="text" id="reference-id" class="form-control"
                                               <#if content.referenceId??>value="${content.referenceId}" </#if>>
                                    </div>
                                </div>
                                <div class="col-auto row align-items-center mt-2">
                                    <div class="col-auto">
                                        <label for="total-attempts" class="col-form-label"><h6>Total Attempts</h6>
                                        </label>
                                    </div>
                                    <div class="col-auto">
                                        <input type="number" id="total-attempts" class="form-control" min="0"
                                               step="1"
                                               name="total-attempts"
                                               onfocus="this.previousValue = this.value"
                                               onkeydown="this.previousValue = this.value"
                                               <#if content.attempts??>value="${content.attempts}" </#if>
                                               oninput="validity.valid || (value = this.previousValue)">
                                    </div>
                                </div>

                                <div class="col-auto row align-items-center mt-2">
                                    <div class="col-auto align-middle">
                                        <label for="subject" class="col-form-label"><h6>Subject</h6></label>
                                    </div>
                                    <div class="col-auto align-middle">
                                        <select name="subject" id="subject" class="form-select">
                                            <#list content.subjects as subject>
                                                <option value="${subject.id}"
                                                        <#if subject.id == content.subjectId>selected</#if>>${subject.name}</option>
                                            </#list>
                                        </select>

                                    </div>
                                    <div class="col-auto ps-0 align-middle">
                                        <button type="button" id="add-subject" class="btn col-sm text-black-50 p-2 m-0"
                                                onclick="addSubject()"><i class="material-icons opacity-10">add</i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <h6 class="mt-4">Instructions</h6>
                <div id="markdown-row" class="instructions">

                    <div class="col-6 mb-3" style="height: 100%">
                        <textarea id="instructions-editor"
                                  name="instructions"
                                  rows="5">
                        </textarea>

                    </div>
                    <div class="col-6 " id="markdown-preview"></div>
                </div>
                <h6 class="mt-4 instructions">Success Message</h6>
                <div id="success-row" class="instructions">

                    <div class="col-6 mb-3" style="height: 100%">
                        <textarea id="success-editor"
                                  name="success"
                                  rows="5">
                        </textarea>

                    </div>
                    <div class="col-6 " id="success-preview"></div>
                </div>

                <h6 class="mt-4 instructions">Failure Message</h6>
                <div id="failure-row" class="instructions">

                    <div class="col-6 mb-3" style="height: 100%">
                        <textarea id="failure-editor"
                                  name="failure"
                                  rows="5">
                        </textarea>

                    </div>
                    <div class="col-6 " id="failure-preview"></div>
                </div>
                <div class="codes">
                    <div class="table-responsive coppin-table">
                        <table id="codes-table" class="stripe hover row-border order-column" style="width: 100%">
                            <thead>
                            <tr>
                                <th>LANGUAGE</th>
                                <th>PRIMARY</th>
                                <th>STARTER CODE</th>
                                <th>SOLUTION CODE</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list content.codes as code>
                                <tr>
                                    <td>${code.language}</td>
                                    <td>${code.primary}</td>
                                    <td>${code.hasStarter}</td>
                                    <td>${code.hasSolution}</td>
                                    <td>${code.id}</td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </form>
        <div class="modal fade" id="archiveModal" tabindex="-1"
             aria-labelledby="archiveModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="archiveModalLabel">Archive Assignment</h5>
                    </div>
                    <div class="modal-body">
                        By clicking archive, you will hide this assignment from view. This action cannot currently be
                        undone.
                        Archived assignments will cease to work with Open Edx.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary"
                                data-bs-dismiss="modal">Close
                        </button>
                        <button type="submit" class="btn btn-primary"
                                id="archive-confirm"
                        >
                            Archive
                        </button>

                    </div>
                </div>
            </div>
        </div>

    </div>
    <script>
        async function addSubject() {
            const frm = `<form>
                         <div>Create a new subject. Note: This will reload the page.
                         </div>
                         <div class="input-group input-group-outline mt-3">
                             <input data-name="name" name="name" id="name" type="text"
                                    class="form-control" placeholder="ex: Functions" required>
                         </div>
</form>`
            let dlg = new BsDialogs()
            dlg.form('Create Subject', 'Create', frm)
            let result = await dlg.onsubmit()
            if (result === undefined) {
                return
            }
            const name = result.name
            console.log(name)
            const body = JSON.stringify({
                name: name,
                courseId: "${content.courseId}",
            })
            console.log(body)
            fetch('/courses/${content.courseId}/subjects', {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: body
            }).then(async response => {
                return await parseResponse(response)
            }).then(async res => {
                if (res.error) {
                    throw new Error(res.error)
                } else {
                    await new BsDialogs().ok('Subject Created', 'Subject \'' + name + '\' created');
                    window.location.reload()
                }
            }).catch(async error => {
                await showError(error)
            });
        }
    </script>
</@layout.header>
