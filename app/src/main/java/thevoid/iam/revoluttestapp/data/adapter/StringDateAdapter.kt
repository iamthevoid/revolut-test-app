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
/**
 * This adapter was created because using Date most convenient than using String. In this app date not
 * using, but app can extend and it can be useful
 */
class StringDateAdapter : TypeAdapter<Date>() {

    private val datePattern = "yyyy-MM-dd"

    override fun read(`in`: JsonReader?): Date? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }

        return `in`?.nextString()?.toDate(datePattern)
    }

    override fun write(out: JsonWriter?, value: Date?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        out?.value(value.toApiString(datePattern))
    }
}