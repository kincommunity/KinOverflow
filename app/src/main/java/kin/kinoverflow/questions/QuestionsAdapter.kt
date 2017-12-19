package kin.kinoverflow.questions

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kin.kinoverflow.R
import kin.kinoverflow.model.Question


class QuestionsAdapter : RecyclerView.Adapter<QuestionListItemViewHolder>() {

    private val questions: ArrayList<Question> = ArrayList()
    private val kinMap: HashMap<String, Long> = HashMap()
    private val subject: PublishSubject<Question> = PublishSubject.create()

    fun updateQuestions(pair: Pair<List<Question>, Map<String, Long>>) {
        questions.clear()
        questions.addAll(pair.first)
        kinMap.clear()
        kinMap.putAll(pair.second)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionListItemViewHolder {
        val view = QuestionListItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.question_list_item_view_holder, parent, false)
        )
        view.listener = object : ViewHolderClickListener {
            override fun onClick(adapterPosition: Int) {
                subject.onNext(questions[adapterPosition])
            }
        }
        return view
    }

    override fun onBindViewHolder(vh: QuestionListItemViewHolder, position: Int) {
        val question = questions[position]
        Log.d("yossi", "index = $position questionId = ${question.questionId}")
        val kin = kinMap[question.questionId.toString()]

        vh.setQuestion(question, kin)
    }

    fun clickEvents(): Observable<Question> {
        return subject.hide()
    }
}

