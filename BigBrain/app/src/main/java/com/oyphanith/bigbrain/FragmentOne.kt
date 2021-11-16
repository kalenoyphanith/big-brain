package com.oyphanith.bigbrain

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.oyphanith.bigbrain.databinding.FragmentOneBinding

class FragmentOne : Fragment() {

    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedCards: SharedPreferences

    private var savedTerm = ""
    private var savedDefinition = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedCards = requireActivity().getSharedPreferences("words", Context.MODE_PRIVATE)

        binding.saveButton.setOnClickListener { handleSaveButtonClick() }

        refreshButtons(null)
    } // onViewCreated

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun refreshButtons(newTag: String?) {

        val terms: Array<String> = savedCards.all.keys.toTypedArray()
        terms.sortWith(String.CASE_INSENSITIVE_ORDER)

        if (newTag != null) {
            var index = terms.indexOf(newTag)
            makeGUI(savedCards.getString(newTag, "").toString(), newTag, index)
        } else {
            for (index in terms.indices) {
                makeGUI(savedCards.getString(terms[index], "").toString(), terms[index], index)
            }
        }

    } // refreshButtons

    private fun make(definition: String, term: String) {
        val originalDefinition = savedCards.getString(tag, "")

        val editor = savedCards.edit()
        editor.putString(term, definition)
        editor.apply()

        if (originalDefinition == "") {
            refreshButtons(term)
        }
    } // make

    private fun makeGUI(term: String, definition: String, index: Int) {

        val newTagView = layoutInflater.inflate(R.layout.new_tag_view, null, false) // inflate xml file

        val termTextView = newTagView.findViewById<TextView>(R.id.termTextView)
        val definitionTextView = newTagView.findViewById<TextView>(R.id.definitionTextView)

        termTextView.text = term
        definitionTextView.text = definition

        val newEditButton = newTagView.findViewById<Button>(R.id.newEditButton)
        newEditButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                handleEditButtonClicked(v as Button)
            }
        })

        val deleteButton = newTagView.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                handleClearButtonClick(v as Button)
            }
        })

        binding.wordsLinearLayout.addView(newTagView, index)

    } // makeGUI

    private fun clearButtons() {
        binding.wordsLinearLayout.removeAllViews()
    } // clearButtons

    private fun clearButton(v: Button){
        val buttonRow = v.parent as ConstraintLayout
        binding.wordsLinearLayout.removeView(buttonRow)
    }

    private fun handleSaveButtonClick() { // data validation
        if (binding.termEditText.text.isNotEmpty() &&
            binding.definitionEditText.text.isNotBlank()) {

            if (savedTerm.isNotEmpty() || savedDefinition.isNotEmpty()) {
                val editor = savedCards.edit()
                editor.remove(savedDefinition)
                editor.putString(binding.definitionEditText.text.toString(), binding.termEditText.text.toString())
                editor.apply()
                clearButtons()
                refreshButtons( null)

            } else {
                make(binding.termEditText.text.toString(),
                    binding.definitionEditText.text.toString())
            }
            binding.termEditText.setText("")
            binding.definitionEditText.setText("")
            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(binding.definitionEditText.windowToken, 0)
        } else {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.missingTitle)
            builder.setPositiveButton(R.string.OK, null)
            builder.setMessage(R.string.missingMessage)
            val errorDialog = builder.create() // could add .show() to save some steps
            errorDialog.show()
        }

    } // handleSaveButtonClick

    private fun handleClearButtonClick(v: Button) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.confirmTitle)
        builder.setPositiveButton(R.string.erase, {dialog, which ->
            clearButton(v)
            savedCards.edit().clear().apply()
        })

        builder.setCancelable(true)
        builder.setNegativeButton(R.string.cancel, null)

        builder.setMessage(R.string.confirmMessage)
        builder.create().show()

    } // handleClear Button

    private fun handleEditButtonClicked(v: Button) {
        val buttonRow = v.parent as ConstraintLayout
        val termSearch = buttonRow.findViewById<TextView>(R.id.termTextView)
        val definitionSearch = buttonRow.findViewById<TextView>(R.id.definitionTextView)

        val term = termSearch.text.toString()
        val definition = definitionSearch.text.toString()

        savedDefinition = definition
        savedTerm = savedCards.getString(definition, "").toString()
        savedTerm = term

        binding.definitionEditText.setText(definition)
        //binding.termEditText.setText(term)
        binding.termEditText.setText(savedCards.getString(definition, ""))
    } // handleEditButtonClicked
}