package org.empowrco.coppin.db

internal val databaseVersion = 1

internal val migrations = listOf(
    "ALTER TABLE assignments RENAME COLUMN assignment_id TO reference_id;",
)
