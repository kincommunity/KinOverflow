package kin.kinoverflow

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import kin.kinoverflow.network.AwardServiceMock
import kin.kinoverflow.network.KinOverflowDb
import kin.kinoverflow.post.PostScreen
import kin.kinoverflow.profile.ProfileScreen
import kin.kinoverflow.questions.QuestionsScreen
import kin.kinoverflow.user.UserManager
import kin.sdk.core.Balance
import kin.sdk.core.KinClient
import kin.sdk.core.ResultCallback
import kin.sdk.core.ServiceProvider
import kin.sdk.core.exception.EthereumClientException

const val PASSPHRASE = "1234"
private const val ROPSTEN_TEST_NET_URL = "http://parity.rounds.video:8545"

class MainActivity : AppCompatActivity() {

    @BindView(R.id.bottom_navigation) lateinit var bottomNavigation: BottomNavigationView
    @BindView(R.id.screen_holder) lateinit var screenHolder: ViewGroup
    private lateinit var questionsScreen: QuestionsScreen
    private lateinit var profileScreen: ProfileScreen
    private lateinit var userManger: UserManager

    private var kinClient: KinClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kinClient = initKinClient()
        userManger = UserManager(this)
        userManger.getUser()
                .doOnSuccess { user ->
                    kinClient?.account?.let {
                        KinOverflowDb.setAddressToUser(user.userId.toString(), it.publicAddress)
                    }
                }.subscribe()

        questionsScreen = QuestionsScreen(this, userManager = userManger)
        profileScreen = ProfileScreen(this, userManger = userManger)
        profileScreen.kinClient = kinClient

        ButterKnife.bind(this)

        screenHolder.removeAllViews()
        screenHolder.addView(questionsScreen)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    screenHolder.removeAllViews()
                    screenHolder.addView(profileScreen)
                }
                R.id.navigation_questions -> {
                    setQuestionScreen()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        setQuestionScreen()
    }

    private fun setQuestionScreen() {
        screenHolder.removeAllViews()
        screenHolder.addView(questionsScreen)
        questionsScreen.questionClickEvents()
                .subscribe { pair ->
                    if (!pair.second) {
                        screenHolder.removeAllViews()
                        kinClient?.let {
                            screenHolder.addView(PostScreen(this, question = pair.first, kinClient = it, answerFakedUser = null, userManager = userManger))
                        }
                    } else {
                        screenHolder.removeAllViews()
                        kinClient?.let {
                            userManger.getOtherUser()
                                    .subscribe { user ->
                                        screenHolder.addView(PostScreen(this, question = pair.first, kinClient = it, answerFakedUser = user, userManager = userManger))
                                    }

                        }
                    }
                }
    }

    private fun initKinClient(): KinClient? {
        try {
            var kinClient = KinClient(this,
                    ServiceProvider(ROPSTEN_TEST_NET_URL, ServiceProvider.NETWORK_ID_ROPSTEN))

            if (!kinClient.hasAccount()) {
                var account = kinClient.createAccount(PASSPHRASE)
                AwardServiceMock().awardKin(account.publicAddress)
            }

            return kinClient

        } catch (e: EthereumClientException) {
            e.printStackTrace()
        }

        return null
    }
}
