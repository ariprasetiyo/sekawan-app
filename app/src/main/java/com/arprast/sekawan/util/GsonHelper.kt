package com.arprast.sekawan.util

import com.arprast.sekawan.paymo.Response
import com.arprast.sekawan.paymo.ResponseDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import java.lang.reflect.Type
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Field


object GsonHelper {
    fun createGson(): Gson {
        return GsonBuilder()
            .disableHtmlEscaping()
//            .registerTypeAdapter(Response::class.java, ResponseDeserializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
            .create()
    }

    fun createGsonSerializeNull(): Gson {
        return GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(Response::class.java, ResponseDeserializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
            .registerTypeAdapterFactory(SerializableAsNullConverter())
            .create()
    }
}

class DateTimeSerializer : JsonSerializer<Any?> {
    override fun serialize(
        src: Any?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class DateTimeDeserializer : JsonDeserializer<Any?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DateTime? {
        return DateTime(json.asJsonPrimitive.asString)
    }
}

class SerializableAsNullConverter : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        fun Field.serializedName() = declaredAnnotations
            .filterIsInstance<SerializedName>()
            .firstOrNull()?.value ?: name
        val declaredFields = type.rawType.declaredFields
        val nullableFieldNames = declaredFields
            .filter { it.declaredAnnotations.filterIsInstance<SerializeNull>().isNotEmpty() }
            .map { it.serializedName() }
        val nonNullableFields = declaredFields.map { it.serializedName() } - nullableFieldNames

        return if (nullableFieldNames.isEmpty()) {
            null
        } else object : TypeAdapter<T>() {
            private val delegateAdapter = gson.getDelegateAdapter(this@SerializableAsNullConverter, type)
            private val elementAdapter = gson.getAdapter(JsonElement::class.java)

            override fun write(writer: JsonWriter, value: T?) {
                val jsonObject = delegateAdapter.toJsonTree(value).asJsonObject
                nonNullableFields
                    .filter { jsonObject.get(it) is JsonNull }
                    .forEach { jsonObject.remove(it) }
                val originalSerializeNulls = writer.serializeNulls
                writer.serializeNulls = true
                elementAdapter.write(writer, jsonObject)
                writer.serializeNulls = originalSerializeNulls
            }

            override fun read(reader: JsonReader): T {
                return delegateAdapter.read(reader)
            }
        }
    }
}
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class SerializeNull