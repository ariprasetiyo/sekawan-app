package com.arprast.sekawan.ui.media.account

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.arprast.sekawan.MainActivity
import com.arprast.sekawan.model.Account
import com.arprast.sekawan.repository.AccountRepository
import com.arprast.sekawan.type.AccountType
import com.arprast.sekawan.ui.media.account.facebook.FacebookMainFragment
import com.arprast.sekawan.ui.media.account.instagram.InstagramMainFragment
import com.arprast.sekawan.ui.media.account.twitter.TwitterMainFragment
import com.arprast.sekawan.ui.media.account.youtube.YoutubeMainFragment
import com.arprastandroid.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.RealmResults

class AccountList(bottomNavigationView: BottomNavigationView?, it: FragmentActivity) : Fragment() {

    private val bottomNavigationView = bottomNavigationView
    private val it = it

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_account_list, container, false)

        val listView = root.findViewById<ListView>(R.id.account_list)
        val context = context as MainActivity

        val usernameList: ArrayList<String> = ArrayList()
        val passwordList: ArrayList<String> = ArrayList()
        val titleList: ArrayList<String> = ArrayList()
        val description: ArrayList<String> = ArrayList()
        val accountType: ArrayList<AccountType> = ArrayList()
        val accountId: ArrayList<Long> = ArrayList()

        val account = Account()
        account.accountType = AccountType.YOUTUBE.stringValue

        AccountRepository().getAccounts(account)
            .observe(it, Observer<RealmResults<Account>> { t ->
                for (accountData in t) {
                    usernameList.add(accountData.username)
                    passwordList.add(accountData.password)
                    titleList.add(accountData.title)
                    description.add(accountData.description)
                    accountType.add(AccountType.valueOfString(accountData.accountType))
                    accountId.add(accountData.id)
                }
                listView.adapter =
                    AccountListAdapter(
                        titleList.toTypedArray(),
                        usernameList.toTypedArray(),
                        passwordList.toTypedArray(),
                        description.toTypedArray(),
                        accountType.toTypedArray(),
                        accountId.toTypedArray(),
                        context
                    )

                itemOnClickListener(listView)
                itemLongClick(listView)

            })
        return root
    }

    private fun itemOnClickListener(listView: ListView) {
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

            bottomNavigationView?.visibility = View.GONE

            val hiddenField =
                view.findViewById<TextView>(R.id.hidden_username_and_password)
            val usernameFromList = hiddenField.text.toString()
            val passwordFromList = hiddenField.getTag().toString()
            val accountTypeView =
                view.findViewById<TextView>(R.id.account_row_list_title).getTag()
                    .toString()
            val accountType = AccountType.valueOfString(accountTypeView)


            when (accountType) {
                AccountType.FACEBOOK ->
                    openFragment(
                        FacebookMainFragment(
                            usernameFromList,
                            passwordFromList
                        )
                    )
                AccountType.YOUTUBE ->
                    openFragment(
                        YoutubeMainFragment(
                            usernameFromList,
                            passwordFromList
                        )
                    )
                AccountType.INSTAGRAM ->
                    openFragment(
                        InstagramMainFragment(
                            usernameFromList,
                            passwordFromList
                        )
                    )
                AccountType.TWITTER ->
                    openFragment(
                        TwitterMainFragment(
                            usernameFromList,
                            passwordFromList
                        )
                    )
                else -> {
                    bottomNavigationView?.visibility = View.VISIBLE
                    tostText("Not yet support")
                }
            }

        })
    }

    private fun itemLongClick(listView: ListView) {
        listView.setOnItemLongClickListener(OnItemLongClickListener { parent, view, pos, id ->

            val hiddenAccountId =
                view.findViewById<TextView>(R.id.hidden_account_id).text.toString().toLong()

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Account Action")
            builder.setMessage("Are you want to edit or delete ?")
            builder.setPositiveButton("Edit", DialogInterface.OnClickListener { dialog, idDialog ->
                openFragment(AddAccount(hiddenAccountId))
            })
            builder.setNegativeButton(
                "Delete",
                DialogInterface.OnClickListener { dialog, idDialog ->
                    deleteAccount(hiddenAccountId)
                })

            builder.create().show()
            true
        })
    }

    private fun deleteAccount(accountId : Long) {

        val account = Account()
        account.id = accountId
        AccountRepository().deleteAccount(account)
        openFragment(AccountList(bottomNavigationView, it))
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = getActivity()?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun tostText(text: String) {
        activity.let {
            val invalidateInputMesssage =
                Toast.makeText(it, text, Toast.LENGTH_LONG)
            invalidateInputMesssage.setGravity(Gravity.CENTER, 0, 0)
            invalidateInputMesssage.show()
        }
    }

}