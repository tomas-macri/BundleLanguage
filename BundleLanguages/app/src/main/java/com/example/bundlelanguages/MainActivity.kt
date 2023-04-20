package com.example.bundlelanguages

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.bundlelanguages.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get installed locales into a list
        val installedLocales: MutableList<Locale> = mutableListOf()
        for (i in 0 until resources.configuration.locales.size()) {
            installedLocales.add(resources.configuration.locales.get(i))
        }

        binding.spinnerLanguages.adapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            installedLocales.map { it.displayName }
        )

        binding.spinnerLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val locale = installedLocales[position]

                if (locale == resources.configuration.locales.get(0)) {
                    return
                }

                val config = Configuration(resources.configuration)
                // TODO: This should be setLocales and a LocaleList, with the first locale being the one you want to display
                config.setLocale(locale)
                // TODO: This is deprecated, but I don't know what to use instead
                resources.configuration.setTo(config)
                resources.displayMetrics.setTo(resources.displayMetrics)
                recreate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }
        }

    }
}