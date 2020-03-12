package cs.ut.ee.rallikuulekus.entities

import android.graphics.drawable.Drawable


data class SignOnTheSchema(
    var xCoordinate: Float,
    var yCoordinate: Float,
    var rkClass: Int,
    var drawable: Drawable,
    var heading: String,
    var description: String,
    var position: Int,
    var rotation: SignRotation){}