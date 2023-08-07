package org.empowrco.coppin.utils.routing

data class Breadcrumbs(val crumbs: List<Crumb>) {
    data class Crumb(val icon: String?, val name: String, val url: String?)
}
