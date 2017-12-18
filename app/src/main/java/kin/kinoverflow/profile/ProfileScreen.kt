package kin.kinoverflow.profile

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import kin.kinoverflow.R
import kin.kinoverflow.model.User
import kin.kinoverflow.user.UserManager
import kin.sdk.core.Balance
import kin.sdk.core.KinClient
import kin.sdk.core.Request
import kin.sdk.core.ResultCallback


class ProfileScreen @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val userManger: UserManager = UserManager(context)
) : ConstraintLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.prof_kin_balance) lateinit var kinBalance: TextView
    @BindView(R.id.prof_bronze_badge_count) lateinit var bronzeBadgeCount: TextView
    @BindView(R.id.prof_silver_badge_count) lateinit var silverBadgeCount: TextView
    @BindView(R.id.prof_gold_badge_count) lateinit var goldBadgeCount: TextView
    @BindView(R.id.prof_user_about) lateinit var aboutUser: TextView
    @BindView(R.id.prof_user_image) lateinit var userProfileImage: ImageView
    @BindView(R.id.prof_user_id) lateinit var userId: TextView
    @BindView(R.id.prof_reputation) lateinit var reputation: TextView

    lateinit var initialBalance: String
    var kinClient: KinClient? = null
    private var request: Request<Balance>? = null

    init {
        val view = FrameLayout.inflate(context, R.layout.profile, this)
        view?.let { ButterKnife.bind(this, it) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        userManger.getUser()
                .subscribe {
                    user -> updateUserDetails(user)
                }
        kinBalance.text = initialBalance
        refreshBalanceText()
        kinBalance.setOnClickListener {
            refreshBalanceText()
        }
    }

    private fun updateUserDetails(user: User){
        bronzeBadgeCount.text = user.badgeCounts.bronze.toString()
        silverBadgeCount.text = user.badgeCounts.silver.toString()
        goldBadgeCount.text = user.badgeCounts.gold.toString()
        aboutUser.text = removeXmlFormatting(user.aboutMe)
        Picasso.with(userProfileImage.context)
                .load(user.profileImage)
                .into(userProfileImage)
        userId.text = user.displayName
        reputation.text = user.reputation.toString()
    }

    private fun removeXmlFormatting(originalString: String) : String {
        var splitStringList = originalString.split(Regex("<.*?>"), 1000)

       return splitStringList.reduce(operation = { tags, tag ->
            return@reduce "$tags$tag"})
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (request != null) {
            request?.cancel(true)
            request = null
        }
    }

    private fun refreshBalanceText() {
        if (kinClient != null) {
            request = kinClient?.account?.pendingBalance
            request?.run(object : ResultCallback<Balance> {
                override fun onResult(balance: Balance) {
                    kinBalance.text = balance.value(0)
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }

}