package cs.ut.ee.rallikuulekus.views

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import cs.ut.ee.rallikuulekus.R
import kotlin.math.round


class Grid(context: Context, val widthScreen: Int, val heightScreen: Int): View(context){
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = resources.getDimensionPixelSize(R.dimen.grid_line_width).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val spaceWidth = resources.getDimensionPixelSize(R.dimen.grid_space)
        val lineWidth = resources.getDimensionPixelSize(R.dimen.grid_line_width).toFloat()

        var xValue: Float = spaceWidth.toFloat()
        while (xValue < widthScreen){
            canvas!!.drawLine(xValue, 0f, xValue, heightScreen.toFloat(), paint)
            xValue += spaceWidth
            xValue += lineWidth
        }

        var yValue: Float = spaceWidth.toFloat()
        while (yValue < heightScreen){
            canvas!!.drawLine(0f, yValue, widthScreen.toFloat(), yValue, paint)
            yValue += spaceWidth
            yValue += lineWidth
        }
    }
}