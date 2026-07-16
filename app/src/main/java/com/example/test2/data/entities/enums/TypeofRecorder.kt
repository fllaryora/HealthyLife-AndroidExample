package com.example.test2.data.entities.enums

enum class TypeofRecorder(val value: Int) {
    NONE(0),
    WEIGHT_RECORDER(1), WATER_RECORDER(2),
    PILL_RECORDER(3), NUMBER_TWO_RECORDER(4);

    companion object {
        @JvmStatic
        fun fromInt(status: Int): TypeofRecorder =
            values().find { value -> value.value == status } ?: NONE
    }
}