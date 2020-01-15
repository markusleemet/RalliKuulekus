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
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema
import cs.ut.ee.rallikuulekus.entities.SignRotation
import cs.ut.ee.rallikuulekus.viewModels.SignsViewModel

private const val index = "index"
class FragmentEdit : Fragment() {
    private var signIndex: Int = 0
    private lateinit var signOnTheSchema: SignOnTheSchema
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
        signOnTheSchema = model.signs[signIndex]
        view.findViewById<TextView>(R.id.text_view_sign_edit_heading).text = signOnTheSchema.heading
        val imageView = view.findViewById<ImageView>(R.id.image_view_sign_edit)
        imageView.setImageDrawable(signOnTheSchema.drawable)
        imageView.rotateToHeading(signOnTheSchema.rotation)
        view.findViewById<TextView>(R.id.text_view_sign_edit_description).text = signOnTheSchema.description
        view.findViewById<MaterialButton>(R.id.button_change_sign).setOnClickListener {
            onButtonChangePressed()
        }
        view.findViewById<MaterialButton>(R.id.button_delete_sign).setOnClickListener {
            onButtonDeletePressed()
        }
        view.findViewById<MaterialButton>(R.id.button_turn_right).setOnClickListener {
            signOnTheSchema.rotation = getNewRotation(signOnTheSchema.rotation, 90)
            imageView.rotateToHeading(signOnTheSchema.rotation)
            onButtonRotatePressed(90)
        }
        view.findViewById<MaterialButton>(R.id.button_turn_left).setOnClickListener {
            signOnTheSchema.rotation = getNewRotation(signOnTheSchema.rotation, -90)
            imageView.rotateToHeading(signOnTheSchema.rotation)
            onButtonRotatePressed(-90)
        }
        return view
    }

    fun ImageView.rotateToHeading(signRotation: SignRotation){
        when (signRotation) {
            SignRotation.TOP -> {
                this.rotation = 0f
            }
            SignRotation.BOTTOM -> {
                this.rotation = 180f
            }
            SignRotation.LEFT -> {
                this.rotation = 270f
            }
            SignRotation.RIGHT -> {
                this.rotation = 90f
            }
        }
    }

    fun getNewRotation(currentRotation: SignRotation, degrees: Int): SignRotation{
        val newRotation: SignRotation
        when (currentRotation) {
            SignRotation.BOTTOM -> {
                if (degrees == -90) {
                    newRotation = SignRotation.RIGHT
                }else{
                    newRotation = SignRotation.LEFT
                }
            }
            SignRotation.TOP -> {
                if (degrees == -90) {
                    newRotation = SignRotation.LEFT
                }else{
                    newRotation = SignRotation.RIGHT
                }
            }
            SignRotation.RIGHT -> {
                if (degrees == -90) {
                    newRotation = SignRotation.TOP
                }else{
                    newRotation = SignRotation.BOTTOM
                }
            }
            SignRotation.LEFT -> {
                if (degrees == -90) {
                    newRotation = SignRotation.BOTTOM
                }else{
                    newRotation = SignRotation.TOP
                }
            }
        }
        return newRotation
    }


    private fun onButtonChangePressed() {
        listener?.changeSign(signIndex)
    }

    private fun onButtonDeletePressed() {
        listener?.deleteSign(signIndex)
    }

    private fun onButtonRotatePressed(degree: Int){
        listener?.rotateDegrees(degree, signIndex)
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
        fun rotateDegrees(degree: Int, signIndex: Int)
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
