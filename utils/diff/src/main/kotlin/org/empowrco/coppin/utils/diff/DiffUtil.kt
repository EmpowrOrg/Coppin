package org.empowrco.coppin.utils.diff

import org.empowrco.coppin.command.CommandResponse
import org.empowrco.coppin.command.Commander
import org.empowrco.coppin.utils.files.FileUtil
import java.io.File

interface DiffUtil {
    suspend fun generateDiffHtml(output: String, expectedOutput: String): String?
}

data class DiffResult(val outputFile: File, val expectedOutputFile: File, val diff: String)

class RealDiffUtil(private val fileUtil: FileUtil, private val commander: Commander) : DiffUtil {

    private suspend fun generateDiff(output: String, expectedOutput: String): DiffResult? {

        val outputFile = fileUtil.writeToFile("output", ".txt") {
            it.appendLine(output)
        }
        val expectedOutputFile = fileUtil.writeToFile("expectedOutput", ".txt") {
            it.appendLine(expectedOutput)
        }

        val result =
            commander.execute("git diff --no-index ${outputFile.absolutePath} ${expectedOutputFile.absolutePath}")
        return when (result) {
            is CommandResponse.Success -> DiffResult(outputFile, expectedOutputFile, result.output)
            is CommandResponse.Error -> null
        }
    }

    override suspend fun generateDiffHtml(output: String, expectedOutput: String): String? {

        val diff = generateDiff(output, expectedOutput) ?: return null
        val diffFile = fileUtil.writeToFile("diff", ".diff") {
            it.write(diff.diff)
        }

        val result =
            commander.execute("diff2html --fct false -s side --su hidden -o stdout -i file -- ${diffFile.absolutePath}")
        if (result is CommandResponse.Error) {
            diff.outputFile.deleteRecursively()
            diff.expectedOutputFile.deleteRecursively()
            diffFile.deleteRecursively()
            return null
        }
        var html = result.output.replace(diff.outputFile.name, "Your Code Output")
        html = html.replace(diff.expectedOutputFile.name, "Expected Output")
        html = html.replace("(?<=>)(.*)(?=\\{)".toRegex(), "")
        html = html.replace(diff.expectedOutputFile.parent, "")
        html = html.replace("<span class=\"d2h-tag d2h-moved d2h-moved-tag\">RENAMED</span>", "")
        html = html.replace(
            "    <svg aria-hidden=\"true\" class=\"d2h-icon\" height=\"16\" version=\"1.1\" viewBox=\"0 0 12 16\" width=\"12\">\n" +
                    "        <path d=\"M6 5H2v-1h4v1zM2 8h7v-1H2v1z m0 2h7v-1H2v1z m0 2h7v-1H2v1z m10-7.5v9.5c0 0.55-0.45 1-1 1H1c-0.55 0-1-0.45-1-1V2c0-0.55 0.45-1 1-1h7.5l3.5 3.5z m-1 0.5L8 2H1v12h10V5z\"></path>\n" +
                    "    </svg>", ""
        )
        html = html.replace("{Your Code Output → Expected Output}", "Your Code Output → Expected Output")
        html = html.replace("<h1>Diff to HTML by <a href=\"https://github.com/rtfpessoa\">rtfpessoa</a></h1>", "")
        html = html.replace("<title>Diff to HTML by rtfpessoa</title>", "")
        val htmlFile = fileUtil.writeToFile("html", ".html") {
            it.write(html)
        }
        val minifyResult =
            commander.execute("html-minifier ${htmlFile.absolutePath} --collapse-whitespace --remove-comments --minify-css true --minify-js true")
        diff.outputFile.deleteRecursively()
        diff.expectedOutputFile.deleteRecursively()
        htmlFile.deleteRecursively()
        diffFile.deleteRecursively()
        if (minifyResult is CommandResponse.Error) {
            return null
        }
        println(minifyResult.output)
        return minifyResult.output
    }
}
