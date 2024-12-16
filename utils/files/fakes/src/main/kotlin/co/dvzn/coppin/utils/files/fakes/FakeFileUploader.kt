package co.dvzn.coppin.utils.files.fakes

import org.empowrco.coppin.utils.files.FileUploader

class FakeFileUploader : FileUploader {
    override suspend fun uploadImage(bytes: ByteArray, name: String): String? {
        return null
    }
}