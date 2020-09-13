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
import com.google.firebase.database.FirebaseDatabase
import com.sharingposts20.dzhealthmanager.getTimeAgo
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class mRrportsAdapter(private val mList: ArrayList<addModel>, private val mCtx:Context) : RecyclerView.Adapter<mRrportsAdapter.MyViewHolder>() {

    private var mRef = FirebaseDatabase.getInstance().reference

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v

            var tvImg = view.findViewById<ImageView>(R.id.tvIMG)
            var tvTimeAgo = view.findViewById<TextView>(R.id.tvTimeAgo)
            var tvDate = view.findViewById<TextView>(R.id.tvDate)
            var tvAddress = view.findViewById<TextView>(R.id.tvAddress)
            var tvCoords = view.findViewById<TextView>(R.id.tvCoords)
            var tvType= view.findViewById<TextView>(R.id.tvType)
            var tvNote = view.findViewById<TextView>(R.id.tvNote)
            var btnMaps = view.findViewById<ImageButton>(R.id.tvBtnMaps)
            var mLayout = view.findViewById<LinearLayout>(R.id.mLayout)
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
                .inflate(R.layout.report_design, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            var item = mList[position]
            holder.tvTimeAgo.setText(getTimeAgo(item.millis!!.toLong()))
            holder.tvAddress.setText(item.address)
            holder.tvType.setText(item.type)
            if (!item.latitude.equals("Nope")){
                holder.tvCoords.setText("Localisation : ${item.latitude} , ${item.longitude}")
            }else{
                holder.tvCoords.setText("Localisation : /")
            }
            if (item.note.equals("")){
                holder.tvNote.setText("/")
            }else{
                holder.tvNote.setText(item.note)
            }

            holder.tvDate.setText(item.date!!.replace("|","/"))

            if(item.pic.equals("")){
                holder.tvImg.setBackgroundResource(R.drawable.logonew)
            }else{
                val mTarget = object :Target{
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        holder.tvImg.setBackgroundResource(R.drawable.ic_001_loading)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        holder.tvImg.setBackgroundResource(R.drawable.logonew)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        holder.tvImg.background = BitmapDrawable(mCtx.resources,bitmap)
                    }
                }
                Picasso.get().load(item.pic).into(mTarget)
            }



            holder.tvImg.setOnClickListener {
                if (!item.pic.equals("")){
                    val fullIMG = (mCtx as Activity).findViewById<ImageView>(R.id.mImgTV)
                    val fullBlack = (mCtx as Activity).findViewById<LinearLayout>(R.id.blackLayout)
                    fullIMG.visibility = View.VISIBLE
                    fullBlack.visibility = View.VISIBLE
                    fullIMG.setOnClickListener {
                        fullIMG.visibility = View.GONE
                        fullBlack.visibility = View.GONE
                    }

                    fullBlack.setOnClickListener {
                        fullIMG.visibility = View.GONE
                        fullBlack.visibility = View.GONE
                    }
                    val mTVTarget = object :Target{
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            fullIMG.setBackgroundResource(R.drawable.logonew)
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            fullIMG.setBackgroundResource(R.drawable.logonew)
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            fullIMG.background= BitmapDrawable(mCtx.resources,bitmap)
                        }
                    }
                    Picasso.get().load(item.pic).into(mTVTarget)
                }

            }

            holder.btnMaps.setOnClickListener {
                val my_data = java.lang.String.format(
                    Locale.ENGLISH,
                    "http://maps.google.com/maps?daddr=${item.latitude},${item.longitude}(Location)"
                )

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(my_data))
                intent.setPackage("com.google.android.apps.maps")
                mCtx!!.startActivity(intent)
            }

            holder.mLayout.setOnLongClickListener {
                openAdminDialog(item,position)
                return@setOnLongClickListener true
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = mList.size




    @SuppressLint("SetTextI18n")
    fun openAdminDialog(item:addModel,pos:Int){
        var setDraft = "false"
    val builder = AlertDialog.Builder(mCtx)
        val view = (mCtx as Activity).layoutInflater.inflate(R.layout.control_panel_reports,null)
        val btnRemove = view.findViewById<Button>(R.id.control_panel_RemoveBtn)
        val btnDraft = view.findViewById<Button>(R.id.control_panel_DraftBtn)
        val mProgress = view.findViewById<ProgressBar>(R.id.control_panel_progressBar)
        if (item.draft.equals("true")){
            btnDraft.setText("transformé au signale")
            setDraft = "false"
        }else{
            btnDraft.setText("ajouter au brouillon")
            setDraft = "true"
        }

         builder.setView(view)
        val dialog = builder.create()
        btnRemove.setOnClickListener {
            viewsState(btnRemove,btnDraft,mProgress,false)
            mRef.child("Reports").child(item.id!!).removeValue().addOnCompleteListener { task ->
                viewsState(btnRemove,btnDraft,mProgress,true)
                dialog.dismiss()
                if (task.isSuccessful){
                    mList.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyDataSetChanged()
                    Toast.makeText(mCtx,"Ce signal a été supprimé avec succès",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(mCtx,"Something went wrong :"+task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }

        btnDraft.setOnClickListener {
            viewsState(btnRemove,btnDraft,mProgress,false)
            mRef.child("Reports").child(item.id!!).child("draft").setValue(setDraft).addOnCompleteListener { task ->
                viewsState(btnRemove,btnDraft,mProgress,true)
                dialog.dismiss()
                if (task.isSuccessful){
                    mList.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyDataSetChanged()
                    Toast.makeText(mCtx,"Operation terminé avec succès",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(mCtx,"Something went wrong : "+task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
        dialog.show()
    }

    fun viewsState(btnRemove:Button,btnDraft:Button,mProgress:ProgressBar,state:Boolean){
        if (state){
            mProgress.visibility = View.GONE
        }
        else{
            mProgress.visibility = View.VISIBLE
        }
        btnDraft.isEnabled = state
        btnRemove.isEnabled = state

    }


    }