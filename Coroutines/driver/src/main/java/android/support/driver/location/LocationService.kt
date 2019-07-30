package android.support.driver.location

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer

abstract class LocationService : Service(), LifecycleOwner {

    private val mLifeCycle = LifecycleRegistry(this)

    override fun getLifecycle() = mLifeCycle

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mLifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        getMyLocation().asLiveData().observe(this, Observer {
            onLocationUpdated(it!!)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        getMyLocation().requestUpdate()
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        mLifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        return super.stopService(name)
    }

    override fun onDestroy() {
        mLifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    abstract fun getMyLocation(): MyLocation

    abstract fun onLocationUpdated(it: Location)

    override fun onTaskRemoved(rootIntent: Intent?) {
        getMyLocation().removeUpdate()
        super.onTaskRemoved(rootIntent)
    }

}