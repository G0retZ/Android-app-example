package com.example.app

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class LocationDeserializer : JsonDeserializer<Location> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Location? {
        val jsonObject = json?.asJsonObject ?: return null
        val coords = jsonObject["coordinates"].asJsonArray

        return Location(
            coords[0].asDouble,
            coords[1].asDouble
        )
    }
}