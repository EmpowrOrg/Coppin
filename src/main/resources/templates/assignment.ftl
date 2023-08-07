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
    <script>
        $(document).ready(function () {
            const instructionsText = $("#instructions-editor")
            instructionsText.markdownEditor({
                defaultMode: 'split',
                toolbarFooterL: [
                    ['hint'],
                ]
            });
            const successText = $("#success-editor")
            successText.markdownEditor({
                defaultMode: 'split',
                toolbarFooterL: [
                    [],
                ]
            });
            const failureText = $("#failure-editor")
            failureText.markdownEditor({
                defaultMode: 'split',
                toolbarFooterL: [
                    [],
                ]
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
                hideSection("submissions")
                showSection("codes")
            } else if ($("#sc-submissions").is(':checked')) {
                hideSection("instructions")
                hideSection("codes")
                showSection("submissions")
            } else {
                hideSection("codes")
                hideSection("submissions")
                showSection("instructions")
            }
            $('input[type=radio][name=sc]').change(function () {
                if (this.value === 'sc-codes') {
                    hideSection("instructions")
                    hideSection("submissions")
                    showSection("codes")
                } else if (this.value === 'sc-instructions') {
                    hideSection("codes")
                    hideSection("submissions")
                    showSection("instructions")
                } else {
                    hideSection("codes")
                    hideSection("instructions")
                    showSection("submissions")
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
            const codesTables = $('#codes-table').DataTable({
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
                const data = codesTables.row(this).data();
                window.location = "/courses/${content.courseId}/assignments/${content.id}/codes/" + data[4]
            });
            const submissionsTable = $('#submissions-table').DataTable({
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
            $('#submissions-table tbody').on('click', 'tr', function () {
                const data = submissionsTable.row(this).data();
                console.log(data)
                window.location = "/courses/${content.courseId}/assignments/${content.id}/submissions/" + data[0]
            });
            </#if>
            var input = document.getElementById('reference-id'); // get the input element
            input.addEventListener('input', resizeInput); // bind the "resizeInput" callback on "input" event
            resizeInput.call(input); // immediately call the function

            function resizeInput() {
                this.style.minWidth = (this.value.length + 1) + "ch";
            }
        });
    </script>
    <div class="container-fluid">
        <form role="form" id="edit-assignment" method="post"
              action="/courses/${content.courseId}/assignments/<#if content.id??>${content.id}<#else >create</#if>">
            <div id="assignment-container" class="row m-3 pb-4">
                <div id="assignment-header" class="justify-content-between">
                    <div style='max-width: 400px;'>
                        <#if content.id??>
                            <div class="pds-segmentedControl pds-segmentedControl-triple">
                                <input id="sc-instructions" name="sc" type="radio" checked
                                       data-gtm="filter" data-gtm-label="first" value="sc-instructions"/>
                                <label for="sc-instructions">Instructions</label>
                                <input id="sc-codes" name="sc" type="radio" data-gtm="filter"
                                       data-gtm-label="second" value="sc-codes"/>
                                <label for="sc-codes" style="padding-right: 0.5rem">Codes</label>
                                <input id="sc-submissions" name="sc" type="radio" data-gtm="filter"
                                       data-gtm-label="second" value="sc-submissions"/>
                                <label for="sc-submissions" style="padding-right: 0.5rem">Submissions</label>
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
                                <label for="title" class="form-label required-field"><h6>Title</h6></label>
                                <input name="title" id="title" <#if content.title??>value="${content.title}" </#if>
                                       type="text" class="form-control">
                            </div>
                            <div id="snackbar">Copied Reference Id</div>
                            <#if content.referenceId??>
                                <div class="row mt-2">
                                    <div class="col-auto">
                                        <label for="reference-id" class="col-form-label"><h6>Reference Id</h6>
                                        </label>
                                    </div>
                                    <div class="col-auto">
                                        <input name="reference-id" type="text" id="reference-id"
                                               class="form-control"
                                               value="${content.referenceId}" disabled>
                                    </div>
                                    <div class="col-auto" style="padding: 0">
                                        <button style="padding: 0.5rem 1rem;" type="button" id="copy"
                                                class="copy btn col-sm text-black-50 mb-0"
                                                onclick="copyReferenceId()"><i class="material-icons opacity-10">content_copy</i>
                                        </button>
                                    </div>

                                </div>
                            </#if>
                            <div class="row align-items-center">
                                <div class="col-auto row align-items-center mt-2">
                                    <div class="col-auto">
                                        <label for="total-attempts" class="col-form-label required-field"><h6>Total
                                                Attempts</h6>
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
                                    <div class="col-auto">
                                        <label for="total-attempts" class="col-form-label required-field"><h6>Points
                                                (For Grading)</h6>
                                        </label>
                                    </div>
                                    <div class="col-auto">
                                        <input type="number" id="points" class="form-control" min="1"
                                               step="1"
                                               name="points"
                                               onfocus="this.previousValue = this.value"
                                               onkeydown="this.previousValue = this.value"
                                               <#if content.points??>value="${content.points}" </#if>
                                               oninput="validity.valid || (value = this.previousValue)">
                                    </div>
                                </div>

                                <div class="col-auto row align-items-center mt-2">
                                    <div class="col-auto align-middle">
                                        <label for="subject" class="col-form-label required-field"><h6>Subject</h6>
                                        </label>
                                    </div>
                                    <div class="col-auto align-middle">
                                        <select name="subject" id="subject" class="form-select">
                                            <#list content.subjects as subject>
                                                <option value="${subject.id}"
                                                        <#if content.subjectId??><#if subject.id == content.subjectId>selected</#if></#if> >${subject.name}</option>
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
                <h6 class="mt-4 instructions required-field">Instructions</h6>
                <div id="markdown-row" class="instructions">

                    <div class="mb-3" style="height: 100%; width: 100%;">
                        <textarea id="instructions-editor"
                                  name="instructions"
                                  form="edit-assignment"
                                  rows="15"><#if content.instructions??>${content.instructions}</#if></textarea>

                    </div>
                </div>
                <h6 class="mt-4 instructions required-field">Success Message</h6>
                <div id="success-row" class="instructions">

                    <div class="mb-3" style="height: 100%; width: 100%;">
                        <textarea id="success-editor"
                                  name="success"
                                  form="edit-assignment"
                                  rows="10"><#if content.successMessage??>${content.successMessage}</#if></textarea>

                    </div>
                </div>

                <h6 class="mt-4 instructions required-field">Failure Message</h6>
                <div id="failure-row" class="instructions">
                    <div class="mb-3" style="height: 100%; width: 100%;">
                        <textarea id="failure-editor"
                                  name="failure"
                                  form="edit-assignment"
                                  rows="10"><#if content.failureMessage??>${content.failureMessage}</#if></textarea>

                    </div>
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
                <div class="submissions">
                    <div class="table-responsive coppin-table">
                        <table id="submissions-table" class="stripe hover row-border order-column" style="width: 100%">
                            <thead>
                            <tr>
                                <th>STUDENT USERNAME</th>
                                <th>SUCCESS</th>
                                <th>NO OF ATTEMPTS</th>
                                <th>LANGUAGE</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list content.submissions as submission>
                                <tr>
                                    <td>${submission.username}</td>
                                    <td>${submission.success}</td>
                                    <td>${submission.numberOfAttempts}</td>
                                    <td>${submission.language}</td>
                                    <td>${submission.id}</td>
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
    <#if content.referenceId??>
        <script>
            function copyReferenceId() {
                navigator.clipboard.writeText("${content.referenceId}");
                const snackbar = document.getElementById("snackbar");
                snackbar.innerText
                snackbar.className = "show";
                setTimeout(function () {
                    snackbar.className = snackbar.className.replace("show", "");
                }, 3000);
            }
        </script>
    </#if>
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
