package com.arprast.sekawan.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.arprast.sekawan.dao.Dao
import com.arprast.sekawan.dao.RealmLiveDataDao
import com.arprast.sekawan.repository.tableModel.AccountTable
import com.arprast.sekawan.repository.tableModel.AuthTable
import com.arprast.sekawan.repository.tableModel.UserInterfacing
import com.arprast.sekawan.util.PreferanceVariable.Companion.DEBUG_NAME
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class RealmDBRepository : ViewModel() {

    fun Realm.dataDao(): RealmLiveDataDao = RealmLiveDataDao(this)

    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    fun saveAuth(authTable : AuthTable) : Boolean{
        return realm.dataDao().saveAuth(authTable)
    }

    fun getAuth() : AuthTable?{
        return realm.dataDao().getAuth()
    }

    fun deleteAuth(authTable : AuthTable){
        realm.dataDao().deleteAuth(authTable)
    }

    fun saveUpdateAccount(accountTable: AccountTable, isUpdate: Boolean): Boolean {
        val deleteAccountTable = AccountTable()
        deleteAccountTable.id = accountTable.id
        val isSaveSuccess = saveAccount(accountTable)
        if (isSaveSuccess && isUpdate) {
            deleteAccount(deleteAccountTable)
        }
        return isSaveSuccess
    }

    private fun saveAccount(accountTable: AccountTable): Boolean {
        accountTable.id = Calendar.getInstance().timeInMillis
        accountTable.createDate = Date()
        Log.i(DEBUG_NAME, "dsbe success account=${accountTable.id}")
        return realm.dataDao().saveAccount(accountTable)
    }

    fun getAccounts(accountTable: AccountTable): LiveData<RealmResults<AccountTable>> {
        return realm.dataDao().getAccounts(accountTable)
    }

    fun getAccount(accountTable: AccountTable): AccountTable {
        val account = realm.dataDao().getAccount(accountTable)
        if (account == null || account.id <= 0) {
            return AccountTable()
        }
        return account
    }

    fun deleteAccount(accountTable: AccountTable) {
        realm.dataDao().deleteAccount(accountTable)
    }

    fun updateAccount(accountTable: AccountTable) {
        realm.dataDao().deleteAccount(accountTable)
    }

    fun insertUpdateUserInterfacing(userInterfacing: UserInterfacing) {
        userInterfacing.createDate = Date()
        realm.beginTransaction()
        val interfacing = Dao(realm).getUserInterface(userInterfacing)
        if (interfacing == null || interfacing.menuId.isEmpty()) {
            Dao(realm).saveUserInterfacing(userInterfacing)
        } else {
            Dao(realm).updateUserInterfaceSync(userInterfacing)
        }
        realm.commitTransaction()
    }

    fun updateUserInterface(userInterfacing: UserInterfacing) {
        userInterfacing.createDate = Date()
        realm.beginTransaction()
        Dao(realm).updateUserInterfaceSync(userInterfacing)
        realm.commitTransaction()
    }

    fun updateUserInterfaceLiveData(userInterfacing: UserInterfacing) {
        RealmLiveDataDao(realm).updateUserInterfacing(userInterfacing)
    }

    fun getUserInterfacing(userInterfacing: UserInterfacing): UserInterfacing {
        return Dao(realm).getUserInterface(userInterfacing)
    }

    override fun onCleared() {
        realm.close()
        super.onCleared()
    }

}