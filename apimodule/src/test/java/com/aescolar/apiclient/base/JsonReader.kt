package com.aescolar.apiclient.base

import java.io.File
import org.apache.commons.io.FileUtils

object JsonReader {
    private const val FILE_ENCODING = "UTF-8"

    fun getContentFromFile(fileName: String? = null): String {
        if (fileName == null) {
            return ""
        }

        val file = File(javaClass.getResource("/$fileName")?.file)
        val lines = FileUtils.readLines(
            file,
            FILE_ENCODING
        )
        val stringBuilder = StringBuilder()
        for (line in lines) {
            stringBuilder.append(line)
        }
        return stringBuilder.toString()
    }
}
