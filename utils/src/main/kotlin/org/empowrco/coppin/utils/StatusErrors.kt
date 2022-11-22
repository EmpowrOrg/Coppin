package org.empowrco.coppin.utils

object UnknownException : RuntimeException("Something bad happened.")
object UnauthorizedException : RuntimeException("User unauthenticated")
class UnsupportedLanguage(language: String) : RuntimeException("The server does not support $language")
class LanguageSupportException(language: String) :
    RuntimeException("$language does not support the necessary features.")

class AssignmentLanguageSupportException(language: String) :
    RuntimeException("The assignmnet does not support $language")

class InvalidUuidException(property: String) : RuntimeException("Invalid uuid specified for property $property")
