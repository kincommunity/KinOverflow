package kin.kinoverflow.post

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.squareup.picasso.Picasso
import kin.kinoverflow.R
import kin.kinoverflow.model.Question
import kin.kinoverflow.questions.ViewHolderClickListener


class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.tv_question_body) lateinit var questionBody: TextView
    @BindView(R.id.tv_title) lateinit var title: TextView
    @BindView(R.id.tv_votes) lateinit var votesCounter: TextView
    @BindView(R.id.tv_tags) lateinit var tags: TextView
    @BindView(R.id.tv_asked_date) lateinit var date: RelativeTimeTextView
    @BindView(R.id.profile_image) lateinit var profileImage: ImageView
    @BindView(R.id.tv_user_name) lateinit var profileName: TextView
    @BindView(R.id.tv_badges_count) lateinit var profileBadgesCount: TextView
    @BindView(R.id.tv_kin) lateinit var kinCounter: TextView
    @BindView(R.id.kin_icon) lateinit var kinIcon: View
    @BindView(R.id.rectangle_kin) lateinit var kinRect: View

    var listener: ViewHolderClickListener? = null

    init {
        ButterKnife.bind(this, itemView)
        itemId
    }

    fun setQuestion(question: Question, kin: Long?) {
        title.text = question.title
        questionBody.text = question.body
        votesCounter.text = (question.upVoteCount - question.downVoteCount).toString()
        tags.text = question.tags.reduce(operation = { tags, tag ->
            return@reduce "$tags, $tag"
        })
        date.setReferenceTime(question.creationDate * DateUtils.SECOND_IN_MILLIS)
        profileName.text = question.owner.displayName
        if (!question.owner.link.isEmpty()) {
            Picasso.with(itemView.context)
                    .load(question.owner.profileImage)
                    .into(profileImage)
        }

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


    @OnClick(R.id.sponsor)
    fun onSponsorClick() {
        listener?.onClick(adapterPosition)
    }

}