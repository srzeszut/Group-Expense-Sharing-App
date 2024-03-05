package pl.edu.agh.utp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pl.edu.agh.utp.R
import pl.edu.agh.utp.viewmodel.SummaryRecyclerViewAdapter
import pl.edu.agh.utp.viewmodel.TransactionsAdapter
import java.util.UUID


import pl.edu.agh.utp.view.transaction.AddTransactionFragment
import pl.edu.agh.utp.view.transaction.TransactionDetailsFragment




class SummaryFragment (private val groupId: UUID): Fragment() {

    private var columnCount = 1

    private lateinit var recyclerView: RecyclerView
    private lateinit var reimbursementAdapter: SummaryRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {val view = inflater.inflate(R.layout.fragment_summary, container, false)

        val btnFilters: FloatingActionButton = view.findViewById(R.id.filters_button)

        val btnGraph: Button = view.findViewById(R.id.graph_button)

        btnGraph.setOnClickListener {
            navigateToGraphFragment()
        }

        btnFilters.setOnClickListener {
            showCategoryFilter()
        }

        recyclerView = view.findViewById(R.id.summary_recycler_view)
        reimbursementAdapter = SummaryRecyclerViewAdapter(groupId)
        recyclerView.adapter = reimbursementAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return view
    }

    override fun onResume() {
        super.onResume()
        reimbursementAdapter.fetchReimburesments()
    }

    private fun showCategoryFilter() {
        val categoryFilter = CategoryFilterFragment(groupId, reimbursementAdapter::fetchReimbursementsByCategories)
        categoryFilter.show(requireActivity().supportFragmentManager, "CategoryFilter")
    }

    private fun navigateToGraphFragment() {
        val fragment = GraphFragment(groupId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}