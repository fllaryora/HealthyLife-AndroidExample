package com.example.test2.data.entities.enums


import java.time.DayOfWeek

enum class DaysOfWeekEnum (val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(4),
    THURSDAY(8),
    FRIDAY(16),
    SATURDAY(32),
    SUNDAY(64),
    ALL(64+32+16+8+4+2+1);

    companion object {
        /**
         * return the first day that it find
         */
        @JvmStatic
        fun getFirstMatch(status: Int): DaysOfWeekEnum =
            DaysOfWeekEnum.values().find { value -> value.applyOn(status) }!!

        @JvmStatic
        fun fromDayOfWeek(day: DayOfWeek): DaysOfWeekEnum =
            when(day){
                java.time.DayOfWeek.MONDAY -> DaysOfWeekEnum.MONDAY
                java.time.DayOfWeek.TUESDAY -> DaysOfWeekEnum.TUESDAY
                java.time.DayOfWeek.WEDNESDAY -> DaysOfWeekEnum.WEDNESDAY
                java.time.DayOfWeek.THURSDAY -> DaysOfWeekEnum.THURSDAY
                java.time.DayOfWeek.FRIDAY -> DaysOfWeekEnum.FRIDAY
                java.time.DayOfWeek.SATURDAY -> DaysOfWeekEnum.SATURDAY
                java.time.DayOfWeek.SUNDAY -> DaysOfWeekEnum.SUNDAY
            }
        @JvmStatic
        fun toDayOfWeek(day: DaysOfWeekEnum): DayOfWeek =
            when(day){
                DaysOfWeekEnum.MONDAY ->  java.time.DayOfWeek.MONDAY
                DaysOfWeekEnum.TUESDAY ->  java.time.DayOfWeek.TUESDAY
                DaysOfWeekEnum.WEDNESDAY ->  java.time.DayOfWeek.WEDNESDAY
                DaysOfWeekEnum.THURSDAY ->  java.time.DayOfWeek.THURSDAY
                DaysOfWeekEnum.FRIDAY ->  java.time.DayOfWeek.FRIDAY
                DaysOfWeekEnum.SATURDAY ->  java.time.DayOfWeek.SATURDAY
                DaysOfWeekEnum.SUNDAY ->  java.time.DayOfWeek.SUNDAY
                DaysOfWeekEnum.ALL ->  java.time.DayOfWeek.SUNDAY
            }

        /**
         * get the first day that match
         */
        @JvmStatic
        fun getFirstDay(today: DayOfWeek, daysOfWeek: Int): DayOfWeek {
            //days in setted days
            val days : List<DaysOfWeekEnum> = DaysOfWeekEnum.values().filter {
                    value -> value.applyOn(daysOfWeek) }
            //for each day of the week starting from today
            for(eachDay in fromDayOfWeek(today).getList()) {
                if(days.contains(eachDay)){
                    return  toDayOfWeek(eachDay)
                }
            }
            return DayOfWeek.SUNDAY
        }
    }


    /**
     * return true if daysOfWeek contains this day
     */
    fun applyOn(daysOfWeek: Int): Boolean {
        val mask : Int = this.value and daysOfWeek
        return mask != 0
    }

    /**
     * Get day list starting from a particular day.
     */
    fun getList(): List<DaysOfWeekEnum> {
        return when(this){
            MONDAY -> listOf<DaysOfWeekEnum>(MONDAY, TUESDAY,
                WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
            TUESDAY -> listOf<DaysOfWeekEnum>(TUESDAY,
                WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY)
            WEDNESDAY -> listOf<DaysOfWeekEnum>(
                WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY,)
            THURSDAY -> listOf<DaysOfWeekEnum>(
                THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY,WEDNESDAY,)
            FRIDAY -> listOf<DaysOfWeekEnum>(
                FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY,WEDNESDAY, THURSDAY,)
            SATURDAY -> listOf<DaysOfWeekEnum>(
                SATURDAY, SUNDAY, MONDAY, TUESDAY,WEDNESDAY, THURSDAY, FRIDAY, )
            SUNDAY -> listOf<DaysOfWeekEnum>(
                SUNDAY, MONDAY, TUESDAY,WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, )
            ALL -> listOf<DaysOfWeekEnum>(MONDAY, TUESDAY,
                WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
        }
    }

    /**
     * amount of days between
     * this day and other day
     */
    fun getDistance(day: DaysOfWeekEnum): Int {
        when(this){
            ALL -> return 0
            MONDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 0
                    TUESDAY -> 1
                    WEDNESDAY -> 2
                    THURSDAY -> 3
                    FRIDAY -> 4
                    SATURDAY -> 5
                    SUNDAY -> 6
                }
            }
            TUESDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 6
                    TUESDAY -> 0
                    WEDNESDAY -> 1
                    THURSDAY -> 2
                    FRIDAY -> 3
                    SATURDAY -> 4
                    SUNDAY -> 5
                }
            }
            WEDNESDAY-> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 5
                    TUESDAY -> 6
                    WEDNESDAY -> 0
                    THURSDAY -> 1
                    FRIDAY -> 2
                    SATURDAY -> 3
                    SUNDAY -> 4
                }
            }
            THURSDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 4
                    TUESDAY -> 5
                    WEDNESDAY -> 6
                    THURSDAY -> 0
                    FRIDAY -> 1
                    SATURDAY -> 2
                    SUNDAY -> 3
                }
            }
            FRIDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 3
                    TUESDAY -> 4
                    WEDNESDAY -> 5
                    THURSDAY -> 6
                    FRIDAY -> 0
                    SATURDAY -> 1
                    SUNDAY -> 2
                }
            }
            SATURDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 2
                    TUESDAY -> 3
                    WEDNESDAY -> 4
                    THURSDAY -> 5
                    FRIDAY -> 6
                    SATURDAY -> 0
                    SUNDAY -> 1
                }
            }
            SUNDAY -> {
                return when(day){
                    ALL -> 0
                    MONDAY -> 1
                    TUESDAY -> 2
                    WEDNESDAY -> 3
                    THURSDAY -> 4
                    FRIDAY -> 5
                    SATURDAY -> 6
                    SUNDAY -> 0
                }
            }
        }
    }



}