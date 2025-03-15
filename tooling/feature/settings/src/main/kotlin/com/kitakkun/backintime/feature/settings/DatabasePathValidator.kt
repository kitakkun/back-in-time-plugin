package com.kitakkun.backintime.feature.settings

import java.io.File

enum class PathValidationResult {
    Valid,
    AlreadyExists,
    Invalid;
}

class DatabasePathValidator {
    fun validate(path: String): PathValidationResult {
        val file = File(path)

        if (file.exists()) {
            // If the path exists but is a directory, it's invalid for a database file
            if (file.isDirectory) {
                return PathValidationResult.Invalid
            }
            return PathValidationResult.AlreadyExists
        }

        try {
            // Check if we can create a file at this path
            file.createNewFile()
            file.delete()
            return PathValidationResult.Valid
        } catch (e: Throwable) {
            return PathValidationResult.Invalid
        }
    }
}
