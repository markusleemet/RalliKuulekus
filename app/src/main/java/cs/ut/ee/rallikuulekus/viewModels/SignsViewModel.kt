package cs.ut.ee.rallikuulekus.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema

class SignsViewModel() : ViewModel() {
    var signs = ArrayList<SignOnTheSchema>()


    fun addSignToSigns(sign: SignOnTheSchema){
        signs.add(sign)
        Log.i("viewModel", "signs count: ${signs.count()}")
    }

    fun getLastAddedSign(): SignOnTheSchema{
        return signs.last()
    }
}