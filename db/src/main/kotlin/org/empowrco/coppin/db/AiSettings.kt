package org.empowrco.coppin.db

object AiSettings : BaseTable() {
    val model = text("model")
    val orgKey = text("org_key")
    val key = text("key")
    val prePrompt = text("pre_prompt")
}
