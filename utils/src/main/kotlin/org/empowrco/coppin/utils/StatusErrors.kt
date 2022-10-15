package org.empowrco.coppin.utils

object UnknownException : RuntimeException("Something bad happened.")
object UnauthorizedException : RuntimeException("User unauthenticated")
class UnsupportedLanguage(language: String) : RuntimeException("The server does not support $language")
class InvalidUuidException(property: String) : RuntimeException("Invalid uuid specified for property $property")
