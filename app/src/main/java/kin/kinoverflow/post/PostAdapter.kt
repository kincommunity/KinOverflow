package kin.kinoverflow.post

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kin.kinoverflow.R
import kin.kinoverflow.model.Answer
import kin.kinoverflow.model.Question
import kin.kinoverflow.questions.ViewHolderClickListener


private const val ID_QUESTION: Int = 0
private const val ID_ANSWER: Int = 1

class PostAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var question: Question
    private var questionKin: Long? = null
    private val answers: ArrayList<Answer> = ArrayList()
    private val kinMap: HashMap<String, Long> = HashMap()
    private val answersClicksSubject = PublishSubject.create<Answer>()
    private val questionsClicksSubject = PublishSubject.create<Question>()

    fun updateAnswers(answersList: List<Answer>, answersMap: Map<String, Long>) {
        answers.clear()
        answers.addAll(answersList)
        kinMap.clear()
        kinMap.putAll(answersMap)
        notifyItemRangeChanged(1, answersList.size)
    }

    fun updateQuestion(question: Question, kin: Long?) {
        this.question = question
        questionKin = kin
        notifyItemChanged(0)
    }

    override fun getItemCount(): Int {
        return answers.size + 1
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        if (vh.itemViewType == ID_ANSWER) {
            val answer = answers[position - 1]
            Log.d("debug", "index = $position answerId = ${answer.answerId}")
            val kin = kinMap[answer.answerId.toString()]

            (vh as AnswerViewHolder).setAnswer(answer, kin)
        } else {
            (vh as QuestionViewHolder).setQuestion(question, questionKin)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ID_ANSWER) {
            val view = AnswerViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.answer_view_holder, parent, false)
            )
            view.listener = object : ViewHolderClickListener {
                override fun onClick(adapterPosition: Int) {
                    answersClicksSubject.onNext(answers[adapterPosition - 1])
                }
            }
            return view
        } else {
            val view = QuestionViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.question_view_holder, parent, false)
            )
            view.listener = object : ViewHolderClickListener {
                override fun onClick(adapterPosition: Int) {
                    questionsClicksSubject.onNext(question)
                }
            }
            return view
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ID_QUESTION else ID_ANSWER
    }

    fun answerGiveKinClickEvents(): Observable<Answer> {
        return answersClicksSubject.hide()
    }

    fun questionGiveKinClickEvents(): Observable<Question> {
        return questionsClicksSubject.hide()
    }
}