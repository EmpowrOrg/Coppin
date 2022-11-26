<!--
=========================================================
* Material Dashboard 2 - v3.0.4
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard
* Copyright 2022 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://www.creative-tim.com/license)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
-->
<#-- @ftlvariable name="language" type="org.empowrco.coppin.models.portal.LanguageItem" -->
<#import "_layout.ftl" as layout />
<@layout.header >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.js"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/markdown/markdown.js"
            crossorigin="anonymous"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.32.0/codemirror.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                        <h6 class="text-white text-capitalize ps-3"><#if language??>${language.name}<#else>Create Language</#if></h6>
                    </div>
                    <div class="col-sm align-content-end">
                        <button type="button" id="delete-button" class="btn bg-gradient-primary float-end"><i class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <form role="form" id="create-assignment"
                          action="<#if language??>/languages/${language.id}<#else>/languages/create</#if>"
                          method="post">
                        <div class="row">
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="name" class="form-control" placeholder="Name"
                                       <#if language??>value="${language.name}" </#if>>
                            </div>
                            <div class="col-sm input-group input-group-outline mb-3">
                                <input type="text" name="mime" class="form-control" placeholder="Mime"
                                       <#if language??>value="${language.mime}" </#if>>
                            </div>
                            <div class="col-sm form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="supports-unit-tests"
                                       name="supports-unit-tests" ${language.supportsUnitTests?string('checked','')}>
                                <label class="form-check-label" for="supports-unit-tests">Supports Unit
                                    Tests</label><br>

                            </div>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="text" name="url" class="form-control" placeholder="CodeMirror Url"
                                   <#if language??>value="${language.url}" </#if>>
                        </div>
                        <div class="col-sm input-group input-group-outline mb-3">
                            <input type="submit"
                                   class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0"
                                   value="<#if language??>Save<#else>Create</#if>">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</@layout.header>
