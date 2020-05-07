package ru.breffi.storyidsample.api.typeadapter

import com.google.gson.*
import com.google.gson.internal.bind.util.ISO8601Utils
import java.lang.reflect.Type
import java.text.ParseException
import java.text.ParsePosition
import java.util.*


class GmtDateTypeAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    @Synchronized
    override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(ISO8601Utils.format(date))
    }

    @Synchronized
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): Date {

        try {
            return ISO8601Utils.parse(jsonElement.asString, ParsePosition(0))
        } catch (e: ParseException) {
            throw JsonSyntaxException(jsonElement.asString, e)
        }

    }
}