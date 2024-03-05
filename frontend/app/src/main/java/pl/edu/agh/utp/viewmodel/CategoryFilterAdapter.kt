package pl.edu.agh.utp.viewmodel

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import pl.edu.agh.utp.api.ApiObject

import pl.edu.agh.utp.databinding.ItemCategoryFilterBinding
import pl.edu.agh.utp.model.category.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CategoryFilterAdapter(private val groupId: UUID) : RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder>() {

    private var categories: List<Category> = listOf()
    private val categoriesHashmap: HashMap<Category, Boolean> = HashMap()
    private val apiService = ApiObject.instance

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemCategoryFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            categoriesHashmap[category] = isChecked
        }
    }

    override fun getItemCount(): Int = categories.size

    inner class ViewHolder(binding: ItemCategoryFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val checkbox: CheckBox = binding.checkbox
        val name: TextView = binding.name
    }

    fun fetchGroupCategories() {
        apiService.getCategoriesByGroup(groupId).enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                if (response.isSuccessful) {
                    Log.i("GetCategoriesByGroup", "Success: ${response.body()}")
                    val groupCategories = response.body()
                    if (groupCategories != null) {
                        categories = groupCategories
                        categories.forEach {category -> categoriesHashmap[category] = true}
                        notifyDataSetChanged()
                    }
                } else {
                    Log.e("GetCategoriesByGroup", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<List<Category>>,
                t: Throwable
            ) {
                Log.e("GetCategoriesByGroup", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun reset() {
        categories.forEach {categoriesHashmap[it] = true}
    }

    fun getSelectedCategories(): List<Category> {
        val selectedCategories = mutableListOf<Category>()
        for ((category, isSelected) in categoriesHashmap.entries) {
            if (isSelected) {
                selectedCategories.add(category)
            }
        }
        return selectedCategories
    }
}