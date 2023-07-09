<#import "_layout.ftl" as layout />
<style>
    #sign-in-container {
        background: white;
        max-width: none;
    }

    .sign-in-input {
        max-width: 400px;
    }

    #sign-in-button {
        max-width: 400px;
        min-width: 250px;
    }
</style>
<@layout.header >
    <body class="bg-gray-200">
    <main class="main-content  m-0 p-0">
        <div class="page-header align-items-start min-vh-100"
             style="background-image: url('https://images.unsplash.com/photo-1497294815431-9365093b7331?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1950&q=80');">
            <div id="sign-in-container" class="container my-auto">
                <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center">
                    <h1> {} COPPIN</h1>
                    <h3 class="mt-3">Sign In</h3>
                    <p>Enter your email and password to Sign In</p>
                    <form role="form" class="text-start" action="/login" method="post">
                        <div class="sign-in-input mb-3 mt-3">
                            <label class="form-label" for="sign-in-email">Email</label>
                            <input name="email" id="sign-in-email" type="email" class="form-control">
                        </div>
                        <div class="sign-in-input mb-3 mt-3">
                            <label class="form-label" for="sign-in-password">Password</label>
                            <input name="password" id="sign-in-password" type="password" class="form-control">
                        </div>
                        <#include "error.ftl">
                        <button id="sign-in-button" type="submit" class="btn btn-primary my-4 mb-2">Sign In
                        </button>
                    </form>
                    <a href="/forgot-password" class="mt-3">Forgot your password? <b>Reset your password</b> </a>
                    <a href="/register" class="mt-2">Don't have an account? <b>Sign Up!</b></a>
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
                                <a href="https://empowrco.org" class="font-weight-bold text-white">Empowr</a>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </main>
    </body>
</@layout.header>
