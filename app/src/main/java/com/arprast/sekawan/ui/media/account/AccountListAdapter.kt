package com.arprast.sekawan.ui.media.account

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.arprastandroid.R
import com.arprast.sekawan.type.AccountType

class AccountListAdapter(
    val titleC: Array<String>,
    val usernameC: Array<String>,
    val passwordC: Array<String>,
    val descriptionC: Array<String>,
    val accountTypeC: Array<AccountType>,
    val accountIdC: Array<Long>,
    val contextC: Context
) : BaseAdapter() {

    private val title: Array<String> = titleC
    private val username: Array<String> = usernameC
    private val password: Array<String> = passwordC
    private val description: Array<String> = descriptionC
    private val accountType: Array<AccountType> = accountTypeC
    private val accountId: Array<Long> = accountIdC
    private val context: Context = contextC
    private val inflter: LayoutInflater = (LayoutInflater.from(contextC))

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflter.inflate(R.layout.account_layout_row_list, null);
        val titleView = view.findViewById<TextView>(R.id.account_row_list_title)

        view.findViewById<TextView>(R.id.account_row_list_description)
            .setText(description[position])

        titleView.setText("${title[position]} ${username[position]}")
        titleView.setTag(accountType[position].stringValue)

        when (accountType[position]) {
            AccountType.YOUTUBE ->
                view.findViewById<ImageView>(R.id.account_favicon_social_media).setImageResource(R.drawable.ic_youtube_icon)
            AccountType.TWITTER ->
                view.findViewById<ImageView>(R.id.account_favicon_social_media).setImageResource(R.drawable.ic_twitter_icon)
            AccountType.INSTAGRAM ->
                view.findViewById<ImageView>(R.id.account_favicon_social_media).setImageResource(R.drawable.ic_instagram_icon)
            AccountType.FACEBOOK ->
                view.findViewById<ImageView>(R.id.account_favicon_social_media).setImageResource(R.drawable.ic_facebook_icon)
            else ->
                view.findViewById<ImageView>(R.id.account_favicon_social_media).setImageResource(R.drawable.ic_unknown_icon)
        }

        val ttt = view.findViewById<TextView>(R.id.hidden_username_and_password)
        ttt.setText(username[position])
        ttt.setTag(password[position])

        val hiddenAccountId = view.findViewById<TextView>(R.id.hidden_account_id)
        hiddenAccountId.setText("${accountId[position]}")

//        view.findViewById<TextView>(R.id.youtube_row_list_password).setText(password[position])
        return view
    }

    override fun getItem(position: Int): Any {
        return titleC[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return title.size
    }

}