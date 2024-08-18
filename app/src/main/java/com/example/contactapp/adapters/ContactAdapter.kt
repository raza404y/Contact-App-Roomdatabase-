package com.example.contactapp.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.database.Contacts
import com.example.contactapp.databinding.ContactRvDesignBinding
import com.example.contactapp.ui.AddUpdate

class ContactAdapter(private val context: Context) : ListAdapter<Contacts, ContactAdapter.ViewHolder>(diffCallBack) {
    inner class ViewHolder(val binding: ContactRvDesignBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<Contacts>() {
            override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ContactRvDesignBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = getItem(position)
        holder.binding.userName.text = i.name
        holder.binding.phoneNo.text = i.phoneNumber
        holder.binding.dateTv.text = i.date
        var bitmap: Bitmap? = null
        if (i.profileImage != null) {
            bitmap = i.profileImage?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        }
        holder.binding.profileImg.setImageBitmap(bitmap)

        val name = i.name
        val tempName = name?.split(" ")
        var nameInitials = ""
        tempName?.forEach {
            nameInitials += it[0]
        }
        holder.binding.nameInitialsTv.text = nameInitials
        if (bitmap == null) {
            holder.binding.nameInitialsTv.isVisible = true
            holder.binding.profileImg.isVisible = false
        } else {
            holder.binding.profileImg.isVisible = true
            holder.binding.nameInitialsTv.isVisible = false
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AddUpdate::class.java).apply {
                putExtra("mode", 2)
                putExtra("oldC", i)
            }
            context.startActivity(intent)
        }

        holder.binding.profileImg.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.profile_picture_dialog)
            val imgview = dialog.findViewById<ImageView>(R.id.imgeView)
            imgview.setImageBitmap(bitmap)
            dialog.setCancelable(true)
            dialog.show()
        }

    }
}