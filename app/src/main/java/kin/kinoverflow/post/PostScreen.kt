package kin.kinoverflow.post

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import kin.kinoverflow.R
import kin.kinoverflow.model.Answer
import kin.kinoverflow.model.Owner
import kin.kinoverflow.model.Question
import kin.kinoverflow.model.User
import kin.kinoverflow.network.KinOverflowDb
import kin.kinoverflow.network.StackOverflowApi
import kin.kinoverflow.transaction.TransactionDialog
import kin.kinoverflow.user.UserManager
import kin.kinoverflow.utils.plusAssign
import kin.sdk.core.KinClient


class PostScreen @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
        private val question: Question = Question(),
        private val answerFakedUser: User?,
        private val kinClient: KinClient,
        private val userManager: UserManager
) : FrameLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.recycler_post) lateinit var recycler: RecyclerView

    private val stackOverflowApi: StackOverflowApi = StackOverflowApi()
    private val postAdapter = PostAdapter()
    private var disposables: CompositeDisposable = CompositeDisposable()
    private val transactionDialog = TransactionDialog(context, kinClient)
    private lateinit var answers: List<Answer>

    init {
        val view = inflate(context, R.layout.post_screen, this)
        view?.let { ButterKnife.bind(this, it) }
        recycler.adapter = postAdapter
        recycler.layoutManager = LinearLayoutManager(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        disposables = CompositeDisposable()
        disposables += Single.zip(
                stackOverflowApi.getAnswers(question.questionId),
                KinOverflowDb.getKinPerQuestionMap().first(HashMap()),
                KinOverflowDb.getKinPerAnswerMap().first(HashMap()),
                Function3 { answers: List<Answer>, questionsMap: Map<String, Long>, answersMap: Map<String, Long> ->
                    Triple(answers, questionsMap, answersMap)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { postAdapter.updateQuestion(question, null) }
                .subscribe { triple ->
                    answers = triple.first
                    if (answerFakedUser != null && !answers.isEmpty()) {
                        val user = answerFakedUser
                        val list = ArrayList(answers)
                        list[0] = answers[0].copy(owner = Owner(reputation = user.reputation, acceptRate = user.acceptRate, displayName = user.displayName,
                                link = user.link, profileImage = user.profileImage, userId = user.userId, userType = user.userType))
                        answers = list
                    }
                    val kin = triple.second[question.questionId.toString()]
                    postAdapter.updateAnswers(answers, triple.third)
                    postAdapter.updateQuestion(question, kin)
                }

        disposables += postAdapter.answerGiveKinClickEvents()
                .flatMapSingle { answer ->
                    KinOverflowDb.getUsersAddressMap()
                            .first(HashMap())
                            .flatMap { map ->
                                var address = map[answer.owner.userId.toString()]
                                if (address == null)
                                    address = "ff2092ebf61aefb0e6f5d4ec208b84b9d9fd2ec9"
                                transactionDialog.showTransactionDialog(address, answer.owner.displayName)
                                        .map { status -> Pair(status, answer) }
                            }

                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pair ->
                    val status = pair.first
                    when (status) {
                        is TransactionDialog.TransactionCancelled ->
                            Toast.makeText(context, "Pay Kin Operation Cancelled", Toast.LENGTH_SHORT).show()
                        is TransactionDialog.TransactionFailed ->
                            Toast.makeText(context, "Failed to pay Kin", Toast.LENGTH_SHORT).show()
                        is TransactionDialog.TransactionSucceed -> {
                            Toast.makeText(context, "Payment done successfully", Toast.LENGTH_SHORT).show()
                            KinOverflowDb.setKinToAnswer(pair.second.answerId.toString(), status.kin)
                            KinOverflowDb.getKinPerAnswerMap()
                                    .subscribe { answersMap ->
                                        val map = HashMap(answersMap)
                                        map.put(pair.second.answerId.toString(), status.kin)
                                        postAdapter.updateAnswers(answers, map)
                                    }
                        }
                    }
                }

        disposables += postAdapter.questionGiveKinClickEvents()
                .flatMapSingle {
                    KinOverflowDb.getUsersAddressMap()
                            .first(HashMap())
                }
                .flatMap { map ->
                    var address: String = map[question.owner.userId.toString()].orEmpty()
                    if (address.isEmpty())
                        address = "ff2092ebf61aefb0e6f5d4ec208b84b9d9fd2ec9"
                    transactionDialog.showTransactionDialog(address, question.owner.displayName)
                            .toObservable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { status ->
                    when (status) {
                        is TransactionDialog.TransactionCancelled ->
                            Toast.makeText(context, "Pay Kin Operation Cancelled", Toast.LENGTH_SHORT).show()
                        is TransactionDialog.TransactionFailed ->
                            Toast.makeText(context, "Failed to pay Kin", Toast.LENGTH_SHORT).show()
                        is TransactionDialog.TransactionSucceed -> {
                            Toast.makeText(context, "Payment done successfully", Toast.LENGTH_SHORT).show()
                            KinOverflowDb.setKinToQuestion(question.questionId.toString(), status.kin)
                            postAdapter.updateQuestion(question, status.kin)
                        }
                    }
                }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposables.clear()
    }

}