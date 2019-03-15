package android.support.location

import android.arch.lifecycle.MutableLiveData
import android.support.core.helpers.RequestBodyBuilder
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


private const val DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?"

class RoutePathTask {
    private val mLiveData = MutableLiveData<Direction>()
    private val mExecutor = Executors.newFixedThreadPool(1)


    private fun execute(from: LatLng, to: LatLng): MutableLiveData<Direction> {
        mExecutor.execute {
            val urlFinding = getUrl(from, to)
            val directionJson = requestUrl(urlFinding)
            val direction = Gson().fromJson(directionJson, Direction::class.java)
            direction.apply {
                if (direction.isStatusOk && direction.hasRoute && direction.decode) {
                    mLiveData.postValue(direction)
                }
            }
        }
        return mLiveData
    }

    private fun requestUrl(urlFinding: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urlFinding)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuilder()
            br.forEachLine {
                sb.append(it)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                iStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            urlConnection?.disconnect()
        }
        return data
    }

    private fun getUrl(from: LatLng, to: LatLng): String {
        return DIRECTION_URL + RequestBodyBuilder()
                .put("origin", from.latitude.toString() + "," + from.longitude)
                .put("destination", to.latitude.toString() + "," + to.longitude)
                .put("mode", "walking")
                .put("key", "AIzaSyCRH74Be2lLNqKWjEH_mb_tK5DlVrQTt-8")
                .buildQuery()
    }

    companion object {
        fun find(from: LatLng, to: LatLng) =
                RoutePathTask().execute(from, to)
    }

}