package cs.ut.ee.rallikuulekus.functions

import android.content.Context
import android.content.res.TypedArray
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.Sign
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema

internal fun generateSignList(rkClass: Int, context: Context): ArrayList<Sign>{
    //Initial values for arrays
    var arrayForDrawables: TypedArray = context.resources.obtainTypedArray(R.array.RK1_signs)
    var arrayForDescription: TypedArray = context.resources.obtainTypedArray(R.array.RK1_descriptions)
    var arrayForHeadings: TypedArray = context.resources.obtainTypedArray(R.array.RK1_hedings)


    when (rkClass){
        1 -> {
            arrayForDrawables = context.resources.obtainTypedArray(R.array.RK1_signs)
            arrayForDescription = context.resources.obtainTypedArray(R.array.RK1_descriptions)
            arrayForHeadings = context.resources.obtainTypedArray(R.array.RK1_hedings)
        }
        2 -> {
            arrayForDrawables = context.resources.obtainTypedArray(R.array.RK2_signs)
            arrayForDescription = context.resources.obtainTypedArray(R.array.RK2_descriptions)
            arrayForHeadings = context.resources.obtainTypedArray(R.array.RK2_headings)
        }
        3 ->{
            arrayForDrawables = context.resources.obtainTypedArray(R.array.RK3_signs)
            arrayForDescription = context.resources.obtainTypedArray(R.array.RK3_descriptions)
            arrayForHeadings = context.resources.obtainTypedArray(R.array.RK3_headings)
        }
        4 ->{
            arrayForDrawables = context.resources.obtainTypedArray(R.array.RK4_signs)
            arrayForDescription = context.resources.obtainTypedArray(R.array.RK4_descriptions)
            arrayForHeadings = context.resources.obtainTypedArray(R.array.RK4_headings)
        }
    }


    val sign_list_for_adapter: ArrayList<Sign> = ArrayList()
    //Signs are constructed in this loop
    for (number in 0 until arrayForDrawables.length()){
        val resourceIdForSignDrawable = arrayForDrawables.getResourceId(number, -1)
        val resourceIdForSignDescription = arrayForDescription.getResourceId(number, -1)
        val resourceForSignHeading = arrayForHeadings.getResourceId(number, -1)
        sign_list_for_adapter.add(
            Sign(context.resources.getDrawable(resourceIdForSignDrawable, null), context.resources.getString(resourceForSignHeading),
                context.resources.getString(resourceIdForSignDescription))
        )
    }

    //TypedArrays must be recycled after use
    arrayForDrawables.recycle()
    arrayForDescription.recycle()
    arrayForHeadings.recycle()

    return sign_list_for_adapter
}