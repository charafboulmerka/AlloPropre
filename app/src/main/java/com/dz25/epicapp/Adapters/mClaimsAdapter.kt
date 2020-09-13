package com.dz25.epicapp.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.dz25.epicapp.R
import com.dz25.epicapp.Models.addModel
import com.dz25.epicapp.Models.claimModel
import com.google.firebase.database.FirebaseDatabase
import com.sharingposts20.dzhealthmanager.getTimeAgo
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class mClaimsAdapter(private val mList: ArrayList<claimModel>, private val mCtx:Context) : RecyclerView.Adapter<mClaimsAdapter.MyViewHolder>() {

        private var mRef = FirebaseDatabase.getInstance().reference

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v

            var tvTimeAgo = view.findViewById<TextView>(R.id.claim_tvTimeAgo)
            var tvDate = view.findViewById<TextView>(R.id.claim_tvDate)
            var tvName = view.findViewById<TextView>(R.id.claim_tvName)
            var tvNum = view.findViewById<TextView>(R.id.claim_tvNum)
            var tvType= view.findViewById<TextView>(R.id.claim_tvType)
            var tvClaim = view.findViewById<TextView>(R.id.claim_tvClaim)
            var btnCall = view.findViewById<ImageButton>(R.id.claim_tvBtnCall)
            var mLayout = view.findViewById<LinearLayout>(R.id.claim_mLayout)
        //3
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }


    }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyViewHolder {
            // create a new view
            val view =  LayoutInflater.from(parent.context)
                .inflate(R.layout.claims_design, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            var item = mList[position]
            holder.tvTimeAgo.setText(getTimeAgo(item.millis!!.toLong()))
            holder.tvDate.setText(item.date!!.replace("|","/"))

            holder.tvName.setText(item.username)
            holder.tvType.setText("type : "+item.userclaimtype)
            holder.tvNum.setText(item.userphone)
            holder.tvClaim.setText(item.usernote)


            holder.btnCall.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_DIAL // Action for what intent called for
                intent.data = Uri.parse("tel: ${item.userphone}") // Data with intent respective action on intent
                mCtx.startActivity(intent)
            }

            holder.mLayout.setOnLongClickListener {
                openAdminDialog(item,position)
                return@setOnLongClickListener true
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = mList.size


    @SuppressLint("SetTextI18n")
    fun openAdminDialog(item:claimModel,pos:Int){
        val builder = AlertDialog.Builder(mCtx)
        val view = (mCtx as Activity).layoutInflater.inflate(R.layout.control_panel_reports,null)
        val btnRemove = view.findViewById<Button>(R.id.control_panel_RemoveBtn)
        val btnDraft = view.findViewById<Button>(R.id.control_panel_DraftBtn)
        val mProgress = view.findViewById<ProgressBar>(R.id.control_panel_progressBar)
        btnDraft.visibility = View.GONE

        builder.setView(view)
        val dialog = builder.create()
        btnRemove.setOnClickListener {
            viewsState(btnRemove,mProgress,false)
            mRef.child("Claims").child(item.id!!).removeValue().addOnCompleteListener { task ->
                viewsState(btnRemove,mProgress,true)
                dialog.dismiss()
                if (task.isSuccessful){
                    mList.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyDataSetChanged()

                    Toast.makeText(mCtx,"operation terminé avec succès",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(mCtx,"Something went wrong :"+task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }


        dialog.show()
    }

    fun viewsState(btnRemove:Button,mProgress:ProgressBar,state:Boolean){
        if (state){
            mProgress.visibility = View.GONE
        }
        else{
            mProgress.visibility = View.VISIBLE
        }
        //btnDraft.isEnabled = state
        btnRemove.isEnabled = state

    }


    }