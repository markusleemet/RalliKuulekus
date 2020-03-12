package cs.ut.ee.rallikuulekus.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.viewModels.SignsViewModel

class FragmentMenu : Fragment() {
    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var model: SignsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity!!.run {
            ViewModelProviders.of(this)[SignsViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fragment_menu, container, false)
        val saveAndExportButton = view.findViewById<MaterialButton>(R.id.button_save_and_export)
        val goBackToMainMenuButton = view.findViewById<MaterialButton>(R.id.button_go_to_main_menu)
        val saveButton = view.findViewById<MaterialButton>(R.id.button_save)
        saveAndExportButton.setOnClickListener {
            closeAndExportBitmap()
        }
        goBackToMainMenuButton.setOnClickListener {
            goBackToMainMenu()
        }
        saveButton.setOnClickListener {
            save()
        }

        return view
    }

    fun onButtonPressed() {
        listener?.saveAndExportBitmap()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun saveAndExportBitmap()
        fun saveSchema()
    }

    private fun closeAndExportBitmap(){
        listener?.saveAndExportBitmap()
    }

    private fun goBackToMainMenu(){
        activity!!.finish()
    }

    private fun save() {
        listener?.saveSchema()
    }
}
