package com.example.hows_this_day

import java.util.*


class BaseCalendar {

    companion object {
        const val DAYS_OF_WEEK = 7
        const val LOW_OF_CALENDAR = 6
    }

    val calendar = Calendar.getInstance()

    var prevMonthTailOffset = 0
    var nextMonthHeadOffset = 0
    var currentMonthMaxDate = 0

    var data = arrayListOf<Int>()

    init {
        calendar.time = Date()
    }

    /**
     * 달력 초기화
     */
    fun initBaseCalendar(refreshCallback: (Calendar) -> Unit) {
        makeMonthDate(refreshCallback)
    }

    /**
     * 이전 달로 이동
     */
    fun changeToPrevMonth(refreshCallback: (Calendar) -> Unit) {
        if(calendar.get(Calendar.MONTH) == 0){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }
        makeMonthDate(refreshCallback)
    }

    /**
     * 다음 달로 이동
     */
    fun changeToNextMonth(refreshCallback: (Calendar) -> Unit) {
        if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
            calendar.set(Calendar.MONTH, 0)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
        }
        makeMonthDate(refreshCallback)
    }

    /**
     * 며칠인지 data에 저장
     */
    private fun makeMonthDate(refreshCallback: (Calendar) -> Unit) {

        data.clear()

        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1

        makePrevMonthTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)

        nextMonthHeadOffset = LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)
        makeNextMonthHead()

        refreshCallback(calendar)
    }

    /**
     * 지금의 달에 이전달 표기
     */
    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevMonthTailOffset

        for (i in 1..prevMonthTailOffset) data.add(++maxOffsetDate)
    }

    /**
     * 이번달 표기
     */
    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) data.add(i)
    }

    /**
     * 지금의 달에 다음달 날짜 표기
     */
    private fun makeNextMonthHead() {
        var date = 1

        for (i in 1..nextMonthHeadOffset) data.add(date++)
    }
}