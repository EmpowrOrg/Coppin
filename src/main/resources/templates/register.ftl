<#import "_layout.ftl" as layout />
<style>
    #sign-up-container {
        max-width: none;
        --bs-gutter-x: 0;
    }

    #sign-up-row {
        position: absolute;
        height: 100%;
        width: 100%;
        max-width: none;
        margin-left: 0 !important;
        margin-right: 0 !important;
        min-width: 200px;
    }

    #sign-up-have-account-container {
        display: flex;
    }

    #sign-up-have-account {
        flex: 1;
    }

    .sign-up-input {
        max-width: 400px;
    }

    #sign-up-button {
        max-width: 400px;
        min-width: 250px;
    }

    #sign-up-carousel {
        max-width: 450px;
    }

    .carousel-text {
        color: #F8F9FA;
    }
</style>
<@layout.header >
    <main class="container" id="sign-up-container">
        <div class="row" id="sign-up-row" style="">
            <div class="col" style="background: white;">
                <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center">
                    <form role="form" action="/register" method="post">
                        <h1> {} COPPIN</h1>
                        <h3>Create Your Account</h3>
                        <div id="sign-up-have-account-container" class="row">
                            <a id="sign-up-have-account">Already have an account? <b>Sign In</b></a>
                        </div>
                        <div class="sign-up-input mb-3 mt-3">
                            <label class="form-label" for="sign-up-email">Email</label>
                            <input name="email" id="sign-up-email" type="email" class="form-control">
                        </div>
                        <div class="sign-up-input mb-3">
                            <label class="form-label" for="sign-up-first-name">First Name</label>
                            <input name="firstName" id="sign-up-first-name" type="text" class="form-control">
                        </div>
                        <div class="sign-up-input mb-3">
                            <label class="form-label" for="sign-up-last-name">Last Name</label>
                            <input name="lastName" id="sign-up-last-name" type="text" class="form-control">
                        </div>
                        <div class="sign-up-input mb-3">
                            <label class="form-label" for="sign-up-password">Password</label>
                            <input name="password" id="sign-up-password" type="password" class="form-control">
                        </div>
                        <div class="sign-up-input mb-3">
                            <label class="form-label" for="sign-up-confirm-password">Confirm Password</label>
                            <input name="confirmPassword" id="sign-up-confirm-password" type="password"
                                   class="form-control">
                        </div>
                        <#include "error.ftl">
                        <button id="sign-up-button" type="submit" class="btn btn-primary my-4 mb-2">Create Your Account
                        </button>
                    </form>
                </div>
            </div>
            <div class="col" style="background-image: linear-gradient(180deg, #3E3D45 0%, #202020 100%);">
                <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center text-start">
                    <div id="sign-up-carousel-controls" class="carousel slide" data-bs-interval="10000"
                         data-bs-ride="carousel">
                        <div id="sign-up-carousel" class="carousel-inner">
                            <div class="carousel-item active">
                                <h3 class="carousel-text">
                                    A revolutionary programming assignment creator and grader for the Open edX Platform
                                </h3>
                                <p class="carousel-text">
                                    Empowr's goal is to create an equitable future for all. Coppin empowers educators to
                                    create and grade programming assignments effortlessly, supporting over 100
                                    programming languages. Since students can write and execute the programming code in
                                    the browser with Coppin, they will no longer need expensive hardware to learn
                                    computer science.
                                </p>
                            </div>
                            <div class="carousel-item">
                                <h3 class="carousel-text">
                                    Coppin is named after Fanny Coppin. She was the first African American
                                    Superintendent in the United States.
                                </h3>
                                <p class="carousel-text">
                                    Coppin's streamlined interface enables instructors to develop programming
                                    assignments in under three minutes, revolutionizing how programming courses are
                                    created and delivered on the Open edX platform. By harnessing the power of
                                    CodeEditorXblock, an XBlock for Open edX that facilitates input and checking of any
                                    programming language, and Doctor, a secure server for executing arbitrary code,
                                    Coppin offers a comprehensive solution for programming education.
                                </p>
                            </div>
                            <div class="carousel-item">
                                <h3 class="carousel-text">
                                    Coppin has four key features:
                                </h3>
                                <ul class="carousel-text">
                                    <li>Support for over 100 programming languages, allowing educators to create Open
                                        edX
                                        courses in any language they wish.
                                    </li>
                                    <br>
                                    <li>A seamless integration with CodeEditorXblock, enabling efficient input and
                                        checking
                                        of programming assignments.
                                    </li>
                                    <br>
                                    <li>A secure, cloud-based execution environment powered by Doctor eliminates
                                        students'
                                        need to have expensive computers for compiling code.
                                    </li>
                                    <br>
                                    <li>Free and Open Source Software</li>
                                </ul>
                            </div>
                        </div>


                    </div>
                    <button class="carousel-control-next" type="button" data-bs-target="#sign-up-carousel-controls"
                            data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                </div>
            </div>
        </div>
    </main>
</@layout.header>
