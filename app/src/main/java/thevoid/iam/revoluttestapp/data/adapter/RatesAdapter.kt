package thevoid.iam.revoluttestapp.data.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import thevoid.iam.revoluttestapp.data.model.CurrencyRate
import java.io.IOException
import kotlin.collections.ArrayList

/**
 * Created by alese_000 on 21.02.2018.
 */

/**
 * Using list of rates more convenient than Map of Key-Values pairs.
 */
class RatesAdapter : TypeAdapter<List<CurrencyRate>>() {

    /**
     * This adapter always used to red value, but never to write it.
     */
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: List<CurrencyRate>?) {
        if (value == null) {
            out.nullValue()
            return
        }

        // TODO implement convert back to map if need
        out.value(value.toString())
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): List<CurrencyRate>? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }

        val output = ArrayList<CurrencyRate>()

        `in`.beginObject()
        while (`in`.hasNext()) {
            output.add(readRate(`in`))
        }
        `in`.endObject()

        return output
    }

    private fun readRate(jsonReader: JsonReader): CurrencyRate {
        return CurrencyRate(jsonReader.nextName(), jsonReader.nextDouble())
    }
}
