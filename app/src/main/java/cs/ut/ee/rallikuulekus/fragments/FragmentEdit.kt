package cs.ut.ee.rallikuulekus.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.viewModels.SignsViewModel

private const val index = "index"
class FragmentEdit : Fragment() {
    private var signIndex: Int = 0
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var model: SignsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity!!.run {
            ViewModelProviders.of(this)[SignsViewModel::class.java]
        }
        arguments?.let {
            signIndex = it.getInt(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fragment_edit, container, false)
        val signOnTheScreen = model.signs[signIndex]
        view.findViewById<TextView>(R.id.text_view_sign_edit_heading).text = signOnTheScreen.heading
        view.findViewById<ImageView>(R.id.image_view_sign_edit).setImageDrawable(signOnTheScreen.drawable)
        view.findViewById<TextView>(R.id.text_view_sign_edit_description).text = signOnTheScreen.description
        view.findViewById<MaterialButton>(R.id.button_change_sign).setOnClickListener {
            onButtonChangePressed()
        }
        view.findViewById<MaterialButton>(R.id.button_delete_sign).setOnClickListener {
            onButtonDeletePressed()
        }
        return view
    }


    private fun onButtonChangePressed() {
        listener?.changeSign(signIndex)
    }

    private fun onButtonDeletePressed() {
        listener?.deleteSign(signIndex)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun changeSign(signIndex: Int)
        fun deleteSign(signIndex: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            FragmentEdit().apply {
                arguments = Bundle().apply {
                    putInt(index, param1)
                }
            }
    }
}
