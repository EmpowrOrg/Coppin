<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetSubmissionResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.js"
            integrity="sha512-8RnEqURPUc5aqFEN04aQEiPlSAdE0jlFS/9iGgUyNtwFnSKCXhmB6ZTNl7LnDtDWKabJIASzXrzD0K+LYexU9g=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/codemirror.min.css"
          integrity="sha512-uf06llspW44/LZpHzHT6qBOIVODjWtv4MxCricRxkzvopAlSWnTf6hpZTFxuuZcuNE9CBQhqE0Seu1CoRk84nQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <#list content.submissions as submission>
        <script src="${submission.language.url}" crossorigin="anonymous"></script>
    </#list>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.65.7/addon/display/autorefresh.min.js"
            integrity="sha512-vAsKB7xXQAWMn5kcwda0HkFVKUxSYwrmrGprVhmbGFNAG1Ij+2epT3zzdwjHTJyDsKXsiEdrUdhIxh7loHyX+A=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdn.jsdelivr.net/npm/showdown@2.1.0/dist/showdown.min.js"></script>
    <script>
        $(document).ready(function () {
            const codeMirrorConfig = {
                lineNumbers: true,
                lineWrapping: true,
                indentWithTabs: true,
                lineWiseCopyCut: true,
                autoCloseBrackets: true,
                readOnly: true,
                autoRefresh: true,
            }

            function unescape(str) {
                const replacements = {
                    '\\b': '\b',
                    '\\t': '\t',
                    '\\n': '\n',
                    '\\f': '\f',
                    '\\r': '\r',
                    '\\"': '\"',
                    "\\'": "\'",
                    '\\\\': '\\'
                };

                return str.replace(/\\['"bfnrt\\]/g, function (match) {
                    return replacements[match];
                });
            }

            const submissions = JSON.parse("${content.submissionsJson}")
            const submissionsSize = submissions.length
            const converter = new showdown.Converter()
            for (let i = 0; i < submissionsSize; i++) {

                const submission = submissions[i]

                // Setup student code
                const studentCodeTextArea = document.getElementById("student-code-editor-" + i);
                codeMirrorConfig.value = unescape(submission.code)
                codeMirrorConfig.mode = submission.language.mime
                const studentCodeCodeMirror = CodeMirror(function (elt) {
                    studentCodeTextArea.parentNode.replaceChild(elt, studentCodeTextArea);
                }, codeMirrorConfig);
                studentCodeCodeMirror.setSize('100%');

                // Setup tested code
                const studentTestedCodeTextArea = document.getElementById("student-tested-code-editor-" + i);
                codeMirrorConfig.value = unescape(submission.fullCode)
                const studentTestedCodeCodeMirror = CodeMirror(function (elt) {
                    studentTestedCodeTextArea.parentNode.replaceChild(elt, studentTestedCodeTextArea);
                }, codeMirrorConfig);
                studentTestedCodeCodeMirror.setSize('100%');

                // Setup feedback
                const feedBackHtml = converter.makeHtml(submission.feedback);
                const studentFeedback = document.getElementById("feedback-" + i);
                studentFeedback.innerHTML = feedBackHtml;
            }

            $('#submission-select').on('change', function () {
                const index = parseInt(this.value)
                const submissionsSize = ${content.submissions?size}
                for (let i = 0; i < submissionsSize; i++) {
                    $('#submission-' + i).css("display", "none");
                }
                $('#submission-' + index).css("display", "block");

            });
            <#if 0 < content.submissions?size>
            $('#submission-select').val(${content.submissions?size - 1}).trigger("change")
            </#if>
        });
    </script>
    <style>
        #submission-container {
            border-radius: 1rem;
            border: 0.125rem solid #DEE2E8;
            background: #FFF
        }

        #submission-header {
            display: flex;
            align-items: flex-start;
            gap: 0.5rem;
        }

        #submission-tested-header {
            display: flex;
            align-items: flex-start;
            gap: 0.5rem;
        }

        .submission-code {
            display: none;
        }
    </style>
    <div class="container-fluid">
        <div id="submission-container" class="row m-3 p-4">
            <h4>${content.assignment}</h4>
            <#include "error.ftl">
            <select class="form-select mb-3 p-2" id="submission-select" aria-label=".form-select-lg example"
                    style="max-width: 400px">
                <option selected disabled>Attempt Number</option>
                <#list content.submissions as submission>
                    <option value="${submission?index}"
                            <#if submission?is_last>selected</#if>>${submission?index + 1}</option>
                </#list>
            </select>
            <#list content.submissions as submission>
                <div id="submission-${submission?index}" class="submission-code">
                    <div id="submission-header" class="justify-content-between">
                        <h6>
                            ${content.studentId}'s Code
                        </h6>

                        <h6 id="submission-language">
                            ${submission.language.name}
                        </h6>

                    </div>
                    <div id="student-code" class="ms-0 ps-0">
                    <textarea id="student-code-editor-${submission?index}"
                              class="form-control"
                              rows="5"></textarea>
                    </div>
                    <div id="submission-tested-${submission?index}">
                        <div id="submission-tested-header" class="justify-content-between">
                            <h6>
                                Tested Code
                            </h6>

                            <h6 id="submission-tested-language">
                                ${submission.language.name}
                            </h6>

                        </div>
                        <div id="student-code" class="ms-0 ps-0">
                        <textarea id="student-tested-code-editor-${submission?index}"
                                  class="form-control"
                                  rows="5"></textarea>
                        </div>
                    </div>
                    <div id="feedback-container-${submission?index}">
                        <h4>Feedback</h4>
                        <div id="feedback-${submission?index}"></div>
                    </div>
                </div>
            </#list>
        </div>
    </div>

</@layout.header>
