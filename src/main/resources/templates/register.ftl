<#import "_layout.ftl" as layout />
<@layout.header >
    <main class="main-content  mt-0">
        <section>
            <div class="page-header min-vh-100">
                <div class="container">
                    <div class="row">
                        <div class="col-6 d-lg-flex d-none h-100 my-auto pe-0 position-absolute top-0 start-0 text-center justify-content-center flex-column">
                            <div class="position-relative bg-gradient-primary h-100 m-3 px-7 border-radius-lg d-flex flex-column justify-content-center"
                                 style="background-image: url('/img/illustrations/illustration-signup.jpg'); background-size: cover;">
                            </div>
                        </div>
                        <div class="col-xl-4 col-lg-5 col-md-7 d-flex flex-column ms-auto me-auto ms-lg-auto me-lg-5">
                            <div class="card card-plain">
                                <div class="card-header">
                                    <h4 class="font-weight-bolder">Sign Up</h4>
                                    <p class="mb-0">Enter your email and password to register</p>
                                </div>
                                <div class="card-body">
                                    <form role="form" action="/register" method="post">
                                        <div class="input-group input-group-outline my-3">
                                            <label for="firstName" class="form-label">First Name</label>
                                            <input name="firstName" id="firstName" type="text" class="form-control">
                                        </div>
                                        <div class="input-group input-group-outline my-3">
                                            <label for="lastName" class="form-label">Last Name</label>
                                            <input name="lastName" id="lastName" type="text" class="form-control">
                                        </div>
                                        <div class="input-group input-group-outline mb-3">
                                            <label for="email" class="form-label">Email</label>
                                            <input name="email" id="email" type="email" class="form-control">
                                        </div>
                                        <div class="input-group input-group-outline mb-3">
                                            <label for="password" class="form-label">Password</label>
                                            <input name="password" id="password" type="password" class="form-control">
                                        </div>
                                        <div class="input-group input-group-outline mb-3">
                                            <label for="confirm-password" class="form-label">Confirm Password</label>
                                            <input name="confirmPassword" id="confirm-password" type="password"
                                                   class="form-control">
                                        </div>
                                        <#include "error.ftl">
                                        <div class="form-check form-check-info text-start ps-0">
                                            <input name="termsAndConditions" class="form-check-input" type="checkbox"
                                                   value="true" id="flexCheckDefault" checked>
                                            <label id="terms-and-conditions" class="form-check-label"
                                                   for="flexCheckDefault">
                                                I agree the <a href="javascript:" class="text-dark font-weight-bolder">Terms
                                                    and Conditions</a>
                                            </label>
                                        </div>
                                        <div class="text-center">
                                            <button type="submit"
                                                    class="btn btn-lg bg-gradient-primary btn-lg w-100 mt-4 mb-0">Sign
                                                Up
                                            </button>
                                        </div>
                                    </form>
                                </div>
                                <div class="card-footer text-center pt-0 px-lg-2 px-1">
                                    <p class="mb-2 text-sm mx-auto">
                                        Already have an account?
                                        <a href="/login" class="text-primary text-gradient font-weight-bold">Sign in</a>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>
</@layout.header>
