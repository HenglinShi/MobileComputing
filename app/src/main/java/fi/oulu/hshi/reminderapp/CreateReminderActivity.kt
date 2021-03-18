package fi.oulu.hshi.reminderapp

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fi.oulu.hshi.reminderapp.databinding.ActivityCreateReminderBinding
import fi.oulu.hshi.reminderapp.utils.BitMapToString
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import fi.oulu.hshi.reminderapp.entity.Reminder
import fi.oulu.hshi.reminderapp.utils.StringToBitMap

const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val LOCATION_REQUEST_CODE = 123
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
var reminderToModify: Reminder? = null
class CreateReminderActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityCreateReminderBinding
    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_GALLERY = 100
    val DATE_SET = 202
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var reminderImgUrl: String
    lateinit var phto:Bitmap
    lateinit var location: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var reminderCalender: Calendar
    lateinit var db: DatabaseReference
    private lateinit var geofencingClient: GeofencingClient

    private lateinit var reminderToModifyKey: String


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance().reference.child("reminders")



        var intent = getIntent()
        reminderToModifyKey = intent.getStringExtra("reminderToModifyKey").toString()



        if (reminderToModifyKey != null) {
            db.child(reminderToModifyKey).get().addOnSuccessListener {

                if (it.value != null){
                    reminderToModify = it.getValue(Reminder::class.java)!!
                    setView(reminderToModify!!)
                }
                else {
                    reminderToModifyKey = null.toString()
                    val c = Calendar.getInstance().time
                    var timStr = android.icu.text.SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH).format(c.getTime());
                    binding.txtCreatedDate.setText(timStr)
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }



        binding = ActivityCreateReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnReturn.setOnClickListener {
            finish()
        }

        geofencingClient = LocationServices.getGeofencingClient(this)
        binding.txtDate.setOnClickListener {
            reminderCalender = GregorianCalendar.getInstance()
            DatePickerDialog(
                this,
                this,
                reminderCalender.get(Calendar.YEAR),
                reminderCalender.get(Calendar.MONTH),
                reminderCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }




        binding.txtLocationX.setText(latitude.toString())
        binding.txtLocationY.setText(longitude.toString())



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


        binding.txtLocationX.setOnClickListener {
            //startActivity(Intent(applicationContext, MapsActivity::class.java))
            startActivityForResult(Intent(applicationContext, MapsActivity::class.java), 1001)
        }

        binding.btnAccept.setOnClickListener {
            // Adding new reminder

            if (binding.txtDate.text.isEmpty()) {

                return@setOnClickListener
            }
            var key:String
            if (reminderToModify == null){
                key = db.push().key.toString()
            }
            else {
                key = reminderToModify!!.key
            }

            val reminder = Reminder(key, binding.txtTaskDesc.text.toString(),
                    binding.txtLocationX.text.toString().toDouble(), binding.txtLocationY.text.toString().toDouble(),
                    binding.txtDate.text.toString(), binding.txtCreatedDate.text.toString(),
                    BitMapToString(phto)) //, falselatlng.latitude, latlng.longitude)

            db.child(key).setValue(reminder)

            var reminderTime = dateTxtToObj(reminder.reminder_time)

            reminderToModify = null
            //(key, LatLng(reminder.location_x, reminder.location_y),reminderTime.timeInMillis, reminder.message,geofencingClient)




            finish()

        }

    }

    private fun dateTxtToObj(date:String):GregorianCalendar{
        //MM/dd/yyyy-HH:mm
        val reminderDueTxt = date.split("-")//.toTypedArray()
        val reminderDueDateTxt = reminderDueTxt[0].split("/").toTypedArray()
        val reminderDueTimeTxt = reminderDueTxt[1].split(":").toTypedArray()


        return GregorianCalendar(
            reminderDueDateTxt[2].toInt(),
            reminderDueDateTxt[0].toInt() - 1,
            reminderDueDateTxt[1].toInt(),
            reminderDueTimeTxt[0].toInt(),
            reminderDueTimeTxt[1].toInt()
        )
    }


    private fun setView(reminder:Reminder) {
        phto = StringToBitMap(reminder.image_uri)!!
        binding.reminderImageCreate.setImageBitmap(phto)
        binding.txtTaskDesc.setText(reminder.message)
        binding.txtCreatedDate.setText(reminder.creation_time)
        binding.txtDate.setText(reminder.reminder_time)
        binding.txtLocationX.setText(reminder.location_x.toString())
        binding.txtLocationY.setText(reminder.location_y.toString())
    }


    private fun createGeoFence(key: String, location: LatLng, reminderTime: Long, msg: String, geofencingClient: GeofencingClient) {
        val geofence = Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(this, GeofenceReceiver::class.java)
            .putExtra("key", msg)
            .putExtra("reminderTime", reminderTime)
            .putExtra("message", "Geofence alert - ${location.latitude}, ${location.longitude}")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
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

                1001 ->{
                    if (data != null) {
                        latitude = data.getDoubleExtra("latitude", 0.0)
                    }
                    if (data != null) {
                        longitude = data.getDoubleExtra("longitude", 0.0)
                    }
                    binding.txtLocationX.setText(latitude.toString())
                    binding.txtLocationY.setText(longitude.toString())
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
                        "fi.oulu.hshi.reminderapp.fileprovider",
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


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        reminderCalender.set(Calendar.YEAR, year)
        reminderCalender.set(Calendar.MONTH, month)
        reminderCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val simleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        binding.txtDate.setText(simleDateFormat.format(reminderCalender.time))

        // if you want to show time picker after the date
        // you dont need this,change dateFormat value to dd.MM.yyyy
        TimePickerDialog(
            this,
            this,
            reminderCalender.get(Calendar.HOUR_OF_DAY),
            reminderCalender.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        reminderCalender.set(Calendar.HOUR_OF_DAY, hourOfDay)
        reminderCalender.set(Calendar.MINUTE, minute)
        val simleDateFormat = SimpleDateFormat("MM/dd/yyyy-HH:mm")
        binding.txtDate.setText(simleDateFormat.format(reminderCalender.time))
    }


}