package dev.farukh.network.core

class UploadProgress<ID, DATA>(
    val id: ID,
    val sent: Long,
    val total: Long,
    val data: DATA
)