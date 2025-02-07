package org.empowrco.coppin.db

object Notes: BaseTable() {
    val notes = text("notes")
    val brainrotUrl = varchar("brainrot_url", ColumnConfig.url)
    val podcastUrl = varchar("podcast_url", ColumnConfig.url)
    val summary = text("summary")
}