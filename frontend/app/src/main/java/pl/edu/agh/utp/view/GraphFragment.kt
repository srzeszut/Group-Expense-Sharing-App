package pl.edu.agh.utp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pl.edu.agh.utp.R
import pl.edu.agh.utp.model.graph.TransactionsGraph
import pl.edu.agh.utp.viewmodel.UserFilterAdapter
import java.util.UUID


class GraphFragment (private val groupId: UUID) : Fragment() {

    private lateinit var userFilterAdapter: UserFilterAdapter
    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_graph, container, false)

        val btnFilters: FloatingActionButton = view.findViewById(R.id.filters_button)

        webView = view.findViewById(R.id.graphWebView)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        btnFilters.setOnClickListener {
            showUserFilter()
        }
        userFilterAdapter = UserFilterAdapter(groupId)

//        var nodes = """[
//            { id: 1, label: "Node 1" },
//            { id: 2, label: "Node 2" },
//            { id: 3, label: "Node 3" },
//            { id: 4, label: "Node 4" },
//            { id: 5, label: "Node 5" },
//        ]""";
//        var edges = """[
//            { from: 1, to: 2, label: 'Edge 1' },
//            { from: 2, to: 3, label: 'Edge 2' },
//            { from: 3, to: 4, label: 'Edge 3' },
//            { from: 4, to: 5, label: 'Edge 4' },
//        ]""";


        return view
    }

    private fun setGraph(transactionsGraph: TransactionsGraph) {
        val nodes = transactionsGraph.toNodesString()
        val edges = transactionsGraph.toEdgesString()
        webView.loadDataWithBaseURL("", getJSCode(nodes, edges), "text/html", "UTF-8", "")

    }

    private fun showUserFilter() {
        val userFilter = UserFilterFragment(groupId, userFilterAdapter::getGraphWithUsers, this::setGraph)
        userFilter.show(requireActivity().supportFragmentManager, "UserFilter")
    }

    override fun onResume() {
        super.onResume()
        userFilterAdapter.getGraph(this::setGraph)
    }

    private fun getJSCode(nodes: String, edges: String): String {
        return """
    <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Interactive Graph</title>
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
</head>
<body>
    <div id="graph-container" style="height: 100vh;"></div>

    <script type="text/javascript">
       var nodes = new vis.DataSet($nodes);

        var edges = new vis.DataSet($edges);

        var data = {
            nodes: nodes,
            edges: edges
        };

        var options = {
            layout: {
                hierarchical: false
            },
            edges: {
                arrows: {
                    to: {
                        enabled: true,
                        scaleFactor: 1.0,
                    }
                },
                font: {
                    size: 30,
                    align: "middle"
                },
                color: "#000000"
            },
            nodes: {
                color: {
                    background: "#ffffff"
                },
                font: {
                    size: 30
                }
            },
            interaction: {
                dragNodes: true,
                dragView: true,
                zoomView: true
            },
            physics: {
                enabled: false
            }
        };

        var container = document.getElementById("graph-container");
        var network = new vis.Network(container, data, options);
    </script>
</body>
</html>

        """.trimIndent()

    }

}