package com.dev.sk.xchangehub.utils



data class AppException(
    val errorCode: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    override fun toString(): String {
        return "AppException(errorCode=$errorCode, message=${message ?: "N/A"})"
    }
}

val CURRENCY_NOT_AVL_EXCEPTION = AppException(4002, "Selected currency not available")
val BASE_CURRENCY_NOT_FOUND_EXCEPTION = AppException(4003, "Base currency not found in the provided currency map")
val BASE_CURRENCY_AMOUNT_IS_ZERO_EXCEPTION = AppException(4004, "Base currency amount is zero")