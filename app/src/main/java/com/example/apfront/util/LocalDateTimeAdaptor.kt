
package com.example.apfront.util

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.format(formatter))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        if (json == null) {
            throw JsonParseException("Date string is null")
        }

        if (json.isJsonArray) {
            val arr = json.asJsonArray
            if (arr.size() >= 6) {
                return LocalDateTime.of(
                    arr[0].asInt, arr[1].asInt, arr[2].asInt,
                    arr[3].asInt, arr[4].asInt, arr[5].asInt
                )
            } else {
                throw JsonParseException("Date array is malformed")
            }
        }
        return LocalDateTime.parse(json.asString, formatter)
    }
}