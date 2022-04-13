package com.pashu.roadcastsaurabhassignment.data

data class EntriesResponse  (

    val count: Int?,
    val entries:List<Entries>?

)

data class Entries (

    val API: String?,
    val Description: String?,
    val Auth: String?,
    val HTTPS: Boolean?,
    val Cors: String?,
    val Link: String?,
    val Category: String?

)