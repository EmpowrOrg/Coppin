<aside class="sidenav navbar navbar-vertical navbar-expand-xs border-0 fixed-start ps" style="background: white"
       id="sidenav-main">
    <div class="sidenav-header">
        <a class="text-center justify-content-center align-items-center align-content-center align-self-center"
           target="_blank">
            <h3 class="ms-1 font-weight-bold">{} COPPIN</h3>
        </a>
        <div class="p-3 cursor-pointer text-white opacity-100 position-absolute end-0 top-0 d-xl-none"
             aria-hidden="true" id="iconSidenav">
            <i class="material-icons opacity-10">menu_open</i>
        </div>
    </div>
    <hr class="horizontal light mt-0 mb-2">
    <div class="collapse navbar-collapse  w-auto " id="sidenav-collapse-main">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="/courses">
                    <div class="text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">school</i>
                    </div>
                    <span class="nav-link-text ms-1">Courses</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/languages">
                    <div class="text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">language</i>
                    </div>
                    <span class="nav-link-text ms-1">Languages</span>
                </a>
            </li>
            <li class="nav-item mt-3">
                <h6 class="ps-4 ms-2 text-uppercase text-xs font-weight-bolder opacity-8">Settings</h6>
            </li>

            <li class="nav-item">
                <a class="nav-link" href="/user">
                    <div class="text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">account_circle</i>
                    </div>
                    <span class="nav-link-text ms-1">Profile</span>
                </a>
            </li>

            <#if isAdmin>
                <li class="nav-item">
                    <a class="nav-link" href="/admin">
                        <div class="text-center me-2 d-flex align-items-center justify-content-center">
                            <i class="material-icons opacity-10">admin_panel_settings</i>
                        </div>
                        <span class="nav-link-text ms-1">Admin</span>
                    </a>
                </li>
            </#if>
            <li class="nav-item">
                <a class="nav-link" href="/signout">
                    <div class="text-center me-2 d-flex align-items-center justify-content-center">
                        <i class="material-icons opacity-10">logout</i>
                    </div>
                    <span class="nav-link-text ms-1">Sign Out</span>
                </a>
            </li>
        </ul>
    </div>
</aside>
