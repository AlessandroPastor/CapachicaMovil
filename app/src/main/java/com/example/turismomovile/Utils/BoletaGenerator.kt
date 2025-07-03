package com.example.turismomovile.Utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.example.turismomovile.data.remote.dto.ventas.Payments
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO

object BoletaGenerator {
    private const val PAGE_WIDTH = 595 // A4 width in points (210mm)
    private const val PAGE_HEIGHT = 842 // A4 height in points (297mm)
    private const val MARGIN = 40
    private const val LINE_SPACING = 20
    private const val TITLE_SIZE = 24f
    private const val HEADER_SIZE = 18f
    private const val SUBHEADER_SIZE = 14f
    private const val NORMAL_TEXT_SIZE = 12f
    private const val SMALL_TEXT_SIZE = 10f

    fun generateBoleta(context: Context, reserva: ReservaUsuarioDTO?, payment: Payments?): File? {
        if (payment == null || reserva == null) return null

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Configuración de pinturas
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = TITLE_SIZE
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val headerPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = HEADER_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }

        val subHeaderPaint = Paint().apply {
            color = Color.BLACK
            textSize = SUBHEADER_SIZE
            typeface = Typeface.DEFAULT_BOLD
        }

        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = NORMAL_TEXT_SIZE
        }

        val smallPaint = Paint().apply {
            color = Color.GRAY
            textSize = SMALL_TEXT_SIZE
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 2f
        }

        // Fondo con gradiente
        val gradient = LinearGradient(
            0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(),
            Color.parseColor("#F5F5F5"), Color.parseColor("#FFFFFF"), Shader.TileMode.MIRROR
        )
        canvas.drawPaint(Paint().apply { shader = gradient })

        // Logo (puedes reemplazar esto con tu logo real)
        val logoPaint = Paint().apply {
            color = Color.BLUE
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Turismo Móvil", (PAGE_WIDTH/2).toFloat(), (MARGIN + 30).toFloat(), logoPaint)

        // Línea decorativa
        canvas.drawLine(
            MARGIN.toFloat(), (MARGIN + 50).toFloat(),
            (PAGE_WIDTH - MARGIN).toFloat(), (MARGIN + 50).toFloat(),
            Paint().apply {
                color = Color.BLUE
                strokeWidth = 3f
            }
        )

        var yPos = MARGIN + 80

        // Título
        canvas.drawText("COMPROBANTE DE PAGO", (PAGE_WIDTH/2).toFloat(), yPos.toFloat(), titlePaint)
        yPos += 40

        // Información de la empresa
        canvas.drawText("Municipalidad de Capachica", MARGIN.toFloat(), yPos.toFloat(), headerPaint)
        yPos += LINE_SPACING
        canvas.drawText("RUC: 20192140448", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
        yPos += LINE_SPACING
        canvas.drawText("Plaza de Armas, Capachica", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
        yPos += LINE_SPACING
        canvas.drawText("Teléfono: +051-1234567", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
        yPos += LINE_SPACING
        canvas.drawText("Email: municipalidad@capachica.gob.pe", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
        yPos += 30

        // Línea separadora
        canvas.drawLine(MARGIN.toFloat(), yPos.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPos.toFloat(), linePaint)
        yPos += 20

        // Información del cliente
        canvas.drawText("DATOS DEL CLIENTE", MARGIN.toFloat(), yPos.toFloat(), subHeaderPaint)
        yPos += LINE_SPACING
        reserva.user?.let { user ->
            user.name?.let {
                canvas.drawText("Nombre: $it", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
                yPos += LINE_SPACING
            }
            user.email?.let {
                canvas.drawText("Email: $it", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
                yPos += LINE_SPACING
            }
        }
        reserva.user?.name?.let {
            canvas.drawText("DNI/RUC: $it", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }
        yPos += 10

        // Línea separadora
        canvas.drawLine(MARGIN.toFloat(), yPos.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPos.toFloat(), linePaint)
        yPos += 20

        // Detalles de la reserva
        canvas.drawText("DETALLES DE LA RESERVA", MARGIN.toFloat(), yPos.toFloat(), subHeaderPaint)
        yPos += LINE_SPACING
        reserva.code?.let {
            canvas.drawText("Código de reserva: $it", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }
        reserva.created_at?.let {
            val formattedDate = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                formatter.format(parser.parse(it) ?: it)
            } catch (e: Exception) {
                it
            }
            canvas.drawText("Fecha de reserva: $formattedDate", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }

        // Detalles de los servicios reservados
        if (reserva.reserve_details.isNotEmpty()) {
            yPos += 10
            canvas.drawText("Servicios contratados:", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING

            // Definimos columnas fijas
            val colDescX = MARGIN.toFloat()
            val colCantX = (PAGE_WIDTH - MARGIN - 210).toFloat()
            val colPrecioX = (PAGE_WIDTH - MARGIN - 120).toFloat()
            val colTotalX = (PAGE_WIDTH - MARGIN).toFloat()

            // Altura de cada fila
            val rowHeight = LINE_SPACING + 4

            // Cabecera
            canvas.drawRect(
                MARGIN.toFloat(), yPos.toFloat(),
                (PAGE_WIDTH - MARGIN).toFloat(), (yPos + rowHeight).toFloat(),
                Paint().apply { color = Color.LTGRAY; style = Paint.Style.FILL }
            )

            canvas.drawText("Descripción", colDescX + 5, (yPos + 15).toFloat(), subHeaderPaint)
            canvas.drawText("Cantidad", colCantX, (yPos + 15).toFloat(), subHeaderPaint)
            canvas.drawText("Precio", colPrecioX, (yPos + 15).toFloat(), subHeaderPaint)
            canvas.drawText("Total", colTotalX, (yPos + 15).toFloat(), subHeaderPaint.apply {
                textAlign = Paint.Align.RIGHT
            })
            yPos += rowHeight

            // Filas de detalles
            for (detail in reserva.reserve_details) {
                // Fondo alterno si deseas (tipo zebra)
                val rowRect = RectF(
                    MARGIN.toFloat(), yPos.toFloat(),
                    (PAGE_WIDTH - MARGIN).toFloat(), (yPos + rowHeight).toFloat()
                )
                canvas.drawRect(rowRect, Paint().apply {
                    color = Color.parseColor("#FAFAFA")
                    style = Paint.Style.FILL
                })

                // Contenido
                val desc = detail.emprendimiento_service?.name ?: "Servicio"
                canvas.drawText(desc, colDescX + 5, (yPos + 15).toFloat(), normalPaint)
                canvas.drawText(detail.cantidad.toString(), colCantX, (yPos + 15).toFloat(), normalPaint)
                canvas.drawText("S/ ${detail.BI ?: "0.00"}", colPrecioX, (yPos + 15).toFloat(), normalPaint)
                canvas.drawText("S/ ${detail.total ?: "0.00"}", colTotalX, (yPos + 15).toFloat(), normalPaint.apply {
                    textAlign = Paint.Align.RIGHT
                })

                // Línea inferior
                canvas.drawLine(
                    MARGIN.toFloat(), (yPos + rowHeight).toFloat(),
                    (PAGE_WIDTH - MARGIN).toFloat(), (yPos + rowHeight).toFloat(),
                    linePaint
                )
                yPos += rowHeight
            }

            yPos += 10
        }


        // Detalles del pago
        canvas.drawText("DETALLES DEL PAGO", MARGIN.toFloat(), yPos.toFloat(), subHeaderPaint)
        yPos += LINE_SPACING
        payment.code?.let {
            canvas.drawText("Código de pago: $it", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }
        payment.created_at?.let {
            val formattedDate = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                formatter.format(parser.parse(it) ?: it)
            } catch (e: Exception) {
                it // Si falla el parseo, mostramos la cadena tal como viene
            }
            canvas.drawText("Fecha de pago: $formattedDate", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }


        canvas.drawText("Método de pago: Visa (****4852)", MARGIN.toFloat(), yPos.toFloat(), normalPaint)
        yPos += LINE_SPACING

        // Totales
        yPos += 10
        canvas.drawLine((PAGE_WIDTH - MARGIN - 150).toFloat(), yPos.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPos.toFloat(), linePaint)
        yPos += LINE_SPACING

        payment.igv?.let {
            canvas.drawText("IGV (18%): S/ $it", (PAGE_WIDTH - MARGIN - 150).toFloat(), yPos.toFloat(), normalPaint)
            yPos += LINE_SPACING
        }

        payment.total?.let {
            canvas.drawText("TOTAL PAGADO:", (PAGE_WIDTH - MARGIN - 150).toFloat(), yPos.toFloat(), subHeaderPaint)
            canvas.drawText("S/ $it", (PAGE_WIDTH - MARGIN).toFloat(), yPos.toFloat(), headerPaint)
            yPos += LINE_SPACING
        }

        // Línea final
        canvas.drawLine(MARGIN.toFloat(), yPos.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPos.toFloat(), linePaint)
        yPos += 30

        // Mensaje de agradecimiento
        canvas.drawText("¡Gracias por su compra!", (PAGE_WIDTH/2).toFloat(), yPos.toFloat(), subHeaderPaint.apply {
            textAlign = Paint.Align.CENTER
            color = Color.BLUE
        })
        yPos += LINE_SPACING
        canvas.drawText("Para cualquier consulta, contacte a nuestro servicio al cliente",
            (PAGE_WIDTH/2).toFloat(), yPos.toFloat(), smallPaint.apply {
                textAlign = Paint.Align.CENTER
            })

        // Pie de página
        yPos = PAGE_HEIGHT - MARGIN
        canvas.drawText("Este documento es válido como comprobante de pago",
            (PAGE_WIDTH/2).toFloat(), yPos.toFloat(), smallPaint.apply {
                textAlign = Paint.Align.CENTER
            })
        yPos += LINE_SPACING
        canvas.drawText("© ${Calendar.getInstance().get(Calendar.YEAR)} Turismo Móvil - Todos los derechos reservados",
            (PAGE_WIDTH/2).toFloat(), yPos.toFloat(), smallPaint.apply {
                textAlign = Paint.Align.CENTER
            })

        pdfDocument.finishPage(page)

        // Guardar el archivo
        val dir = context.getExternalFilesDir(null)
        val fileName = "Boleta_${reserva.code ?: "RES"}_${payment.code ?: "PAG"}.pdf"
        val file = File(dir, fileName)

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