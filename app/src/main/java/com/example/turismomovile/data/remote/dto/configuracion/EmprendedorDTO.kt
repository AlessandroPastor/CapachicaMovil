package com.example.turismomovile.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Respuesta paginada general
@Serializable
data class EmprendedorResponse(
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int,
    val content: List<Emprendedor>,
)

// Modelo completo de Emprendedor
@Serializable
data class Emprendedor(
    val id: String? = null,
    val razon_social: String? = null,
    val address: String? = null,
    val code: String? = null,
    val ruc: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val lugar: String? = null,
    val img_logo: String? = null,
    val name_family: String? = null,
    val status: Int,
    val asociacion_id: String? = null,
    val nombre_asociacion: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null,
    val imagenes: List<Imagen> = emptyList(),
    val products: List<Producto> = emptyList()
)

// Imágenes del Emprendedor
@Serializable
data class Imagen(
    val id: String? = null,
    val url_image: String? = null,
    val estado: Boolean? = null,
    val code: String? = null
)

@Serializable
data class Producto(
    val emprendedor_service_id: String? = null,
    val productCode: String? = null,
    val productStatus: Int? = null,
    val cantidad: Int? = null,
    val name: String? = null,
    val description: String? = null,
    @Serializable(with = DoubleSerializer::class)
    val costo: Double? = null,
    @Serializable(with = DoubleSerializer::class)
    val costoUnidad: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val service_id: String? = null,
    val service_name: String? = null,
    val service_description: String? = null,
    val service_code: String? = null,
    val service_category: String? = null,
    val service_status: Int? = null,
    val imagenes: List<ImagenProducto> = emptyList(),
)



@Serializable
data class ImagenProducto(
    val id: String? = null,
    val url_image: String? = null,
    val estado: Boolean? = null,
    val code: String? = null,
)

// Para crear un nuevo emprendedor
@Serializable
data class EmprendedorCreateDTO(
    val asociacion_id: String,
    val razon_social: String,
    val address: String? = null,
    val code: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val lugar: String? = null,
    val img_logo: String? = null,
    val name_family: String? = null,
    val status: Boolean? = true,
    val imagenes: List<Imagen>? = null
)


// Estado para el ViewModel
data class EmprendedorState(
    val items: List<Emprendedor> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Emprendedor? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

object DoubleSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Double", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Double {
        return decoder.decodeString().toDouble()
    }
}
