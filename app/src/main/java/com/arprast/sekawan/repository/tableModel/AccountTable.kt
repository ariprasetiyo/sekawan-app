package com.arprast.sekawan.repository.tableModel

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AccountTable : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    lateinit var title: String
    lateinit var username: String
    lateinit var password: String
    lateinit var accountType: String
    lateinit var description: String
    lateinit var createDate: Date
}