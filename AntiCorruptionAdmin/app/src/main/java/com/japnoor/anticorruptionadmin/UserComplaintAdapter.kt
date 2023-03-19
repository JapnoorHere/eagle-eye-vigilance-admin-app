package com.japnoor.anticorruptionadmin

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruptionadmin.databinding.ItemComplaintBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class UserComplaintAdapter(var context: AdminHomeScreen, var complaintsList: ArrayList<Complaints>,var userComplaintClick: UserComplaintClick)  : RecyclerView.Adapter<UserComplaintAdapter.ViewHolder>() {


    class ViewHolder(var binding: ItemComplaintBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemComplaintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.compAgainst.setText(decrypt(complaintsList[position].complaintAgainst))
        holder.binding.tvDistrict.setText(decrypt(complaintsList[position].complaintDistrict))
        holder.binding.compdetails.setText(decrypt(complaintsList[position].complaintDetails))
        holder.binding.date.setText(decrypt(complaintsList[position].complaintDate))
        holder.binding.time.setText(decrypt(complaintsList[position].complaintTime))
        holder.binding.complaintNumber.setText(decrypt(complaintsList[position].complaintNumber))
        when (complaintsList[position].status) {
            "1" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.acceptedback)
//                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            }
            "2" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.resolvedback)
                //                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            "3" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.rejectedback)
//                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_done_all_24)
            }
            else->{
//                holder.binding.inpriority.setImageResource(0)
            }

        }


        holder.itemView.setOnClickListener{
            userComplaintClick.onClick(complaintsList[position])
        }
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }

//    fun getuserId(position: Int): Complaints {
//        return complaintsList[position]
//    }

    fun FilteredList(filteredList: ArrayList<Complaints>) {
        complaintsList=filteredList
        notifyDataSetChanged()
    }
    private fun decrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
