package org.empowrco.coppin.utils

object UnknownException : RuntimeException("Something bad happened.")
object UnauthorizedException : RuntimeException("User unauthenticated")
class UnsupportedLanguage(language: String) : RuntimeException("The server does not support $language")
class LanguageSupportException(language: String) :
    RuntimeException("$language does not support the necessary features.")

class AssignmentLanguageSupportException(language: String) :
    RuntimeException("The assignment does not support $language")

class InvalidUuidException(property: String) : RuntimeException("Invalid uuid specified for property $property")

data class DuplicateKeyException(val throwable: Throwable) : RuntimeException() {
    val duplicateKeyError: String?
        get() {
            val keyInfo = message?.substringAfter("Detail: Key ") ?: return null
            val keyNames = keyInfo.substringAfter("(").substringBefore(")").split(",").map { it.trim() }
            val valueNames = keyInfo.substringAfterLast("(").substringBeforeLast(")").split(",").map { it.trim() }
            val messageBuilder = StringBuilder()
            messageBuilder.appendLine("An object with these values already exists. ")
            keyNames.forEachIndexed { index, keyName ->
                messageBuilder.appendLine("$keyName: ${valueNames[index]}")
            }
            return messageBuilder.toString()
        }

}
