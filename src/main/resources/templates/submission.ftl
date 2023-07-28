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
            console.log(submissionsSize)
            for (let i = 0; i < submissionsSize; i++) {
                console.log("student-code-editor-" + i)
                const studentCodeTextArea = document.getElementById("student-code-editor-" + i);
                console.log(studentCodeTextArea)
                const submission = submissions[i]
                codeMirrorConfig.value = unescape(submission.code)
                codeMirrorConfig.mode = submission.language.mime
                console.log(codeMirrorConfig)
                const studentCodeCodeMirror = CodeMirror(function (elt) {
                    studentCodeTextArea.parentNode.replaceChild(elt, studentCodeTextArea);
                }, codeMirrorConfig);
                studentCodeCodeMirror.setSize('100%');
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
                </div>
            </#list>
        </div>
    </div>

</@layout.header>
