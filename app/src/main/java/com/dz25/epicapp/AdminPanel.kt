package com.dz25.epicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dz25.epicapp.Utils.mUtils
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_panel.*
import kotlinx.android.synthetic.main.header.*

class AdminPanel : AppCompatActivity() {
    lateinit var mRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        mRef = FirebaseDatabase.getInstance().reference
        mUtils(this).Config_Header(header_title, header_btn_back, "Panneau d'administration")
        admin_btnReports.isEnabled = false
        admin_btnDrafts.isEnabled = false


        admin_btnClaims.isEnabled = false
        getDataCount()

        admin_btnClaims.setOnClickListener { startActivity(Intent(this,ShowData::class.java).putExtra("mType","claims")) }
        admin_btnReports.setOnClickListener { startActivity(Intent(this,ShowData::class.java).putExtra("mType","false")) }
        admin_btnDrafts.setOnClickListener { startActivity(Intent(this,ShowData::class.java).putExtra("mType","true"))  }



    }








    fun getDataCount(){
        mRef.child("Reports").orderByChild("draft").equalTo("false").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    admin_btnReports.isEnabled = true
                    tv_signales.setText(p0.children.count().toString())
                }else{
                    tv_signales.setText("0")
                }
            }
        })

        mRef.child("Reports").orderByChild("draft").equalTo("true").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    admin_btnDrafts.isEnabled = true
                    tv_drafts.setText(p0.children.count().toString())
                }else{
                    tv_drafts.setText("0")
                }
            }
        })

        mRef.child("Claims").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    admin_btnClaims.isEnabled = true
                    tv_clamis.setText(p0.children.count().toString())
                }else{
                    tv_clamis.setText("0")
                }
            }
        })





    }
}