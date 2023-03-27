
<#-- @ftlvariable name="content" type="org.empowrco.coppin.users.presenters.GetUserResponse" -->
<#import "_layout.ftl" as layout />
<@layout.header >
<script>
    console.log(JSON.stringify('${content}'))
</script>
<body class="bg-gray-200">
<main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg mt-4">
    <div class="container-fluid py-4 pe-2">
        <div class="row">
            <div class="col-lg-12 col-md-12 col-12 mx-auto">
                <div class="card z-index-0 fadeIn3 fadeInBottom">
                    <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                        <div class="bg-gradient-primary shadow-primary border-radius-lg py-3 pe-1">
                            <h4 class="text-white font-weight-bolder text-center mt-2 mb-0">Edit User</h4>
                        </div>
                    </div>
                    <div class="card-body">
                        <#include "error.ftl">
                        <form role="form" class="text-start" method="post" action="/user/${content.id}">
                            <div class="row">
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">First Name</label>
                                    <input name="firstName" type="text" class="form-control"
                                           value="${content.firstName}" readonly>
                                </div>
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">Last Name</label>
                                    <input name="lastName" type="text" class="form-control"
                                           value="${content.lastName}" readonly>
                                </div>
                                <div class="input-group input-group-static my-3 col">
                                    <label class="form-label">Email</label>
                                    <input name="email" type="email" class="form-control"
                                           value="${content.email}" readonly>
                                </div>
                            </div>
                            <div class="row">
                                <div class="form-group col-md">
                                    <label for="authorized">Authorized</label>
                                    <select name="authorized" id="authorized" class="form-select">
                                        <option value="true"
                                                <#if content.authorized>selected</#if>>Yes
                                        </option>
                                        <option value="false"
                                                <#if !content.authorized>selected</#if>>No
                                        </option>
                                    </select>
                                </div>
                                <div class="form-group col-md">
                                    <label for="authorized">Role</label>
                                    <select name="type" id="type" class="form-select">
                                        <option value="Admin"
                                                <#if content.type == "Admin">selected</#if>>Admin
                                        </option>
                                        <option value="Teacher"
                                                <#if content.type == "Teacher">selected</#if>>Teacher
                                        </option>
                                    </select>
                                </div>
                            </div>


                            <#if content.id??>
                                <input type="hidden" name="id" value="${content.id}">
                            </#if>
                            <div class="text-center col-sm">
                                <button type="submit" class="btn bg-gradient-primary my-4 mb-2 px-6">Save
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
</@layout.header>
