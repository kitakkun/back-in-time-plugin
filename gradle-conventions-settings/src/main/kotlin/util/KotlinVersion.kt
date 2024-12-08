package util

data class KotlinVersion(
    val majorVersion: Int,
    val minorVersion: Int,
    val patchVersion: Int,
) {
    override fun toString(): String {
        return "$majorVersion.$minorVersion.$patchVersion"
    }

    operator fun compareTo(other: KotlinVersion): Int {
        return when {
            this.majorVersion != other.majorVersion -> this.majorVersion - other.majorVersion
            this.minorVersion != other.minorVersion -> this.minorVersion - other.minorVersion
            else -> this.patchVersion - other.patchVersion
        }
    }
}

fun String.parseToKotlinVersion(): KotlinVersion {
    val (major, minor, patch) = substringBefore("-").split(".").map { it.toInt() }
    return KotlinVersion(major, minor, patch)
}
