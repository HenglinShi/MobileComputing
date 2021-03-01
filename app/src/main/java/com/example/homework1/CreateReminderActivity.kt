package com.example.homework1

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.homework1.db.AppDatabase
import com.example.homework1.databinding.ActivityCreateReminderBinding
import com.example.homework1.db.ReminderInfo
import com.example.homework1.db.UserInfo
import com.example.homework1.utils.BitMapToString
import com.google.gson.Gson
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.example.homework1.DatePickerFragment

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateReminderBinding
    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_GALLERY = 100
    val DATE_SET = 202
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var reminderImgUrl: String
    lateinit var phto:Bitmap

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_create_reminder)

        //phto = BitmapFactory.decodeResource(getResources(),
        //    R.drawable.ic_account_box);
        binding = ActivityCreateReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnReturn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }


        val c = Calendar.getInstance().time
        var timStr = android.icu.text.SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH).format(c.getTime());
        binding.txtCreatedDate.setText(timStr)

        //binding.reminderImage.setClick
        binding.reminderImageCreate.setOnClickListener{

            val builder = AlertDialog.Builder(this@CreateReminderActivity)
            builder.setTitle("Add image")
                .setNegativeButton("Gallery") { _, _ ->
                    var galleryIntent = Intent(Intent.ACTION_GET_CONTENT)

                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    galleryIntent.setType("image/*")

                    startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY)

                }
                .setPositiveButton("Camera"){ _, _ ->
                    dispatchTakePictureIntent()

                }
                .show()


            return@setOnClickListener
        }
        binding.btnAccept.setOnClickListener {
            // Adding new reminder

            if (binding.txtDate.text.isEmpty()) {

                return@setOnClickListener
            }

            val gson = Gson()
            val json = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE).getString("currentUser", "")

            var currentUser = gson.fromJson(json, UserInfo::class.java)








            // put the new item to the database
            // TODO CREATING THE NEW TASK OBJECT

            val reminder = ReminderInfo(
                null,
                //taskname = binding.txtTaskName.text.toString(),
                //taskdesc = binding.txtTaskDesc.text.toString(),
                //duedate = binding.txtDate.text.toString()
                message = binding.txtTaskDesc.text.toString(),
                creator_id = currentUser.uid,
                location_x = binding.txtLocationX.text.toString().toDouble(),
                location_y = binding.txtLocationY.text.toString().toDouble(),
                reminder_see = false,

                creation_time = binding.txtCreatedDate.text.toString(), //Calendar.getInstance().toString(),
                reminder_time = binding.txtDate.text.toString(),
                image_uri = BitMapToString(phto)
            )


            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            var reminderId = db.reminderDao().insert(reminder).toInt()
            db.close()


            Toast.makeText(
                applicationContext,
                "New task added.",
                Toast.LENGTH_SHORT
            ).show()


            //add reminder
            //MM/dd/yyyy-HH:mm
            val reminderDueTxt = reminder.reminder_time.split("-")//.toTypedArray()
            val reminderDueDateTxt = reminderDueTxt[0].split("/").toTypedArray()
            val reminderDueTimeTxt = reminderDueTxt[1].split(":").toTypedArray()


            val reminderDueDate = GregorianCalendar(
                    reminderDueDateTxt[2].toInt(),
                    reminderDueDateTxt[1].toInt() - 1,
                    reminderDueDateTxt[0].toInt(),
                    reminderDueTimeTxt[0].toInt(),
                    reminderDueTimeTxt[1].toInt()
            )

            if (reminderDueDate.timeInMillis > Calendar.getInstance().timeInMillis){

                val message = reminder.message
                MainActivity.setReminderWithWorkManager(
                        applicationContext,
                        reminderId,
                        reminderDueDate.timeInMillis,
                        message
                )



            }



            finish()

        }











    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){

            when(requestCode){
                REQUEST_IMAGE_GALLERY ->{
                    binding.reminderImageCreate.setImageURI(data?.data) // handle chosen image
                    //reminderImgUrl = data?.data.toString()
                    //currentPhotoPath = data.g
                    if (data != null) {
                        phto = BitmapFactory.decodeStream(data.data?.let {
                            contentResolver.openInputStream(
                                it
                            )
                        })
                    }

                }
                REQUEST_IMAGE_CAPTURE -> {
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                        val f = File(currentPhotoPath)
                        mediaScanIntent.data = Uri.fromFile(f)
                        sendBroadcast(mediaScanIntent)
                        binding.reminderImageCreate.setImageURI(mediaScanIntent.data)
                        //reminderImgUrl = mediaScanIntent.data.toString()
                        phto = BitmapFactory.decodeFile(currentPhotoPath)
                    }
                }

                DATE_SET ->{
                    if (data != null) {
                        binding.txtDate.setText(data.getStringExtra("Date"))
                    }

                }
            }


        }


    }


    // CITE: Source from official documentation: https://developer.android.com/training/camera/photobasics?hl=zh-cn
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    // CITE: Source from official documentation: https://developer.android.com/training/camera/photobasics?hl=zh-cn
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.homework1.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun requestPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            val checkPermissions =
                ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)
            if (checkPermissions != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.LOCATION_HARDWARE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    , 1
                )
                return
            }
        }
    }
    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")


        //showTimePickerDialog(v)

    }

    fun showTimePickerDialog(v: View) {
        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "timePicker")
    }



}