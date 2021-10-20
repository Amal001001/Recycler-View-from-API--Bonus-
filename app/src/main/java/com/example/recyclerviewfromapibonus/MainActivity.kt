package com.example.recyclerviewfromapibonus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var rv: RecyclerView
    private lateinit var rvAdapter: RecyclerViewAdapter
    var names = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        names = arrayListOf()
        rv = findViewById(R.id.rv)
        rvAdapter = RecyclerViewAdapter(names)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        requestAPI()
        Log.d("tag","onCreate")
    }

    private fun requestAPI() {
        CoroutineScope(IO).launch {
            Log.d("tag", "requestAPI/CoroutineScope")
            val data = async { getNames() }.await()
            //      val data = getNames()
            if (data.isNotEmpty()) {
                showNames(data)
            }
        }
    }

    private fun getNames(): String {
        var response = ""
        try {
            response = URL("https://dojo-recipes.herokuapp.com/people/?format=json").readText(Charsets.UTF_8)
        } catch (e: Exception) {
            println("ISSUE: $e")
        }
        Log.d("tag","getNames")
        return response
    }

    private suspend fun showNames(data: String) {
        withContext(Main) {
            val jsonArr = JSONArray(data)
        Log.d("tag","showNames1")
            for (i in 0 until jsonArr.length()) {
                var name = jsonArr.getJSONObject(i).getString("name")
                names.add(name)
            }
        Log.d("tag","showNames2")
            rvAdapter.notifyDataSetChanged()
        }
    }

}
