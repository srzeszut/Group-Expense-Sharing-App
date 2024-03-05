package pl.edu.agh.utp.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailListAdapter(private val emailList: MutableList<String>) :
    RecyclerView.Adapter<EmailListAdapter.EmailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emailList[position]

        holder.itemView.setOnClickListener {
            emailList.remove(email)
            notifyItemRemoved(position)
        }

        holder.bind(email)
    }

    override fun getItemCount(): Int {
        return emailList.size
    }

    class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(email: String) {
            val textView: TextView = itemView.findViewById(android.R.id.text1)
            textView.text = email
        }
    }
}