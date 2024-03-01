package com.arprast.sekawan.repository.tableModel

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AuthTable : RealmObject() {
    @PrimaryKey
    var userId: String? = null
    var token: String? = null
    var roles: String? = null
    var createDate: Date? = null
    var updateDate: Date? = null
}