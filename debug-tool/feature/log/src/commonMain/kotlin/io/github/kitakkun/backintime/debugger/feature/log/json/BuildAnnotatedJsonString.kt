package io.github.kitakkun.backintime.debugger.feature.log.json

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

enum class ColorMode {
    NONE,
    KEY,
    VALUE,
}

fun buildAnnotatedJsonString(rawJsonString: String): AnnotatedString = buildAnnotatedString {
//    var inKey = false
//    var inValue = false
//
//    rawJsonString.forEach { char ->
//        when (char) {
//            '{', '}', '[', ']', ',', ':' -> {
//                withStyle(style = SpanStyle(color = Color.White)) {
//                    append(char)
//                }
//                inKey = false
//                inValue = false
//            }
//
//            '"' -> {
//                when {
//                    inKey -> {
//                        inKey = false
//                        inValue = true
//                        withStyle(style = SpanStyle(color = Color.Red)) {
//                            append(char)
//                        }
//                    }
//                }
//                if (!inKey && !inValue) {
//                    inKey = true
//                    withStyle(style = SpanStyle(color = Color.Red)) {
//                        append(char)
//                    }
//                } else if (inKey) {
//                } else if (inValue) {
//                    inValue = false
//                    withStyle(style = SpanStyle(color = Color.Blue)) {
//                        append(char)
//                    }
//                }
//            }
//
//            else -> {
//                withStyle(
//                    style = SpanStyle(
//                        color = when {
//                            inKey -> Color.Red
//                            inValue -> Color.Blue
//                            else -> Color.White
//                        }
//                    )
//                ) {
//                    append(char)
//                }
//            }
//        }
//    }
}
