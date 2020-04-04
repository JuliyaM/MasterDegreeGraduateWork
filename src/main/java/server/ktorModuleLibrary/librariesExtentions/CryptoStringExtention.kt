package ktorModuleLibrary.librariesExtentions

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.sha256(salt: String = ""): String {
    return this.hashWithAlgorithm("SHA-256", salt)
}

fun String.sha1(salt: String = ""): String {
    return this.hashWithAlgorithm("SHA-1", salt)
}


fun String.salt(salt: String): String {
    val substringStart = if (salt.isNotEmpty()) salt.substring(0, salt.length / 2) else ""
    val substringEnd = if (salt.length > 1) salt.substring(salt.length / 2, salt.length) else ""
    return (substringStart + this + substringEnd)
}

fun String.isSalted(checkedSting: String, salt: String) = this.salt(salt) == checkedSting

private fun String.hashWithAlgorithm(algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    val bytes = digest.digest(this.toByteArray(Charsets.UTF_8))
    return bytes.fold("") { str, it -> str + "%02x".format(it) }
}

private fun String.hashWithAlgorithm(algorithm: String, salt: String): String {
    return this.salt(salt).hashWithAlgorithm(algorithm)
}


