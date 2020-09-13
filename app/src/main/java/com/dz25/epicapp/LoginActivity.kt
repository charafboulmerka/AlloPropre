package com.dz25.epicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dz25.epicapp.Utils.mUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.header.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mUtils(this)
            .Config_Header(header_title, header_btn_back, "Panneau d'administration")
        mAuth = FirebaseAuth.getInstance()
        btn_login.setOnClickListener {
            if (edit_login_name.text.isEmpty()){
                edit_login_name.setError("Veuillez vous assurer de saisir votre email")
            }else if (edit_login_pass.text.isEmpty()){
                edit_login_pass.setError("Veuillez vous assurer de saisir votre mot de pass")
            }
            else{
                ViewsState(false)
                mAuth.signInWithEmailAndPassword(edit_login_name.text.toString(),edit_login_pass.text.toString())
                    .addOnCompleteListener { task ->
                        ViewsState(true)
                        if (task.isSuccessful){
                            startActivity(Intent(this,AdminPanel::class.java))
                            finish()
                        }else{
                            Snackbar.make(
                                findViewById(R.id.header_btn_back),
                                "Erreur: veuillez vous assurer que vos données sont correctes et réessayer",
                                Snackbar.LENGTH_LONG
                            ).setAction("Action", null).show()
                        }
                    }
            }
        }
    }


    fun ViewsState(state:Boolean){
        if (state.equals(false)){
            loginProgressBar.visibility = View.VISIBLE
        }else{
            loginProgressBar.visibility = View.GONE
        }
        edit_login_name.isEnabled = state
        edit_login_pass.isEnabled = state
        btn_login.isEnabled = state
        header_btn_back.isEnabled = state
    }
}