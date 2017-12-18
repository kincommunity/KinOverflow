package kin.kinoverflow.user

import android.content.Context
import io.reactivex.Single
import kin.kinoverflow.model.User
import kin.kinoverflow.network.StackOverflowApi


class UserManager(private val context: Context) {

    private var userId: Int
    private val api: StackOverflowApi = StackOverflowApi()
    private var cachedUser: Single<User>
    private var cachedFakedOtherUser: Single<User>

    init {
        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", 3903847) //default is yosriz profile
        val fakedUserId = sharedPref.getInt("faked_user_id", 9113876) //default is berryve profile
        cachedUser = api.getUserDetails(userId)
                .cache()

        cachedUser
                .subscribe()

        cachedFakedOtherUser = api.getUserDetails(fakedUserId)
                .cache()

        cachedFakedOtherUser
                .subscribe()
    }

    fun getUser(): Single<User> {
        return cachedUser
    }

    fun getOtherUser(): Single<User> {
        return cachedFakedOtherUser
    }

}