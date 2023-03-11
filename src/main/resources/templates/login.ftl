<#import "_layout.ftl" as layout />
<@layout.header >
    <body class="bg-gray-200">
    <main class="main-content  m-0 p-0">
        <div class="page-header align-items-start min-vh-100"
             style="background-image: url('https://images.unsplash.com/photo-1497294815431-9365093b7331?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1950&q=80');">
            <span class="mask bg-gradient-dark opacity-6"></span>
            <div class="container my-auto">
                <div class="row">
                    <div class="col-lg-4 col-md-8 col-12 mx-auto">
                        <div class="card z-index-0 fadeIn3 fadeInBottom">
                            <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                                <div class="bg-gradient-primary shadow-primary border-radius-lg py-3 pe-1">
                                    <h4 class="text-white font-weight-bolder text-center mt-2 mb-0">Sign in</h4>
                                </div>
                            </div>
                            <div class="card-body">
                                <form role="form" class="text-start" action="/login" method="post">
                                    <div class="input-group input-group-outline my-3">
                                        <label for="email" class="form-label">Email</label>
                                        <input name="email" id="email" type="email" class="form-control">
                                    </div>
                                    <div class="input-group input-group-outline mb-3">
                                        <label for="password" class="form-label">Password</label>
                                        <input name="password" id="password" type="password" class="form-control">
                                    </div>
                                    <#include "error.ftl">
                                    <div class="text-center">
                                        <button type="submit" class="btn bg-gradient-primary w-100 my-4 mb-2">Sign in
                                        </button>
                                    </div>
                                    <p class="mt-4 text-sm text-center">
                                        Don't have an account?
                                        <a href="/register" class="text-primary text-gradient font-weight-bold">Sign
                                            up</a>
                                    </p>

                                    <p class="mt-4 text-sm text-center">
                                        Forgot your password?
                                        <a href="/forgot-password" class="text-primary text-gradient font-weight-bold">Reset
                                            your password</a>
                                    </p>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <footer class="footer position-absolute bottom-2 py-2 w-100">
                <div class="container">
                    <div class="row align-items-center justify-content-lg-between">
                        <div class="col-12 col-md-6 my-auto">
                            <div class="copyright text-center text-sm text-white text-lg-start">
                                ©
                                <script>
                                    document.write(new Date().getFullYear())
                                </script>
                                ,
                                made by
                                <a href="https://devezin.tech" class="font-weight-bold text-white">Devezin.Tech</a>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </main>
    </body>
</@layout.header>
