package com.rodriguez.riceretailmaster.util

import java.io.IOException

fun Throwable.userMessage(): String =
    message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."

fun Throwable.loginMessage(): String {
    val msg = (message ?: "").lowercase()
    return when {
        "invalid login credentials" in msg ||
            "invalid_credentials" in msg ||
            "invalid grant" in msg ||
            "invalid_grant" in msg ->
            "Incorrect email or password. Please try again."

        "email not confirmed" in msg || "email_not_confirmed" in msg ->
            "This account's email hasn't been confirmed yet."

        "too many requests" in msg || "rate limit" in msg || "request_rate_limit" in msg ->
            "Too many attempts. Please wait a moment and try again."

        this is IOException ||
            "unable to resolve host" in msg ||
            "failed to connect" in msg ||
            "timeout" in msg ||
            "timed out" in msg ->
            "Can't reach the server. Check your internet connection."

        else -> userMessage()
    }
}
