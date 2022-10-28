package org.empowrco.coppin.assignment.presenters

import org.empowrco.coppin.models.portal.AssignmentItem
import org.empowrco.coppin.models.portal.CodeListItem
import org.empowrco.coppin.models.portal.FeedbackListItem

data class GetAssignmentPortalResponse(
    val assignment: AssignmentItem,
    val codes: List<CodeListItem>,
    val feedback: List<FeedbackListItem>,
)

