package ec.edu.monster.vista

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CanchaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#046c4e")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.argb(180, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.argb(60, 255, 255, 255)
        textSize = 28f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // 1. Fondo de la cancha
        val rect = RectF(0f, 0f, w, h)
        canvas.drawRoundRect(rect, 20f, 20f, bgPaint)

        // 2. Línea exterior
        val border = 10f
        canvas.drawRect(border, border, w - border, h - border, linePaint)

        // 3. Línea central
        val centerX = w / 2f
        canvas.drawLine(centerX, border, centerX, h - border, linePaint)

        // 4. Círculo central
        canvas.drawCircle(centerX, h / 2f, 30f, linePaint)

        // 5. Área penal izquierda
        canvas.drawRect(border, h / 2f - 40f, border + 40f, h / 2f + 40f, linePaint)

        // 6. Área penal derecha
        canvas.drawRect(w - border - 40f, h / 2f - 40f, w - border, h / 2f + 40f, linePaint)

        // 7. Texto FIFA 2026
        canvas.drawText("FIFA 2026", centerX, h / 2f + 10f, textPaint)
    }
}
