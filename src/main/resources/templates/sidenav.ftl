<aside
        class="sidenav navbar navbar-vertical navbar-expand-xs border-0 border-radius-top-end-xl border-radius-bottom-end-xl fixed-start bg-gradient-dark"
        id="sidenav-main"
>
    <div class="sidenav-header d-xl-none">
        <div class="nav-item ps-4 d-flex align-items-center mt-4-5">
            <a
                    href="javascript:;"
                    class="nav-link text-body p-0"
                    id="iconNavbarSidenavMobile"
            >
                <div class="sidenav-toggler-inner">
                    <i class="sidenav-toggler-line"></i>
                    <i class="sidenav-toggler-line"></i>
                    <i class="sidenav-toggler-line"></i>
                </div>
            </a>
        </div>
    </div>
    <hr class="horizontal light mt-0 mb-xl-4"/>

    <div class="collapse navbar-collapse w-auto" id="sidenav-collapse-main">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link text-white " href="/assignments">
                    <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">assignment</i>
                    </div>
                    <span class="nav-link-text ms-1">Assignments</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="/languages">
                    <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">language</i>
                    </div>
                    <span class="nav-link-text ms-1">Languages</span>
                </a>
            </li>
            <li class="nav-item mt-3">
                <h6 class="ps-4 ms-2 text-uppercase text-xs text-white font-weight-bolder opacity-8">Settings</h6>
            </li>
            <#if isAdmin>
                <li class="nav-item">
                    <a class="nav-link text-white " href="/users">
                        <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
                            <i class="material-icons opacity-10">manage_accounts</i>
                        </div>
                        <span class="nav-link-text ms-1">Users</span>
                    </a>
                </li>
            <#else >
                <li class="nav-item">
                    <a class="nav-link text-white " href="/user">
                        <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
                            <i class="material-icons opacity-10">account_circle</i>
                        </div>
                        <span class="nav-link-text ms-1">Profile</span>
                    </a>
                </li>
            </#if>
            <li class="nav-item">
                <a class="nav-link text-white " href="/signout">
                    <div class="text-white text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">logout</i>
                    </div>
                    <span class="nav-link-text ms-1">Sign Out</span>
                </a>
            </li>
        </ul>
    </div>
</aside>
