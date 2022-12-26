package com.example.umbrella.common

import androidx.annotation.DrawableRes
import com.example.umbrella.R
import com.example.umbrella.api.WeatherDataItem
import retrofit2.Response

fun Int.toUTCformatedLocalTime(
    response: Response<WeatherDataItem>,
    returnOnlyHour: Boolean
): String {
    val timeZone = response.body()!!.timezone
    val timeDifference = timeZone / 3600
    val dateAndTimeRaw = java.time.format.DateTimeFormatter.ISO_INSTANT
        .format(java.time.Instant.ofEpochSecond(this.toLong()))
    val dateAndTimeStringList = dateAndTimeRaw.split("T")
    val hourAndMinuteStringList = dateAndTimeStringList[1].split(":").dropLast(1)
    val hour = hourAndMinuteStringList[0].toInt() + timeDifference
    val hourString = if (hour < 10) {
        "0"
    } else {
        ""
    } + hour.toString() + ":" + hourAndMinuteStringList[1]
    return if (returnOnlyHour) {
        hourString
    } else {
        val dateString = dateAndTimeStringList[0] + " " + hourString
        dateString
    }
}

@DrawableRes
fun String.mapToDrawableResource(): Int {
    return when (this) {
        "01d" -> {
            R.drawable._01d}
        "01n" -> {
            R.drawable._01n}
        "02d" -> {
            R.drawable._02d}
        "02n" -> {
            R.drawable._02n}
        "03d" -> {
            R.drawable._03d}
        "03n" -> {
            R.drawable._03d}
        "04d" -> {
            R.drawable._04d}
        "04n" -> {
            R.drawable._04d}
        "09d" -> {
            R.drawable._09d}
        "09n" -> {
            R.drawable._09d}
        "10d" -> {
            R.drawable._10d}
        "10n" -> {
            R.drawable._10n}
        "11d" -> {
            R.drawable._11d}
        "11n" -> {
            R.drawable._11d}
        "13d" -> {
            R.drawable._13d}
        "13n" -> {
            R.drawable._13d}
        "50d" -> {
            R.drawable._50d}
        "50n" -> {
            R.drawable._50n}
        else -> {R.drawable._defaultweathericon}
    }
}