package org.empowrco.coppin.db

import org.empowrco.coppin.models.StudentNote

object StudentNotes: BaseTable() {
    val studentId = text("student_id").index()
    val notes = reference("notes", Notes.id)
    val comment = text("comment")
    val contextBefore = varchar("context_before", ColumnConfig.docContext)
    val contextAfter = varchar("context_after", ColumnConfig.docContext)
    val highlightedText = text("highlighted_text")
    val type = enumeration<StudentNote.Type>("type")
    val contextType = enumeration<StudentNote.ContextType>("context_type")
    val index = integer("index") // can be line number or number of seconds. Helps for fuzzy matching if document changes
}