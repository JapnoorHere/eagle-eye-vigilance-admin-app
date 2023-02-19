    package com.japnoor.anticorruption.admin

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.cardview.widget.CardView
    import androidx.recyclerview.widget.RecyclerView
    import com.google.android.material.bottomsheet.BottomSheetDialog
    import com.google.firebase.database.ValueEventListener
    import com.japnoor.anticorruptionadmin.AdminHomeScreen
    import com.japnoor.anticorruptionadmin.ComplaintClickedInterface
    import com.japnoor.anticorruptionadmin.Complaints
    import com.japnoor.anticorruptionadmin.R
    import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding

    class AdminTotalComplaintsAdapter(
        var context: AdminHomeScreen, var complaintsList: ArrayList<Complaints>,
        var complaintClickedInterface: ComplaintClickedInterface
    ) : RecyclerView.Adapter<AdminTotalComplaintsAdapter.ViewHolder>() {
        class ViewHolder(var binding: ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root) {
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemComlaintBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            when(complaintsList[position].status){
                "1"->{
                    holder.binding.upline.setBackgroundResource(R.drawable.acceptedback)
                    holder.binding.downline.setBackgroundResource(R.drawable.acceptedback)
                }
                "2"->{
                    holder.binding.upline.setBackgroundResource(R.drawable.resolvedback)
                    holder.binding.downline.setBackgroundResource(R.drawable.resolvedback)
                }
                "3"->{
                    holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
                    holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
                }
                else->{
                    holder.binding.upline.setBackgroundResource(R.drawable.purpleback)
                    holder.binding.downline.setBackgroundResource(R.drawable.purpleback)
                }

            }
            holder.binding.tvAgainst.setText(complaintsList[position].complaintAgainst)
            holder.binding.tvSummary.setText(complaintsList[position].complaintSummary)
            holder.binding.compNumber.setText(complaintsList[position].complaintNumber)
            holder.binding.time.setText(complaintsList[position].complaintTime)
            holder.binding.Date.setText(complaintsList[position].complaintDate)
            holder.binding.userName.setText(complaintsList[position].userName)
            holder.itemView.setOnClickListener {
                   complaintClickedInterface.onComplaintsClicked(complaintsList[position])
            }
            holder.itemView.setOnLongClickListener{
                var bottomSheet = BottomSheetDialog(context)
                bottomSheet.setContentView(R.layout.complaint_menu_hold)
                var chat=bottomSheet.findViewById<CardView>(R.id.chat)
                bottomSheet.show()
                true
            }
        }

        override fun getItemCount(): Int {
            return complaintsList.size
        }

        fun FilteredList(filteredList: ArrayList<Complaints>) {
            complaintsList=filteredList
            notifyDataSetChanged()
        }
    }