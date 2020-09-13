package com.dz25.epicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.dz25.epicapp.Models.claimModel
import com.dz25.epicapp.Utils.mUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_claim.*
import kotlinx.android.synthetic.main.activity_report.btn_finish
import kotlinx.android.synthetic.main.activity_report.btn_try_again
import kotlinx.android.synthetic.main.activity_report.image_loading_result
import kotlinx.android.synthetic.main.activity_report.mProgressBar
import kotlinx.android.synthetic.main.activity_report.progress_layout
import kotlinx.android.synthetic.main.activity_report.tv_loading
import kotlinx.android.synthetic.main.header.*
import java.text.SimpleDateFormat
import java.util.*

class ClaimActivity : AppCompatActivity() {

    private lateinit var mRef :DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim)

        mRef=FirebaseDatabase.getInstance().reference
        mUtils(this)
            .Config_Header(header_title,header_btn_back,"Ajouter une réclamation")

        val report_array= resources.getStringArray (R.array.report_types_array)
        val adapter=  ArrayAdapter<String> (this,R.layout.spinner_item,report_array)
        adapter.setDropDownViewResource (android.R.layout.simple_dropdown_item_1line)
        report_spinner.adapter = adapter

        btn_report_send.setOnClickListener {
            if (edit_report_name.text.isEmpty()){
                edit_report_name.setError("S'il vous plaît entrez votre nom")
            }
            else if(edit_report_num.text.isEmpty()){
                edit_report_num.setError("Veuillez entrer votre numéro de téléphone")
            }
            else if(edit_report_text.text.isEmpty()){
                edit_report_text.setError("Veuillez entrer votre réclamation")
            }
            else{
                sendDataToServer()
            }
        }
    }

    fun sendDataToServer(){
        startLoadingLayout("loading")
        val mKey = mRef.child("Claims").push()
        mKey.setValue(
            claimModel(
                mKey.key.toString(),
                edit_report_name.text.toString(),
                edit_report_num.text.toString(),
                edit_report_text.text.toString(),
                report_spinner.selectedItem.toString(),
                "AinSmara",
                System.currentTimeMillis().toString(),
                SimpleDateFormat("dd|MM|yyyy").format(Date())
            )
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    startLoadingLayout("success")
                }else{
                    startLoadingLayout("failed")
                }
            }
    }

    fun startLoadingLayout(type:String){
        progress_layout.visibility = View.VISIBLE
        btn_try_again.setOnClickListener {
            ButtonsState(true)
            progress_layout.visibility = View.GONE
        }
        btn_finish.setOnClickListener { finish() }

        if (type.equals("loading")){
            ButtonsState(false)
            mProgressBar.visibility = View.VISIBLE
            tv_loading.text = "Le processus est en cours, veuillez patienter..."
            image_loading_result.visibility = View.GONE
            btn_try_again.visibility = View.GONE
            btn_finish.visibility = View.GONE
        }
        else if(type.equals("success")){
            mProgressBar.visibility = View.GONE
            tv_loading.text = "Merci pour votre réclamation."
            image_loading_result.visibility = View.VISIBLE
            image_loading_result.setBackgroundResource(R.drawable.ic_correct)
            btn_try_again.visibility = View.GONE
            btn_finish.visibility = View.VISIBLE
        }
        else if(type.equals("failed")){
            mProgressBar.visibility = View.GONE
            tv_loading.text = "Réclamation non enregistré, Veulliez verifier votre connection internet."
            image_loading_result.visibility = View.VISIBLE
            image_loading_result.setBackgroundResource(R.drawable.failed)
            btn_try_again.visibility = View.VISIBLE
            btn_finish.visibility = View.VISIBLE
        }
    }

    fun ButtonsState(state:Boolean){
        edit_report_name.isEnabled = state
        edit_report_num.isEnabled = state
        edit_report_text.isEnabled = state
        btn_report_send.isEnabled = state
        report_spinner.isEnabled = state
    }



}