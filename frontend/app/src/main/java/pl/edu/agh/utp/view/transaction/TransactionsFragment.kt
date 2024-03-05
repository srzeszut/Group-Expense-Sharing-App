package pl.edu.agh.utp.view.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pl.edu.agh.utp.R
import pl.edu.agh.utp.view.CategoryFilterFragment
import pl.edu.agh.utp.view.GraphFragment
import pl.edu.agh.utp.view.SummaryFragment
import pl.edu.agh.utp.view.transaction.AddTransactionFragment
import pl.edu.agh.utp.view.transaction.TransactionDetailsFragment
import pl.edu.agh.utp.viewmodel.TransactionsAdapter
import java.util.UUID


class TransactionsFragment( private val groupId: UUID) : Fragment(),
    TransactionsAdapter.TransactionClickListener {


    override fun onTransactionClick(transactionId: UUID) {
        navigateToTransactionDetailsFragment(transactionId)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionsAdapter: TransactionsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        val btnAdd: Button = view.findViewById(R.id.add_transaction_button)
        val btnSummary: Button = view.findViewById(R.id.summary_button)
        val btnFilters: FloatingActionButton = view.findViewById(R.id.filters_button)

        btnAdd.setOnClickListener {
            navigateToAddTransactionFragment()
        }
        btnSummary.setOnClickListener {
            navigateToSummaryFragment()
        }

        btnFilters.setOnClickListener {
            showCategoryFilter()
        }

        recyclerView = view.findViewById(R.id.transactions_recycler_view)
        transactionsAdapter = TransactionsAdapter(groupId, this)
        recyclerView.adapter = transactionsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return view
    }

    override fun onResume() {
        super.onResume()
        transactionsAdapter.fetchTransactions()
    }

    private fun navigateToAddTransactionFragment() {
        val fragment = AddTransactionFragment(groupId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun navigateToTransactionDetailsFragment(transactionId: UUID) {
        val fragment = TransactionDetailsFragment(transactionId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToSummaryFragment() {
        val fragment = SummaryFragment(groupId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun showCategoryFilter() {
        val categoryFilter = CategoryFilterFragment(groupId, transactionsAdapter::fetchTransactionsByCategories)
        categoryFilter.show(requireActivity().supportFragmentManager, "CategoryFilter")
    }
}