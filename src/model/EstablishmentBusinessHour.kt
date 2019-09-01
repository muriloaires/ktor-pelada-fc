package model

import java.util.*

data class EstablishmentBusinessHour(
    val isOpen : Boolean,
    val openingTime : Date,
    val closingTime: Date,
    val dayOfWeek : Int
)