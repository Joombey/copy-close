package dev.farukh.auth.ui.sign_up

data class SignUpForm(
    val login: String,
    val password: String,
    val name: String,
    val address: String,
    val lat: String,
    val lon: String,
)