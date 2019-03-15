package android.support.location

import android.app.Service
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.support.core.extensions.observe
import android.support.core.lifecycle.LifeRegistry

abstract class LocationService : Service(), LifecycleOwner {

    private val mLifeCycle = LifeRegistry(this)

    override fun getLifecycle() = mLifeCycle

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mLifeCycle.create()
        getMyLocation().asLiveData().observe(this) {
            onLocationUpdated(it!!)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLifeCycle.start()
        getMyLocation().requestUpdate()
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        mLifeCycle.stop()
        return super.stopService(name)
    }

    override fun onDestroy() {
        mLifeCycle.destroy()
        super.onDestroy()
    }

    abstract fun getMyLocation(): MyLocation

    abstract fun onLocationUpdated(it: Location)

    override fun onTaskRemoved(rootIntent: Intent?) {
        getMyLocation().removeUpdate()
        super.onTaskRemoved(rootIntent)
    }

}