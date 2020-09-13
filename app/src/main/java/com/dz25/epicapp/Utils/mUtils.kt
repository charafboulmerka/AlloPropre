package com.dz25.epicapp.Utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.util.*

class mUtils {
    var mCtx:Context?=null
    constructor(mCtx:Context){
        this.mCtx=mCtx
    }

    fun Config_Header(header_txt:TextView,btn_back:ImageButton,label:String){
      header_txt.setText(label)
        btn_back.setOnClickListener { (mCtx as Activity).finish() }
    }

    fun openMapsPos(){
        val my_data = java.lang.String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?daddr=36.2692658,6.4969576(EPAS)"
        )

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(my_data))
        intent.setPackage("com.google.android.apps.maps")
        mCtx!!.startActivity(intent)
    }

    fun openGmailSend(){
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.parse("mailto:epas.epic@gmail.com")
        email.putExtra(Intent.EXTRA_SUBJECT, "")
        email.putExtra(Intent.EXTRA_TEXT, "")
        mCtx!!.startActivity(email)
    }


    fun openDialCall(){
        val mobileNumber = "031975712"
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL // Action for what intent called for
        intent.data = Uri.parse("tel: $mobileNumber") // Data with intent respective action on intent
        mCtx!!.startActivity(intent)
    }

    fun openFacebookPage(){
        try {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            val facebookUrl = getFacebookPageURL(mCtx!!)
            facebookIntent.data = Uri.parse(facebookUrl)
            mCtx!!.startActivity(facebookIntent)
        } catch (e: Exception) {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(FACEBOOK_URL)
            mCtx!!.startActivity(facebookIntent)
        }
    }

    var FACEBOOK_URL = "https://www.facebook.com/epas.epic"
    var FACEBOOK_PAGE_ID = "100022610171027"

    //method to get the right URL to use in the intent
    fun getFacebookPageURL(context: Context): String? {
        val packageManager: PackageManager = context.packageManager
        return try {
            val versionCode =
                packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=$FACEBOOK_URL"
            } else { //older versions of fb app
                "fb://page/$FACEBOOK_PAGE_ID"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            FACEBOOK_URL //normal web url
        }
    }

    fun openDialogConv(){
        var builder = AlertDialog.Builder(mCtx)
        builder.setMessage("Si vous êtes intéressés par les prestations de l'EPIC (convention), n'hésitez pas à nous contacter")

        builder.setNegativeButton("Fermer") { dialog, which -> }

        builder.create().show()
    }
}