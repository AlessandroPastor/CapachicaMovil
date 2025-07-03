package com.example.turismomovile.presentation.screens.land_page

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import com.example.turismomovile.data.remote.dto.ventas.Payments
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO

object BoletaGenerator {
    fun generateBoleta(context: Context, reserva: ReservaUsuarioDTO?, payment: Payments?): File? {
        if (payment == null) return null
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var y = 20
        paint.textSize = 16f
        canvas.drawText("Boleta de Pago", 80f, y.toFloat(), paint)
        paint.textSize = 12f
        y += 30
        reserva?.code?.let {
            canvas.drawText("Reserva: $it", 20f, y.toFloat(), paint)
            y += 20
        }
        payment.code?.let {
            canvas.drawText("Pago: $it", 20f, y.toFloat(), paint)
            y += 20
        }
        payment.total?.let {
            canvas.drawText("Total: S/ $it", 20f, y.toFloat(), paint)
            y += 20
        }

        pdfDocument.finishPage(page)

        val dir = context.getExternalFilesDir(null)
        val file = File(dir, "boleta_${payment.code ?: "pago"}.pdf")
        return try {
            FileOutputStream(file).use { fos ->
                pdfDocument.writeTo(fos)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfDocument.close()
        }
    }
}
