package com.example.check_camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Part
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val name ="lucky roy"
    val mobile = "99999999"
    val email = "lucky@gmail.com"
    val datee = "2020-10-10 10:10:10"
    val token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjE1MDUiLCJuYW1lIjoiU2hvcCBVc2VyIiwibW9iaWxlIjoiOTk5OTk5OTk5OSIsImNyZWF0ZWRfYXQiOiIyMDIxLTA1LTIxIDE1OjM5OjA3IiwidXBkYXRlZF9hdCI6IjIwMjEtMDUtMjEgMTU6Mzk6MDciLCJ0aW1lIjoxNjI3OTAyMDM4fQ.zo_YfBHZe8J6a_OeoR5DLxVvjgdEEV_I60ReoUCBXRI"
    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null
    lateinit var imageview:ImageView
    lateinit var upload : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image_button = findViewById<Button>(R.id.capture)
        upload = findViewById(R.id.upload)
        upload.setOnClickListener{
            sendimage()
        }
         imageview = findViewById<ImageView>(R.id.imageview)
        image_button.setOnClickListener {
            captureImage()
        }
    }
    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this,
                            "com.example.check_camera.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")
            }
        }

    }

    private fun sendimage() {

        val getting_name : RequestBody = RequestBody.create(MediaType.parse("text/plain"),name)
        val gett_mobile : RequestBody = RequestBody.create(MediaType.parse("text/plain"),mobile)
        val get_email : RequestBody = RequestBody.create(MediaType.parse("text/plain"),email)
      //  val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        var propertyImagePart: MultipartBody.Part? = null
        val propertyImageFile = File(mCurrentPhotoPath)
        val propertyImage: RequestBody = RequestBody.create(MediaType.parse("image/*"), propertyImageFile)
        propertyImagePart =MultipartBody.Part.createFormData("image", propertyImageFile.name, propertyImage)

//        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"),mCurrentPhotoPath)
//        val body = MultipartBody.Part.createFormData("image", mCurrentPhotoPath)

        val get_date : RequestBody = RequestBody.create(MediaType.parse("text/plain"),datee)
        val service = ApiInterface.retrofit.create(ApiInterface::class.java)

        val call= service.getpost(token,getting_name,gett_mobile,get_email,propertyImagePart,get_date)
        call.enqueue(object : retrofit2.Callback<Mymodel>{
            override fun onFailure(call: Call<Mymodel>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.localizedMessage,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Mymodel>, response: Response<Mymodel>) {
//               if (response.body().message)
                Toast.makeText(this@MainActivity, response.body()?.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })
//        call.enqueue( object : Callback<Mymodel>{
//            override fun onResponse(call: Call<Mymodel>?, response: Response<Mymodel>?) {
//
//                if(response?.body() != null)
//                    recyclerAdapter.setMovieListItems(response.body()!!)
//            }
//
//            override fun onFailure(call: Call<Mymodel>?, t: Throwable?) {
//
//            }
//        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            imageview.setImageBitmap(myBitmap)
        } else {
            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }

}
