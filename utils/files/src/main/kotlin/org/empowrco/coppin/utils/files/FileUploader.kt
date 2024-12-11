package org.empowrco.coppin.utils.files

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import org.empowrco.coppin.utils.logs.logDebug
import org.empowrco.coppin.utils.logs.logError
import java.net.URL

interface FileUploader {
    suspend fun uploadImage(bytes: ByteArray, name: String): String?
}

internal class RealFileUploader : FileUploader {

    private val cloudFrontUrl = "https://d1z7946js1v8tm.cloudfront.net/"

    override suspend fun uploadImage(bytes: ByteArray, name: String): String? {
        return upload("coppin-imgs", name, bytes, true)
    }

    private suspend fun upload(bucketName: String, name: String, bytes: ByteArray, isImage: Boolean): String? {
        return try {
            val request = PutObjectRequest {
                bucket = bucketName
                key = name
                body = ByteStream.fromBytes(bytes)
            }
            val client = S3Client { region = System.getenv("AWS_REGION") }
            client.use { s3 ->
                val response = s3.putObject(request)
                logDebug("Tag information is ${response.eTag}")
            }
            val urlString = cloudFrontUrl + if (isImage) {
                ""
            } else {
                "docs/"
            } + name.replace(' ', '+')
            val url = URL(urlString).toExternalForm()
            url
        } catch (ex: Exception) {
            logError(ex)
            null
        }

    }
}
