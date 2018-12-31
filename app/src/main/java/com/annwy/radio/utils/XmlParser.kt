package com.annwy.radio.utils

import android.content.res.Resources
import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

open class XmlParser(protected val resources: Resources?) {
    protected val namespace: String? = null

    @Throws(IOException::class, XmlPullParserException::class)
    protected fun readTag(parser: XmlResourceParser, tag: String): String {
        parser.require(XmlResourceParser.START_TAG, namespace, tag)
        val summary = readText(parser)
        parser.require(XmlResourceParser.END_TAG, namespace, tag)
        return summary
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlResourceParser): String {
        var result = ""
        if (parser.next() == XmlResourceParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    protected fun skip(parser: XmlResourceParser) {
        if (parser.eventType != XmlResourceParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlResourceParser.END_TAG -> --depth
                XmlResourceParser.START_TAG -> ++depth
            }
        }
    }
}