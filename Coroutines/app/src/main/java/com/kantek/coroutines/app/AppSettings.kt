package android.support.core.helpers

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.core.base.BaseActivity


private const val OPEN_GALLERY = 1
private const val SELECT_MULTIPLE_IMAGE = 2
private const val CAMERA_REQUEST = 3
private const val RC_OPEN_PLACE_AUTO_COMPLETE = 4

class AppSettings(val context: BaseActivity) {
    companion object {
        private const val REQUEST_CHECK_SETTINGS = 214
        private const val REQUEST_ENABLE_GPS = 516
    }

    fun openGalleryForImage(function: (Uri) -> Unit) {
        openGallery("image/*", "Select Picture", function)
    }

    fun openGalleryForVideo(function: (Uri) -> Unit) {
        openGallery("video/*", "Select Videos", function)
    }

    private fun openGallery(format: String, title: String, function: (Uri) -> Unit) {
        val galleryIntent = Intent()
        galleryIntent.type = format
        galleryIntent.action = Intent.ACTION_GET_CONTENT

        context.resultLife.onActivityResult { requestCode, resultCode, intent ->
            if (requestCode != OPEN_GALLERY
                || resultCode != Activity.RESULT_OK
                || intent == null
            ) return@onActivityResult

            val data = intent.data ?: return@onActivityResult
            function.invoke(data)
        }

        context.startActivityForResult(Intent.createChooser(galleryIntent, title), OPEN_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun openGalleryForImages(function: (MutableList<Uri>) -> Unit) {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        context.resultLife.onActivityResult { requestCode, resultCode, intent ->
            if (requestCode != SELECT_MULTIPLE_IMAGE
                || resultCode != Activity.RESULT_OK
                || intent == null
            ) return@onActivityResult
            if (intent.clipData != null) {
                val count = intent.clipData!!.itemCount
                val uris = (0 until count).asSequence()
                    .map { intent.clipData!!.getItemAt(it).uri }
                    .toList()
                function(uris as MutableList<Uri>)
            } else if (intent.data != null) {
                function(arrayListOf(intent.data!!))
            }
        }
        context.startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_MULTIPLE_IMAGE)
    }

    fun openCameraForBitmap(function: (Bitmap) -> Unit) {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        context.resultLife.onActivityResult { requestCode, resultCode, intent ->
            if (requestCode != CAMERA_REQUEST
                || resultCode != Activity.RESULT_OK
                || intent == null
            ) return@onActivityResult
            val bundle = intent.extras
            bundle?.apply {
                val bitmap = get("data") as Bitmap
                function(bitmap)
            }
        }
        context.startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    fun openCameraForImage(function: (Uri) -> Unit) {
        val imageURI = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        })
        context.resultLife.onActivityResult { requestCode, resultCode, intent ->
            if (requestCode != CAMERA_REQUEST
                || resultCode != Activity.RESULT_OK
            ) return@onActivityResult
            try {
//                val thumbnail = MediaStore.Images.Media.getBitmap(context.contentResolver, imageURI)
//                val url = getRealPathFromURI(imageURI!!)
                function(imageURI!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        context.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
        }, CAMERA_REQUEST)
    }

//    fun openPlaceAutoComplete(placeOriginal: String, function: (LatLng) -> Unit) {
//        var statusCode = -1
//        try {
//            context.startActivityForResult(PlaceAutocomplete
//                .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                .setFilter(AutocompleteFilter.Builder()
//                    .setCountry("VN")
//                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
//                    .build())
//                .zzh(placeOriginal).zzg(1)
//                .build(context), RC_OPEN_PLACE_AUTO_COMPLETE)
//        } catch (exception: GooglePlayServicesRepairableException) {
//            statusCode = exception.connectionStatusCode
//        } catch (exception: GooglePlayServicesNotAvailableException) {
//            statusCode = exception.errorCode
//        }
//        if (statusCode != -1) {
//            GoogleApiAvailability.getInstance().showErrorDialogFragment(context, statusCode, 30422)
//        }
//        context.resultRegistry.onActivityResult { requestCode, resultCode, intent ->
//            if (requestCode != RC_OPEN_PLACE_AUTO_COMPLETE || intent == null) return@onActivityResult
//            if (resultCode == RESULT_OK) {
//                function(PlaceAutocomplete.getPlace(context, intent).latLng)
//            } else if (resultCode == RESULT_ERROR) {
//                Toast.makeText(context, PlaceAutocomplete.getStatus(context, intent).statusMessage,
//                    Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    fun openLocation(function: (Boolean) -> Unit) {
//        if (DriverUtils.isGPSEnabled(context)) {
//            function(true)
//            return
//        }
//        LocationServices.getSettingsClient(context).checkLocationSettings(LocationSettingsRequest.Builder()
//            .setAlwaysShow(true)
//            .addLocationRequest(LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10000)
//                .setFastestInterval((10000 / 2).toLong()))
//            .build())
//            .addOnSuccessListener {
//                context.runOnUiThread {
//                    function(it.locationSettingsStates.isGpsUsable
//                            || it.locationSettingsStates.isLocationUsable
//                            || it.locationSettingsStates.isNetworkLocationUsable)
//                }
//            }.addOnFailureListener {
//                val statusCode = (it as ApiException).statusCode
//                when (statusCode) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> openGpsEnableDialog(it, function)
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> openGpsEnableSetting()
//                    else -> context.runOnUiThread { function(false) }
//                }
//            }.addOnCanceledListener { context.runOnUiThread { function(false) } }
//        context.resultLife.onActivityResult { requestCode, resultCode, _ ->
//            if (requestCode == REQUEST_CHECK_SETTINGS) {
//                function(resultCode == Activity.RESULT_OK)
//            } else if (requestCode == REQUEST_ENABLE_GPS) {
//                function(DriverUtils.isGPSEnabled(context))
//            }
//        }
//    }
//
//    private fun openGpsEnableDialog(it: ApiException, function: (Boolean) -> Unit) {
//        try {
//            it as ResolvableApiException
//            it.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
//        } catch (sie: IntentSender.SendIntentException) {
//            context.runOnUiThread { function(false) }
//        }
//    }
//
//    private fun openGpsEnableSetting() {
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        context.startActivityForResult(intent, REQUEST_ENABLE_GPS)
//    }


    fun openGalleryForVideo(any: Any) {

    }
}

