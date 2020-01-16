package cs.ut.ee.rallikuulekus.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import cs.ut.ee.rallikuulekus.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val NAME = ""
private const val DESCRIPTION = ""

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentMenu.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMenu : Fragment() {
    // TODO: Rename and change types of parameters
    private var name: String? = null
    private var description: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(NAME)
            description = it.getString(DESCRIPTION)
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
        saveAndExportButton.setOnClickListener {
            closeFragmentAndCallListener(it)
        }
        goBackToMainMenuButton.setOnClickListener {
            goBackToMainMenu()
        }

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.saveAndExportBitmap(uri)
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun saveAndExportBitmap(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param name Parameter 1.
         * @param description Parameter 2.
         * @return A new instance of fragment FragmentMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(name: String, description: String) =
            FragmentMenu().apply {
                arguments = Bundle().apply {
                    putString(NAME, name)
                    putString(DESCRIPTION, description)
                }
            }
    }



    private fun closeFragmentAndCallListener(v: View){
        listener?.saveAndExportBitmap(Uri.EMPTY)
    }

    private fun goBackToMainMenu(){
        activity!!.finish()
    }
}
