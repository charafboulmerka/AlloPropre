package com.dz25.epicapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dz25.epicapp.Adapters.mClaimsAdapter
import com.dz25.epicapp.Adapters.mRrportsAdapter
import com.dz25.epicapp.Models.addModel
import com.dz25.epicapp.Models.claimModel
import com.dz25.epicapp.Utils.mUtils
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.header.*

class ShowData : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mReports = ArrayList<addModel>()
    private var mClaims = ArrayList<claimModel>()
    private lateinit var mRef:DatabaseReference
    private var finalType = "false"
    private lateinit var mPr_rec:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)
        mPr_rec=findViewById(R.id.progress_rec_waiting_reports)
        var it = intent.extras
        mRef=FirebaseDatabase.getInstance().reference
        viewManager = LinearLayoutManager(this)
        var headerTitle = "Signales"
        if (it!!.getString("mType").equals("true")){
            finalType = "true"
            headerTitle = "Brouillons"
            getReports()
        }else if (it.getString("mType").equals("false")){
            finalType = "false"
            headerTitle = "Signales"
            getReports()
        }else{
            getClaims()
            headerTitle = "Les Réclamations"
        }
        mUtils(this)
            .Config_Header(header_title, header_btn_back, headerTitle)


    }


    fun getClaims(){
        getClaimsData()

        viewAdapter = mClaimsAdapter(mClaims, this)

        recyclerView = findViewById<RecyclerView>(R.id.mRec).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    fun getReports(){
        getReportsData()

        viewAdapter = mRrportsAdapter(mReports, this)

        recyclerView = findViewById<RecyclerView>(R.id.mRec).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }


    fun getClaimsData(){
        mRef.child("Claims").orderByChild("millis").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    mPr_rec.visibility = View.GONE
                    for (item in p0.children){
                        //  var data = item.getValue(addModel::class.java)
                        val data: claimModel = item.getValue(claimModel::class.java)!!

                            mClaims.add(data)
                            viewAdapter.notifyDataSetChanged()

                    }
                    mClaims.reverse()
                }else{
                    mPr_rec.visibility = View.GONE
                    Toast.makeText(this@ShowData,"There is no data on database so far",Toast.LENGTH_LONG).show()
                }

            }

        })
    }

    fun getReportsData(){
        mRef.child("Reports").orderByChild("millis").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    mPr_rec.visibility = View.GONE
                    for (item in p0.children){
                        //  var data = item.getValue(addModel::class.java)
                        val data: addModel = item.getValue(addModel::class.java)!!

                        if (data.draft.equals(finalType)){
                            mReports.add(data)
                            viewAdapter.notifyDataSetChanged()
                        }
                    }
                    mReports.reverse()
                }else{
                    mPr_rec.visibility = View.GONE
                    Toast.makeText(this@ShowData,"There is no data on database so far",Toast.LENGTH_LONG).show()
                }

            }

        })
    }
}