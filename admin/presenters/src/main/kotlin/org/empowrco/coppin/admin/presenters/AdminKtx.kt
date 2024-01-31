package org.empowrco.coppin.admin.presenters

internal fun String.displaySecret(): String {
    return "******************************" + this.takeLast(4)
}
