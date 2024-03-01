package com.arprast.sekawan.repository.tableModel

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AuthTable : RealmObject() {
    @PrimaryKey
    lateinit var userId: String
    lateinit var token: String
    lateinit var roles: String
    lateinit var createDate: Date
    lateinit var updateDate: Date
}