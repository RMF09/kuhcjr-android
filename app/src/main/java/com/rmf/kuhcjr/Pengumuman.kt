package com.rmf.kuhcjr

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.rmf.kuhcjr.Adapter.AdapterRVDataPengumuman
import com.rmf.kuhcjr.Api.ApiClient
import com.rmf.kuhcjr.Api.ApiInterface
import com.rmf.kuhcjr.Data.DataPengumuman
import com.rmf.kuhcjr.Data.GetPengumuman
import com.rmf.kuhcjr.Utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_pengumuman.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class Pengumuman : AppCompatActivity() {
    lateinit var list  : ArrayList<DataPengumuman>
    lateinit var adapter : AdapterRVDataPengumuman
    lateinit var apiInterface : ApiInterface



    //Donwload Lampiran
    lateinit var downloadZipFileTask: DownloadZipFileTask
    var namafile: String? = null
    private val TAG = "BerandaFragment"

    //Dialog Download
    var progressBar: ProgressBar?=null
    lateinit var textProgress: TextView
    lateinit var textHeaderDownload:TextView
    lateinit var textOKDownload: TextView
    lateinit var textKeteranganDownload:TextView
    lateinit var alertDialogDownload: AlertDialog

    //Masalah Jaringan Layout
    lateinit var linearMasalahJaringan: LinearLayout
    lateinit var textERR: TextView
    lateinit var textMuatUlang:TextView

    //Belum Ada data
    lateinit var linearBelumAdaData: LinearLayout
    lateinit var textBelumAdaData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengumuman)

        //        Sistem

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        rv.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false)

        apiInterface =
                ApiClient.getClient()
                .create(ApiInterface::class.java)

        initUI()
        initialDialogDownload()
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101)
        getData()


    }
    fun initUI(){
        //Masalah Jaringan
        linearMasalahJaringan = findViewById<View>(R.id.layout_masalah_jaringan) as LinearLayout
        textERR = findViewById<View>(R.id.text_error_code) as TextView
        textMuatUlang = findViewById<View>(R.id.text_muat_ulang_data) as TextView
        textMuatUlang.setOnClickListener { getData() }

        //Belum Ada Data
        linearBelumAdaData = findViewById<View>(R.id.layout_belum_ada_data) as LinearLayout
        textBelumAdaData = findViewById<View>(R.id.text_belum_ada_data) as TextView

        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing=false
            getData()
        }
    }
    private fun askForPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@Pengumuman, permission)) {
                ActivityCompat.requestPermissions(this@Pengumuman, arrayOf(permission), requestCode)
            } else {
                ActivityCompat.requestPermissions(this@Pengumuman, arrayOf(permission), requestCode)
            }
        } else if (ContextCompat.checkSelfPermission(this@Pengumuman, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this@Pengumuman, "Permission was denied", Toast.LENGTH_SHORT).show()
        }
    }
    fun getData(){
        linearMasalahJaringan.visibility = View.GONE
        linearBelumAdaData.visibility = View.GONE

        progress.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val pengumumanCall = apiInterface.getPengumuman("pengumuman")

        pengumumanCall.enqueue(object : Callback<GetPengumuman>{
            override fun onFailure(call: Call<GetPengumuman>?, t: Throwable?) {
//                Log.e("Retrofit Get", t.toString())

                linearMasalahJaringan.visibility = View.VISIBLE
                textERR.text = "ERR : " + t!!.message
                progress.visibility = View.GONE
                rv.visibility = View.GONE
            }

            override fun onResponse(call: Call<GetPengumuman>?, response: Response<GetPengumuman>?) {
                progress.visibility = View.GONE

                if(response!!.isSuccessful){
                    if(response.body().status.equals("berhasil")){
                        list = response.body().listPengumuman as ArrayList<DataPengumuman>
                        adapter = AdapterRVDataPengumuman(list,this@Pengumuman)
                        rv.adapter =adapter
                        rv.visibility = View.VISIBLE
                    }else{
                        linearBelumAdaData.visibility = View.VISIBLE
                        textBelumAdaData.text = "Belum ada pengumuman"
                    }
                }else{
                    linearMasalahJaringan.visibility = View.VISIBLE
                    textERR.text = "ERR : Terjadi masalah pada DB server"
                    progress.visibility = View.GONE
                }
            }
        })
    }

    //Download FIle
    fun downloadZipFile(namafile: String) {
        alertDialogDownload.show()
        val call = apiInterface.downloadFile("assets/files/pengumuman/$namafile")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {

                    Toast.makeText(this@Pengumuman, "Downloading...", Toast.LENGTH_SHORT).show();
                    downloadZipFileTask = DownloadZipFileTask()
                    downloadZipFileTask.execute(response.body())
                } else {
                    Toast.makeText(this@Pengumuman, "File tidak ada", Toast.LENGTH_SHORT).show()
                    alertDialogDownload.dismiss()
                    textHeaderDownload.setText("Ups Terjadi");
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                t.printStackTrace();
//                Log.e(TAG, t.getMessage());
                alertDialogDownload.dismiss()
                Toast.makeText(this@Pengumuman, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initialDialogDownload() {
        val builder = AlertDialog.Builder(this@Pengumuman)
        val view = layoutInflater.inflate(R.layout.dialog_download, null)
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        textProgress = view.findViewById<View>(R.id.text_progress) as TextView
        textKeteranganDownload = view.findViewById<View>(R.id.text_keterangan_download) as TextView
        textOKDownload = view.findViewById<View>(R.id.ok) as TextView
        textHeaderDownload = view.findViewById<View>(R.id.text_header_download) as TextView
        builder.setView(view)
        alertDialogDownload = builder.create()
        alertDialogDownload.setCancelable(false)
        textOKDownload!!.setOnClickListener {
            alertDialogDownload.dismiss()
            textProgress!!.text = "0"
            textHeaderDownload!!.text = "Mengunduh file, harap menunggu..."
            progressBar!!.progress = 0
            textOKDownload!!.visibility = View.GONE
            textKeteranganDownload!!.visibility = View.GONE
        }
    }

    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    inner class DownloadZipFileTask : AsyncTask<ResponseBody, Pair<Integer, Long>, String>() {
        lateinit var status : String


        override fun onPreExecute() {
            super.onPreExecute()

        }
        override fun doInBackground(vararg params: ResponseBody): String? {
            saveToDisk(params[0])
            return null
        }

        override fun onProgressUpdate(vararg values: Pair<Integer, Long>?) {
            if(values[0]!!.first.toInt() == 100){
                status = "berhasil"
            }
            if(values[0]!!.second> 0){
                val currentProgress = (values[0]!!.first.toDouble() / values[0]!!.second.toDouble()  * 100).toInt()
                progressBar!!.progress = currentProgress
                textProgress.text = "$currentProgress%"
            }
            if (values[0]!!.first.toInt() == -1) {
//                Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
                status = "gagal"
            }
        }
        fun doProgress(progressDetails  : Pair<Integer,Long>){
            publishProgress(progressDetails)

        }

        override fun onPostExecute(result: String?) {
            textKeteranganDownload.visibility = View.VISIBLE
            textOKDownload.visibility = View.VISIBLE
            if (status == "berhasil") {
                textHeaderDownload.text = "File berhasil diunduh"
                textKeteranganDownload.text = "File '$namafile' disimpan di folder Download pada Penyimpanan Internal."
            } else {
                textHeaderDownload.text = "File gagal diunduh"
                textKeteranganDownload.text = "Harap periksa jaringan internet dan ruang bebas penyimpanan."
            }
        }

        fun saveToDisk(responseBody: ResponseBody){
            try {
                var destinationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),namafile)

                var inputStream : InputStream? =null
                var outputStream : OutputStream?=null

                try {
                    inputStream = responseBody.byteStream()
                    outputStream = FileOutputStream(destinationFile)
                    var data = ByteArray(4096)
                    var count : Int
                    var values : Int =0
                    var fileSize : Long = responseBody.contentLength()

                    while (inputStream.read(data).also { count = it } != -1){
                        outputStream.write(data, 0, count)
                        values += count
                        val pairs = Pair<Integer, Long>(values as Integer, fileSize)
                        downloadZipFileTask.doProgress(pairs)
                    }
                    outputStream.flush()
                    val pairs = Pair<Integer, Long>(100 as Integer, 100L)
                    downloadZipFileTask.doProgress(pairs)
                }
                catch (e : IOException){
                    val pairs = Pair<Integer, Long>(-1 as Integer, java.lang.Long.valueOf(-1))
                    downloadZipFileTask.doProgress(pairs)
                }finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            }catch (e : IOException){
//                e.printStackTrace()
//                Log.d(BerandaFragment.TAG, "Failed to save the file!")
                return
            }


        }

    }

    override fun onResume() {
        super.onResume()

        // clear the notification area when the app is opened

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
    }

}