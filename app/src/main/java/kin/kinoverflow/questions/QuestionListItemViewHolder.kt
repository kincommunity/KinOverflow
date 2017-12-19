package kin.kinoverflow.questions

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.BindViews
import butterknife.ButterKnife
import com.github.curioustechizen.ago.RelativeTimeTextView
import io.reactivex.subjects.PublishSubject
import kin.kinoverflow.R
import kin.kinoverflow.model.Question

class QuestionListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.tv_date) lateinit var date: RelativeTimeTextView
    @BindView(R.id.tv_tags) lateinit var tags: TextView
    @BindView(R.id.tv_title) lateinit var title: TextView
    @BindView(R.id.tv_votes_counter) lateinit var votesCounter: TextView
    @BindView(R.id.tv_kin) lateinit var kinCounter: TextView
    @BindView(R.id.kin_icon) lateinit var kinIcon: View
    @BindView(R.id.rectangle_kin) lateinit var kinRect: View

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
            listener?.onClick(adapterPosition)
        }
    }

    var listener: ViewHolderClickListener? = null

    fun setQuestion(question: Question, kin: Long?) {
        date.setReferenceTime(question.creationDate * DateUtils.SECOND_IN_MILLIS)
        tags.text = question.tags.reduce(operation = { tags, tag ->
            return@reduce "$tags,$tag"
        })
        title.text = question.title
        votesCounter.text = (question.upVoteCount - question.downVoteCount).toString()
        if (kin != null) {
            kinCounter.text = kin.toString()
            kinCounter.visibility = View.VISIBLE
            kinIcon.visibility = View.VISIBLE
            kinRect.visibility = View.VISIBLE
        } else {
            kinCounter.visibility = View.GONE
            kinIcon.visibility = View.GONE
            kinRect.visibility = View.GONE
        }
    }

}

interface ViewHolderClickListener {
    fun onClick(adapterPosition: Int)
}