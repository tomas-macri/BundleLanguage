package com.example.bundlelanguages

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
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

        // TODO: This is a list of languages available in the app
        //  this should be generated automatically or stored in a better place
        val appLanguages = mutableListOf("en_US","es_US")

        //Get installed locales into a list
        val installedLocales: MutableList<Locale> = mutableListOf()
        for (i in 0 until resources.configuration.locales.size()) {
            installedLocales.add(resources.configuration.locales.get(i))
        }

        // Display the list of installed locales in the spinner
        //  and filter out the ones that are not in the app
        binding.spinnerLanguages.adapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            installedLocales.filter { it.toString() in appLanguages }.map { it.displayName }
        )


        binding.spinnerLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                //If the selected locale is already the first one, do nothing
                if (position == 0) {
                    return
                }

                //Move the selected locale to the first position
                val locale = installedLocales[position]
                installedLocales.remove(locale)
                installedLocales.add(0, locale)

                //Update the configuration and recreate the activity
                val config = Configuration(resources.configuration)
                val localeList = LocaleList(*installedLocales.toTypedArray())
                config.setLocales(localeList)

                // TODO: Avoid deprecated method
                resources.updateConfiguration(config, resources.displayMetrics)

                //Set the spinner to the first position
                binding.spinnerLanguages.setSelection(0)
                recreate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }
        }

    }
}