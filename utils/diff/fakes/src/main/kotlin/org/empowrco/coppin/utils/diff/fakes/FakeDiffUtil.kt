package org.empowrco.coppin.utils.diff.fakes

import org.empowrco.coppin.utils.diff.DiffUtil

class FakeDiffUtil : DiffUtil {

    val diffHtmls = mutableMapOf<Pair<String, String>, String>()

    override suspend fun generateDiffHtml(output: String, expectedOutput: String): String? {
        return if (diffHtmls.containsKey(output to expectedOutput)) {
            diffHtmls.remove(output to expectedOutput)
        } else {
            null
        }
    }
}
