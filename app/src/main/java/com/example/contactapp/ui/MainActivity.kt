package com.example.contactapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.adapters.ContactAdapter
import com.example.contactapp.database.Contacts
import com.example.contactapp.databinding.ActivityMainBinding
import com.example.contactapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    companion object{
        const val REQUEST_CODE = 123
    }
    private lateinit var mainViewModel: MainViewModel
    lateinit var mList: ArrayList<Contacts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddUpdate::class.java))
        }
        val adapter = ContactAdapter(this@MainActivity)
        binding.contactsRV.adapter = adapter
        binding.contactsRV.layoutManager = LinearLayoutManager(this@MainActivity)

        mainViewModel.contactList.observe(this, Observer {
            adapter.submitList(it)
            mList = it as ArrayList
            binding.contactsRV.post {
                binding.contactsRV.smoothScrollToPosition(0)
            }
        })

        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        val currentItem = mList[viewHolder.adapterPosition]
                        CoroutineScope(Dispatchers.IO).launch {
                            mainViewModel.delete(currentItem)
                        }
                        mList.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }else{

                        val currentItem = mList[viewHolder.adapterPosition]
                        val phoneNo = currentItem.phoneNumber
                        if (ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED){
                            if (phoneNo != null) {
                                dialPhoneNumber(phoneNo)
                            }
                        }else{
                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CODE)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.contactsRV)

    }
    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Optionally, you can handle the call initiation here
                val pendingPhoneNumber = null
                pendingPhoneNumber?.let { dialPhoneNumber(it) }
            } else {
                // Permission denied
                // Optionally, inform the user
            }
        }
    }
}
