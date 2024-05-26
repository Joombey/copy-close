package dev.farukh.copyclose.features.order.list.data.dto

class OrderDTO(
    val orderID: String,
    val name: String,
    val id: String,
    val addressName: String,
    val icon: ByteArray,
    val services: List<Service>,
    val comment: String,
    val attachments: List<Attachment>
)