package web.model.incoming

import com.google.gson.annotations.SerializedName
import java.util.*

data class NewEstablishmentBusinessHour(
    @SerializedName("isOpen") private val _isOpen: Boolean?,
    @SerializedName("openingTime") private val _openingTime: Date?,
    @SerializedName("closingTime") private val _closingTime: Date?
) {
    val isOpen
        get() = _isOpen ?: throw IllegalArgumentException("isOpen is required")

    val openingTime: Date?
        get() {
            return if (isOpen) {
                _openingTime ?: throw IllegalArgumentException("openingTime is required when isOpen equals true")
            } else {
                _openingTime
            }
        }

    val closingTime: Date?
        get() {
            return if (isOpen) {
                _closingTime ?: throw IllegalArgumentException("openingTime is required when isOpen equals true")
            } else {
                _closingTime
            }
        }


}
