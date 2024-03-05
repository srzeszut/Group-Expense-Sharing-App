package pl.edu.agh.utp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.edu.agh.utp.R
import pl.edu.agh.utp.model.category.Category
import pl.edu.agh.utp.viewmodel.CategoryFilterAdapter
import java.util.UUID


class CategoryFilterFragment(
    private val groupId: UUID,
    private val callback: (List<Category>) -> Unit) : DialogFragment() {

    private lateinit var categoryFilterAdapter: CategoryFilterAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_filter, container, false)

        recyclerView = view.findViewById(R.id.category_list)
        categoryFilterAdapter = CategoryFilterAdapter(groupId)
        categoryFilterAdapter.fetchGroupCategories()
        recyclerView.adapter = categoryFilterAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val resetButton: Button = view.findViewById(R.id.reset_btn)
        val applyButton: Button = view.findViewById(R.id.apply_btn)

        resetButton.setOnClickListener {
            categoryFilterAdapter.reset()
        }
        applyButton.setOnClickListener {
            callback(categoryFilterAdapter.getSelectedCategories())
            this.dismiss()
        }

        return view
    }
}