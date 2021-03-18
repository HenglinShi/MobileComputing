package fi.oulu.hshi.reminderapp


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fi.oulu.hshi.reminderapp.databinding.ActivityModifyReminderBinding
import fi.oulu.hshi.reminderapp.databinding.ActivityProfileBinding
import fi.oulu.hshi.reminderapp.db.AppDatabase
import fi.oulu.hshi.reminderapp.db.ReminderInfo
import fi.oulu.hshi.reminderapp.entity.Reminder
import fi.oulu.hshi.reminderapp.entity.User
import fi.oulu.hshi.reminderapp.utils.BitMapToString
import fi.oulu.hshi.reminderapp.utils.StringToBitMap
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class ModifyReminderActivity : AppCompatActivity() {

    private lateinit var binding:ActivityModifyReminderBinding
    private lateinit var reminder:ReminderInfo
    //private var reminderId by Delegates.notNull<Int>()
    lateinit var reminderImgUri: Uri
    lateinit var reminderImgUriStr: String
    val REQUEST_IMAGE_GALLERY = 100
    val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_CODE = 1001
    lateinit var currentPhotoPath: String
    lateinit var phot:Bitmap
    var imgIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyReminderBinding.inflate(layoutInflater)

        var intent = getIntent()
        var key = intent.getStringExtra("reminder")


        db = FirebaseDatabase.getInstance().reference.child("reminders")

        db = Firebase.database.reference
        if (key != null) {
            db.child("users").child(key).get().addOnSuccessListener {
                var reminder = it.getValue(Reminder::class.java)!!

                phot = StringToBitMap(reminder.image_uri)!!
                binding.reminderImageModify.setImageBitmap(phot)
                binding.txtTaskDesc.setText(reminder.message)
                binding.txtCreatedDate.setText(reminder.creation_time)
                binding.txtDate.setText(reminder.reminder_time)
                binding.txtLocationX.setText(reminder.location_x.toString())
                binding.txtLocationY.setText(reminder.location_y.toString())

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }


        val view = binding.root
        setContentView(view)



        binding.btnAccept.setOnClickListener {
            val remind2Update = ReminderInfo(
                rid = reminder.rid,
                message = binding.txtTaskDesc.text.toString(),
                creator_id = reminder.creator_id,
                location_x = binding.txtLocationX.text.toString().toDouble(),
                location_y = binding.txtLocationY.text.toString().toDouble(),
                reminder_see = reminder.reminder_see,
                creation_time = binding.txtCreatedDate.text.toString(),
                reminder_time = binding.txtDate.text.toString(),
                image_uri = BitMapToString(phot)
            )
            val db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName))
                .allowMainThreadQueries().
                fallbackToDestructiveMigration().build()
            //AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            val uuid = db.reminderDao().updateReminder(remind2Update)//.toInt()
            db.close()


            finish()
        }

        binding.btnReturn.setOnClickListener {
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }

        binding.reminderImageModify.setOnClickListener{

            val builder = AlertDialog.Builder(this@ModifyReminderActivity)
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

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){

            when(requestCode){
                REQUEST_IMAGE_GALLERY ->{
                    binding.reminderImageModify.setImageURI(data?.data) // handle chosen image
                    //reminderImgUriStr = data?.data.toString()
                    if (data != null) {
                        phot = BitmapFactory.decodeStream(data.data?.let {
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
                        binding.reminderImageModify.setImageURI(mediaScanIntent.data)
                        //reminderImgUriStr = mediaScanIntent.data.toString()
                        phot = BitmapFactory.decodeFile(currentPhotoPath)
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