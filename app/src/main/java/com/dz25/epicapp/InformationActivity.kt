package com.dz25.epicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dz25.epicapp.Utils.mUtils
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_information.btn_facebook
import kotlinx.android.synthetic.main.activity_information.btn_gmail
import kotlinx.android.synthetic.main.activity_information.btn_gps
import kotlinx.android.synthetic.main.activity_information.btn_phone
import kotlinx.android.synthetic.main.header.*

class InformationActivity : AppCompatActivity() {
    lateinit var mUtils : mUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)


         mUtils = mUtils(this)

        setPage()

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

    fun setPage(){
        val data = intent.extras
        when(data!!.getString("data")){
            "INTRO"->{
                introduction.visibility = View.VISIBLE
                mUtils.Config_Header(header_title, header_btn_back, "Introduction sur L'EPIC")

            }

            "TIME"->{
                Les_horraires.visibility = View.VISIBLE
                mUtils.Config_Header(header_title, header_btn_back, "Les Horraires  de la collecte")
            }

            "COMPAGNE"->{
                layout_compagne.visibility = View.VISIBLE
                mUtils.Config_Header(header_title, header_btn_back, "Compagne de nettoyage")
                socialMedia()
            }
        }
    }

    fun IntroductionImagesClick(view:View){
        layout_intro_img1.setBackgroundResource(0)
        layout_intro_img2.setBackgroundResource(0)
        layout_intro_img3.setBackgroundResource(0)
        layout_intro_img4.setBackgroundResource(0)
        var selectedImg = 0
        when(view.id){

            R.id.layout_intro_img1 ->{
                layout_intro_img1.setBackgroundResource(R.drawable.design_selected2)
                selectedImg=R.drawable.intro_img1
            }

            R.id.layout_intro_img2 ->{
                layout_intro_img2.setBackgroundResource(R.drawable.design_selected2)
                selectedImg=R.drawable.intro_img2
            }

            R.id.layout_intro_img3 ->{
                layout_intro_img3.setBackgroundResource(R.drawable.design_selected2)
                selectedImg=R.drawable.intro_img3
            }

            R.id.layout_intro_img4 ->{
                layout_intro_img4.setBackgroundResource(R.drawable.design_selected2)
                selectedImg=R.drawable.intro_img4
            }
        }
        tv_selected_introImg.setBackgroundResource(selectedImg)
    }

    fun socialMedia(){
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
}