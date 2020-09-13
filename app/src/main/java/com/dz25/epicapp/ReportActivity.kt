package com.dz25.epicapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.dz25.epicapp.Models.addModel
import com.dz25.epicapp.Utils.mUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.header.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ReportActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    var locationManager: LocationManager? = null
    private lateinit var storageRef: StorageReference;
    private lateinit var mRef: DatabaseReference
    val images: ArrayList<Image>? = null
    var finalLatitude = "Nope"
    var finalLongitude = "Nope"
    val df2 = SimpleDateFormat("dd|MM|yyyy")
    var todayDate = ""
    var finalPath = ""
    var finalType = ""

    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var result: PendingResult<LocationSettingsResult>? = null
    val REQUEST_LOCATION = 199
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        FirebaseApp.initializeApp(this)

        mUtils(this)
            .Config_Header(header_title, header_btn_back, "Signalé un déchet")

        storageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://epic-app-a5676.appspot.com")
            .child("AinSmara")

        mRef = FirebaseDatabase.getInstance().reference.child("Reports")


        btn_get_auto_address.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
                return@setOnClickListener
            }
            if (isLocationEnabled(this)){
                getLocation()
            }
            else{
                turnOnGps()
            }
        }

        btn_send.setOnClickListener { startSending() }


        btn_addImg.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setRootDirectoryName(Config.ROOT_DIR_DCIM)
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("Vous ne pouvez sélectionner qu'une seule image")
                .setSelectedImages(images)
                .setRequestCode(100)
                .start();
        }

        /*
        mradio_group.setOnPositionChangedListener(object :
            RadioRealButtonGroup.OnPositionChangedListener {
            override fun onPositionChanged(
                button: RadioRealButton?,
                currentPosition: Int,
                lastPosition: Int
            ) {
                finalType = button!!.text
            }

        })

         */


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults[0].equals(PackageManager.PERMISSION_GRANTED)){
                    if (isLocationEnabled(this)){
                        getLocation()
                    }
                    else{
                        turnOnGps()
                    }
                }else{
                    Toast.makeText(this,"Error: You have to give the need permession to this application",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun startSending() {
        if (edit_address.text.isEmpty()) {
            edit_address.setError("Veuillez saisir l'adresse")
        } else if (finalType.equals("")) {
            Snackbar.make(
                findViewById(R.id.header_btn_back),
                "Veuillez choisir le type de déchets",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        } else {
            ButtonsState(false)
            startLoadingLayout("loading")
            todayDate = df2.format(Date())
            if (finalPath != "") {
                upImageToServer()
            } else {
                upDataToServer("")
            }
        }
    }

    fun getFinalCoords(): HashMap<String, String> {
        var map = HashMap<String, String>()
        map.put("latitude", finalLatitude)
        map.put("longitude", finalLongitude)
        /*
        if (switch_sendLocation.isChecked) {
            map.put("latitude", finalLatitude)
            map.put("longitude", finalLongitude)
            return map
        } else {
            map.put("latitude", "nope")
            map.put("longitude", "nope")
            return map
        }

         */
        return map
    }

    fun DechetTypeClick(view:View){
        layout_dechet_encom.setBackgroundResource(0)
        layout_dechet_verts.setBackgroundResource(0)
        layout_dechet_autre.setBackgroundResource(0)
        when(view.id){
            R.id.layout_dechet_encom->{
                layout_dechet_encom.setBackgroundResource(R.drawable.design_selected)
                 finalType = "Encombrants"
            }

            R.id.layout_dechet_verts->{
                layout_dechet_verts.setBackgroundResource(R.drawable.design_selected)
                finalType = "Verts"
            }

            R.id.layout_dechet_autre->{
                layout_dechet_autre.setBackgroundResource(R.drawable.design_selected)
                finalType = "Autre"
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // The last parameter value of shouldHandleResult() is the value we pass to setRequestCode().
        // If we do not call setRequestCode(), we can ignore the last parameter.

        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 100)) {
            // Do stuff with image's path or id. For example:
            tv_selectedImg.visibility = View.VISIBLE
            btn_addImg.text = "Changer l'image"
            val images: ArrayList<Image> = ImagePicker.getImages(data)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Glide.with(this)
                    .load(images[0].uri)
                    .into(tv_selectedImg)
            } else {
                Glide.with(this)
                    .load(images[0].path)
                    .into(tv_selectedImg)
            }
            finalPath = images[0].path


        }

        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    getLocation()
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(
                        this@ReportActivity,
                        "Please give the needed permessions to this application and try again",
                        Toast.LENGTH_LONG
                    ).show()
                    //turnOnGps()
                }
                else -> {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //TODO CHECK PERMESSIONS

    private fun getAddress(coords: Location) {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        addresses =
            geocoder.getFromLocation(coords.latitude!!.toDouble(), coords.longitude!!.toDouble(), 1)
        val address = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        edit_address.setText(address)
        tv_coords.visibility = View.VISIBLE
        tv_coords.setText("Localisation : ${coords.latitude}, ${coords.longitude}")
    }


    private fun getLocation() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val last_known = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val locationLS = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude.toString()
                val longitude = location.longitude.toString()
                finalLatitude = latitude
                finalLongitude = longitude
                //Toast.makeText(this@MainActivity,"LOCATION CHANGED",Toast.LENGTH_LONG).show()
                getAddress(location)
                Log.e("mLocation", "Lat ${location.latitude} Long ${location.longitude}")
                //sendDataToServe(latitude, longitude)

                //String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                //Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
/*
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10,
                5f, locationLS
            )

 */

        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            10,
            5f, locationLS
        )


    }
/*
    fun sendDataToServe() {
        mRef
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        mRef.setValue("mUtils.getAllData()")
                        val map = HashMap<String, String>()
                        map.clear()
                        map["latitude"] = latitude
                        map["longitude"] = longitude
                        mRef.child("Locations").child("mUtils.getDateForPath()").setValue(map)
                            .addOnCompleteListener { task ->
                                // showSuccessfull()
                                //showSuccessfull()
                            }

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //showSuccessfull()
                }
            })
    }

 */

    fun upImageToServer() {
        try {
            val df = SimpleDateFormat("ddMMyyHHmmSS")
            val path = "image_" + "." + df.format(Date()) + ".jpg"
            val imgRef = storageRef.child("$todayDate/$path")

            val compressedpictureFile = Compressor(this).compressToFile(File(finalPath))
            val picFinalPath = compressedpictureFile.path
            val chosenFile = Uri.fromFile(File(picFinalPath))

            val up = imgRef.putFile(chosenFile)

            up.addOnFailureListener {
                Snackbar.make(
                    findViewById(R.id.header_btn_back),
                    it.message.toString(),
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                startLoadingLayout("failed")
                // mDialog.dismiss()
            }


            val urlTask =
                up.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation imgRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadURL = (task.result).toString()
                        upDataToServer(downloadURL)
                    } else {
                        startLoadingLayout("failed")
                    }

                }
        } catch (e: Exception) {
        }

    }

    fun upDataToServer(picUrl: String) {
        var mKey = mRef.push()
        mKey.setValue(
            addModel(
                mKey.key.toString(),
                getFinalCoords()["latitude"]!!,
                getFinalCoords()["longitude"]!!,
                edit_address.text.toString(),
                edit_note.text.toString(),
                finalType,
                picUrl,
                todayDate,
                System.currentTimeMillis().toString(),
                "AinSmara",
                "false"
            )
        )
            .addOnCompleteListener { task ->
                // ButtonsState(true)
                if (task.isSuccessful) {
                    startLoadingLayout("success")
                } else {
                    startLoadingLayout("failed")
                }
            }

    }

    fun startLoadingLayout(type: String) {
        progress_layout.visibility = View.VISIBLE
        btn_try_again.setOnClickListener {
            ButtonsState(true)
            progress_layout.visibility = View.GONE
        }
        btn_finish.setOnClickListener { finish() }

        if (type.equals("loading")) {
            mProgressBar.visibility = View.VISIBLE
            tv_loading.text = "Le processus est en cours, veuillez patienter..."
            image_loading_result.visibility = View.GONE
            btn_try_again.visibility = View.GONE
            btn_finish.visibility = View.GONE
        } else if (type.equals("success")) {
            mProgressBar.visibility = View.GONE
            tv_loading.text = "Signal enregistré."
            image_loading_result.visibility = View.VISIBLE
            image_loading_result.setBackgroundResource(R.drawable.ic_correct)
            btn_try_again.visibility = View.GONE
            btn_finish.visibility = View.VISIBLE
        } else if (type.equals("failed")) {
            mProgressBar.visibility = View.GONE
            tv_loading.text = "Signal non enregistré, Veulliez verifier votre connection internet."
            image_loading_result.visibility = View.VISIBLE
            image_loading_result.setBackgroundResource(R.drawable.failed)
            btn_try_again.visibility = View.VISIBLE
            btn_finish.visibility = View.VISIBLE
        }


    }

    fun ButtonsState(state: Boolean) {
        edit_address.isEnabled = state
        edit_note.isEnabled = state
        btn_addImg.isEnabled = state
        btn_send.isEnabled = state
        btn_get_auto_address.isEnabled = state
        layout_dechet_encom.isEnabled = state
        layout_dechet_verts.isEnabled = state
        layout_dechet_autre.isEnabled = state
        switch_sendLocation.isEnabled = state
    }


    fun turnOnGps(){
        mGoogleApiClient =  GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build();
        mGoogleApiClient!!.connect();
    }

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE
                )

            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF

        } else {
            locationProviders = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            return !TextUtils.isEmpty(locationProviders)
        }

    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setInterval((30 * 1000).toLong())
        mLocationRequest!!.setFastestInterval((5 * 1000).toLong())

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)

        result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())

        result!!.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(p0: LocationSettingsResult) {
                var  status: Status = p0.status;
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS-> {
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED-> {
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                this@ReportActivity ,
                                REQUEST_LOCATION
                            );
                        } catch ( e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE-> {

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                    }
                }
            }


        })
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}