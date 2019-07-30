package android.support.driver.location

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.support.core.utils.DriverUtils
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


abstract class MyLocation(val context: Context,
                          protected val liveData: MutableLiveData<Location>) {
    protected val cache by lazy { context.getSharedPreferences(LOCATION_CACHE, Context.MODE_PRIVATE)!! }

    var next: MyLocation? = null
        private set
    var previous: MyLocation? = null
        private set

    abstract fun loadLastLocation(function: (Location) -> Unit)

    abstract fun requestUpdate()

    abstract fun removeUpdate()

    suspend fun getLastLocation() = suspendCoroutine<Location> { con ->
        loadLastLocation { con.resume(it) }
    }

    fun getFullAddress(latitude: Double, longitude: Double): Address? {
        return try {
            val geocode = Geocoder(context, Locale.getDefault())
            val addresses = geocode.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) addresses[0] else
                null
        } catch (e: IOException) {
            return null
        }
    }

    fun getAddressLine(latitude: Double, longitude: Double): String {
        return getFullAddress(latitude, longitude)?.getAddressLine(0) ?: ""
    }

    fun ifNotThenNextTo(myLocation: MyLocation): MyLocation {
        next = myLocation
        myLocation.previous = this
        return next!!
    }

    protected fun saveLocation(location: Location?) {
        cache.edit().putString(LOCATION_CACHE, Gson().toJson(location)).apply()
    }

    fun asLiveData() = liveData

    companion object {
        fun newInstance(context: Context): MyLocation {
            return LocationProvider(context)
        }

        fun newInstance(context: Context, options: MyLocationOptions): MyLocation {
            return LocationProvider(context, options = options)
        }
    }
}

class MyLocationOptions(
    var intervalRequestUpdate: Long = 5000,
    val fasterRequestUpdate: Long = 5000,
    var minDistance: Float = 0f,
    var minTime: Long = 1000,
    val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
)

private class LocationProvider(context: Context,
                               liveData: MutableLiveData<Location> = MutableLiveData(),
                               options: MyLocationOptions? = null
) : MyLocation(context, liveData) {

    private val mProvider = PlayServiceLocation(context, liveData, options)

    init {
        mProvider.ifNotThenNextTo(NetworkLocation(context, liveData, options))
            .ifNotThenNextTo(GPSLocation(context, liveData, options))
            .ifNotThenNextTo(CacheLocation(context))
    }

    override fun requestUpdate() {
        mProvider.requestUpdate()
    }

    override fun removeUpdate() {
        mProvider.removeUpdate()
    }

    override fun loadLastLocation(function: (Location) -> Unit) {
        mProvider.loadLastLocation(function)
    }

}

private class CacheLocation(context: Context) : MyLocation(context, MutableLiveData()) {
    private val mHandler = Handler()
    private val mRoot: MyLocation
        get() {
            var prev = previous!!
            while (prev.previous != null) prev = prev.previous!!
            return prev
        }

    override fun loadLastLocation(function: (Location) -> Unit) {
        if (DriverUtils.isGPSEnabled(context)) {
            retry(function)
            return
        }
        getLocationCached().let(function)
    }

    private fun retry(function: (Location) -> Unit) {
        Log.e("MyLocation", "Retry request last location")
        mHandler.postDelayed({
            mRoot.loadLastLocation(function)
        }, 1000)
    }

    private fun getLocationCached(): Location {
        val json = cache.getString(LOCATION_CACHE, "")
        if (json == "") return Location("Empty").apply {
            latitude = 0.0
            longitude = 0.0
        }
        return Gson().fromJson(json, Location::class.java)
    }

    override fun requestUpdate() {
        // Skip
    }

    override fun removeUpdate() {
        // Skip
    }
}

private class PlayServiceLocation(context: Context,
                                  liveData: MutableLiveData<Location>,
                                  private val options: MyLocationOptions?
) : MyLocation(context, liveData) {

    private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var mLocationCallback: LocationCallback? = null
    private var mLastLocationCallback: LocationCallback? = null

    override fun removeUpdate() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            mLocationCallback = null
        } else {
            next?.removeUpdate()
        }
    }

    @SuppressLint("MissingPermission")
    override fun requestUpdate() {
        if (mLocationCallback != null) return
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult!!.lastLocation
                location?.let {
                    liveData.postValue(location)
                    saveLocation(location)
                }
            }
        }
        mFusedLocationClient
            .requestLocationUpdates(createLocationRequest(), mLocationCallback, null)
            .addOnFailureListener { next?.requestUpdate() }
    }

    @SuppressLint("MissingPermission")
    override fun loadLastLocation(function: (Location) -> Unit) {
        val lastLocation = mFusedLocationClient.lastLocation
        lastLocation.addOnSuccessListener {
            if (it == null) {
                if (DriverUtils.isGPSEnabled(context))
                    requestLastLocation(function)
                else next?.loadLastLocation(function)
            } else function(it)
        }
        lastLocation.addOnFailureListener {
            next?.loadLastLocation(function)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLastLocation(function: (Location) -> Unit) {
        if (mLastLocationCallback == null)
            mLastLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    val location = locationResult?.lastLocation
                    if (location != null) {
                        removeLastLocationRequest()
                        function(location)
                        saveLocation(location)
                    } else {
                        next?.loadLastLocation(function)
                    }
                }
            }
        mFusedLocationClient
            .requestLocationUpdates(createLocationRequest(), mLastLocationCallback, null)
            .addOnFailureListener {
                removeLastLocationRequest()
                next?.loadLastLocation(function)
            }.addOnCanceledListener { removeLastLocationRequest() }
    }

    private fun removeLastLocationRequest() {
        Log.e("MyLocation", "Remove Request")
        mFusedLocationClient.removeLocationUpdates(mLastLocationCallback!!)
        mLastLocationCallback = null
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = options?.intervalRequestUpdate ?: INTERVAL_REQUEST_UPDATE
        locationRequest.fastestInterval = options?.fasterRequestUpdate ?: INTERVAL_REQUEST_UPDATE
        locationRequest.priority = options?.priority ?: LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }
}

private abstract class GPSProvider(context: Context,
                                   liveData: MutableLiveData<Location>,
                                   private val options: MyLocationOptions?)
    : MyLocation(context, liveData), LocationListener {
    protected val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun requestUpdate() {
        if (locationManager.isProviderEnabled(getProvider())) {
            locationManager.requestLocationUpdates(
                getProvider(),
                options?.minTime ?: MIN_TIME_BW_UPDATES,
                options?.minDistance ?: MIN_DISTANCE_CHANGE_FOR_UPDATES, this)
        } else {
            next?.requestUpdate()
        }
    }

    override fun removeUpdate() {
        if (locationManager.isProviderEnabled(getProvider())) {
            locationManager.removeUpdates(this)
        } else {
            next?.removeUpdate()
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadLastLocation(function: (Location) -> Unit) {
        if (locationManager.isProviderEnabled(getProvider())) {
            val location = locationManager.getLastKnownLocation(getProvider())
            if (location != null) {
                function(location)
                saveLocation(location)
                return
            }
        }
        next?.loadLastLocation(function)
    }

    abstract fun getProvider(): String

    override fun onLocationChanged(location: Location?) {
        liveData.postValue(location)
        saveLocation(location)
    }

    //region Ignore
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
    //endregion
}

private class GPSLocation(context: Context,
                          liveData: MutableLiveData<Location>,
                          options: MyLocationOptions?
) : GPSProvider(context, liveData, options) {
    override fun getProvider() = LocationManager.GPS_PROVIDER
}

private class NetworkLocation(context: Context,
                              liveData: MutableLiveData<Location>,
                              options: MyLocationOptions?
) : GPSProvider(context, liveData, options) {
    override fun getProvider() = LocationManager.NETWORK_PROVIDER
}

private const val LOCATION_CACHE = "com.android.support.driver.location.cache:location"
private const val INTERVAL_REQUEST_UPDATE: Long = 5000
private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0f // The minimum distance to change Updates in meters
private const val MIN_TIME_BW_UPDATES: Long = 1000 // The minimum time between updates in milliseconds