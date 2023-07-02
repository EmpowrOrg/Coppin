package org.empowrco.coppin.utils.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    explicitNulls = false
    encodeDefaults = true
}
