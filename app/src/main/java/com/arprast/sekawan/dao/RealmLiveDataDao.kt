package com.arprast.sekawan.dao

import androidx.lifecycle.LiveData
import com.arprast.sekawan.repository.tableModel.AccountTable
import com.arprast.sekawan.repository.tableModel.UserInterfacing
import com.arprast.sekawan.repository.RealmLiveData
import com.arprast.sekawan.repository.tableModel.AuthTable
import com.arprast.sekawan.util.PreferanceVariable.Companion.ID
import com.arprast.sekawan.util.PreferanceVariable.Companion.MENU_ID_FIELD
import com.arprast.sekawan.util.PreferanceVariable.Companion.userId
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


/**
 * https://proandroiddev.com/the-realm-of-kotlin-and-live-data-using-mvp-architecture-f04fc41c914e
 */
class RealmLiveDataDao(val realm: Realm) {

    fun <T : RealmModel> RealmResults<T>.asLiveData() =
        RealmLiveData<T>(this)

    fun saveAuth(authTable : AuthTable) : Boolean{
        return !realm.executeTransactionAsync {
            it.insertOrUpdate(authTable)
        }.isCancelled
    }

    fun getAuth(): AuthTable? {
        return realm.where(AuthTable::class.java)
            .findFirst()
    }

    fun deleteAuth(authTable: AuthTable) {
        realm.executeTransaction {
            val result = it.where(AuthTable::class.java).equalTo(userId, authTable.userId).findFirst()
            result?.deleteFromRealm()
        }
    }


    fun saveAccount(accountTable: AccountTable): Boolean {
        return !realm.executeTransactionAsync {
            it.insertOrUpdate(accountTable)
        }.isCancelled
    }

    /**
     * Get all accounts
     */
    fun getAccounts(accountTable: AccountTable): LiveData<RealmResults<AccountTable>> {
        return realm.where(AccountTable::class.java)
//            .equalTo("accountType", account.accountType)
            .findAllAsync().asLiveData()
    }

    fun getAccount(accountTable: AccountTable): AccountTable? {
        return realm.where(AccountTable::class.java)
            .equalTo(ID, accountTable.id)
            .findFirst()
    }

    fun updateUserInterfacing(userInterfacing: UserInterfacing) {

        val userinterface = realm.where(UserInterfacing::class.java)
            .equalTo(MENU_ID_FIELD, userInterfacing.menuId).findFirstAsync()
        userinterface.isDisabled = userInterfacing.isDisabled

    }

    fun getUserInterfacing(userInterfacing: UserInterfacing): LiveData<RealmResults<UserInterfacing>> {
        return realm.where(UserInterfacing::class.java)
            .equalTo(MENU_ID_FIELD, userInterfacing.menuId)
            .findAllAsync().asLiveData()
    }

    fun deleteAccount(accountTable: AccountTable) {
        realm.executeTransactionAsync {
            val result = it.where(accountTable::class.java).equalTo(ID, accountTable.id).findFirst()
            result?.deleteFromRealm()
        }
    }

}