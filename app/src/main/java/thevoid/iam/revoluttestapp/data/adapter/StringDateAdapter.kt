package thevoid.iam.revoluttestapp.data.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import thevoid.iam.revoluttestapp.toApiString
import thevoid.iam.revoluttestapp.toDate
import java.util.*

/**
 * Created by alese_000 on 21.02.2018.
 */
class StringDateAdapter : TypeAdapter<Date>() {

    private val datePattern = "yyyy-MM-dd"

    override fun read(`in`: JsonReader?): Date? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }

        val nextString = `in`?.nextString()
        val toDate = nextString?.toDate(datePattern)
        return toDate
    }

    override fun write(out: JsonWriter?, value: Date?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        val toApiString = value.toApiString(datePattern)
        out?.value(toApiString)
    }
}