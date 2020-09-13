package com.dz25.epicapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dz25.epicapp.Utils.mUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init();


        val mUtils = mUtils(this)

        btnAdmin.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser !=null){
                startActivity(Intent(this,AdminPanel::class.java))
            }else{
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
        imageView_header.setOnClickListener { startActivity(Intent(this,InformationActivity::class.java).putExtra("data","INTRO")) }

        btn_4.setOnClickListener { startActivity(Intent(this,InformationActivity::class.java).putExtra("data","TIME")) }

        btn_3.setOnClickListener { startActivity(Intent(this,InformationActivity::class.java).putExtra("data","COMPAGNE")) }

        imageView_header.setOnClickListener { startActivity(Intent(this,InformationActivity::class.java).putExtra("data","INTRO")) }



        btn_report.setOnClickListener { startActivity(Intent(this,ReportActivity::class.java)) }

        btn_2.setOnClickListener { startActivity(Intent(this,ClaimActivity::class.java))}

        btn_5.setOnClickListener {mUtils.openDialogConv() }

        btn_gps.setOnClickListener {
            mUtils.openMapsPos()
        }

        btn_gmail.setOnClickListener {
            mUtils.openGmailSend()
        }

        btn_phone.setOnClickListener {
            mUtils.openDialCall()
        }

        btn_facebook.setOnClickListener {
            mUtils.openFacebookPage()
        }

    }

    override fun onStart() {
        super.onStart()
        checkUpdate()
    }

    fun checkUpdate(){
        try {
            FirebaseApp.initializeApp(this)
            FirebaseDatabase.getInstance().reference.child("Update").addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try{
                        val version = p0.child("version").value!! as Int
                        val isUpdated = p0.child("isupdate").value!! as Boolean
                        if (isUpdated.equals(true) && BuildConfig.VERSION_CODE > version){
                            updateDialog()
                        }
                    }catch (e:Exception){}

                }

            })
        }catch (e:Exception){}

    }

    fun updateDialog(){
        var builder = AlertDialog.Builder(this)
        builder.setMessage("Nouvelle mise à jour disponible")
        builder.setPositiveButton("Télécharger"){ dialog, which ->
            val appPackageName = packageName
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName")
            )
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(marketIntent)
        }
        builder.setCancelable(false)
        builder.create().show()
    }







}