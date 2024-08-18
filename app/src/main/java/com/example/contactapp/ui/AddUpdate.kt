package com.example.contactapp.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactapp.R
import com.example.contactapp.database.Contacts
import com.example.contactapp.databinding.ActivityAddUpdateBinding
import com.example.contactapp.viewmodel.AddUpdateViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddUpdate : AppCompatActivity() {
    private val binding: ActivityAddUpdateBinding by lazy {
        ActivityAddUpdateBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: AddUpdateViewModel
    private var imageData: ByteArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this@AddUpdate)[AddUpdateViewModel::class.java]

        val mode = intent.getIntExtra("mode", -1)
        if (intent.hasExtra("mode")) {
            if (mode == 2) {
                val contacts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("oldC", Contacts::class.java)!!
                } else {
                    intent.getSerializableExtra("oldC") as Contacts
                }
                binding.saveContactBtn.text = "Update contact"
                binding.nameEt.setText(contacts.name)
                binding.phoneNumberEt.setText(contacts.phoneNumber)
                var bitmap: Bitmap? = null
                if (contacts.profileImage != null) {
                    bitmap = contacts.profileImage?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                }
                binding.profileImage.setImageBitmap(bitmap)
                if (bitmap == null) binding.profileImage.setImageResource(R.drawable.user_placeholder)

                binding.saveContactBtn.setOnClickListener {

                    val name = binding.nameEt.text.toString().trim()
                    val phoneNo = binding.phoneNumberEt.text.toString().trim()

                    when {
                        name.isEmpty() -> showToast("Enter name")
                        phoneNo.isEmpty() -> showToast("Enter phone number")
                        name.length <= 3 -> showToast("name greater than 3")
                        phoneNo.length <= 4 || phoneNo.length >= 15 -> showToast("invalid phone number")
                        else -> {
                            contacts.name = name
                            contacts.phoneNumber = phoneNo
                            val simpleDate = SimpleDateFormat("E, dd MMM yyyy - hh:mm a", Locale.getDefault())
                            val currentDateTime = simpleDate.format(Date())
                            contacts.date = currentDateTime
                            if (imageData != null) {
                                contacts.profileImage = imageData
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.update(contacts)
                                withContext(Dispatchers.Main) {
                                    showToast("Updated")
                                    finish()
                                }
                            }
                        }
                    }
                }
            } else {
                binding.saveContactBtn.setOnClickListener {

                    val name = binding.nameEt.text.toString().trim()
                    val phoneNo = binding.phoneNumberEt.text.toString().trim()
                    when {
                        name.isEmpty() -> showToast("Enter name")
                        phoneNo.isEmpty() -> showToast("Enter phone number")
                        name.length <= 3 -> showToast("name greater than 3")
                        phoneNo.length <= 4 || phoneNo.length >= 15 -> showToast("invalid phone number")
                        else -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                insertData(name, phoneNo, imageData)
                                withContext(Dispatchers.Main) {
                                    showToast("Inserted")
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            binding.saveContactBtn.setOnClickListener {

                val name = binding.nameEt.text.toString().trim()
                val phoneNo = binding.phoneNumberEt.text.toString().trim()
                when {
                    name.isEmpty() -> showToast("Enter name")
                    phoneNo.isEmpty() -> showToast("Enter phone number")
                    name.length <= 3 -> showToast("name greater than 3")
                    phoneNo.length <= 4 || phoneNo.length >= 15 -> showToast("invalid phone number")
                    else -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            insertData(name, phoneNo, imageData)
                            withContext(Dispatchers.Main) {
                                showToast("Inserted")
                            }
                        }
                    }
                }
            }
        }


        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }


    }

    private suspend fun insertData(username: String, phoneNo: String, imageData: ByteArray?) {
        val simpleDate = SimpleDateFormat("E, dd MMM yyyy - hh:mm a", Locale.getDefault())
        val currentDateTime = simpleDate.format(Date())
        val contacts: Contacts = Contacts()
        contacts.name = username
        contacts.phoneNumber = phoneNo
        contacts.profileImage = imageData
        contacts.date = currentDateTime
        viewModel.insert(contacts)

    }


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    binding.profileImage.setImageURI(fileUri)
                    val stream: InputStream = fileUri.let {
                        contentResolver.openInputStream(fileUri)!!
                    }
                    imageData = stream.readBytes()
                }

                ImagePicker.RESULT_ERROR -> {
                    showToast(ImagePicker.getError(data))
                }

                else -> {
                    showToast("task cancelled")
                }
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this@AddUpdate, message, Toast.LENGTH_SHORT).show()
    }
}
