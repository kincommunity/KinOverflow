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
import kin.kinoverflow.model.Answer
import kin.kinoverflow.questions.ViewHolderClickListener
import kin.kinoverflow.utils.removeXmlFormatting

class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.tv_answer) lateinit var answerBody: TextView
    @BindView(R.id.accepted_answer) lateinit var acceptedAnswer: ImageView
    @BindView(R.id.tv_votes) lateinit var votesCounter: TextView
    @BindView(R.id.tv_kin) lateinit var kinCounter: TextView

    @BindView(R.id.tv_asked_date) lateinit var date: RelativeTimeTextView
    @BindView(R.id.profile_image) lateinit var profileImage: ImageView
    @BindView(R.id.tv_user_name) lateinit var profileName: TextView
    @BindView(R.id.tv_badges_count) lateinit var profileBadgesCount: TextView
    @BindView(R.id.kin_icon) lateinit var kinIcon: View
    @BindView(R.id.rectangle_kin) lateinit var kinRect: View

    var listener: ViewHolderClickListener? = null

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setAnswer(answer: Answer, kin: Long?) {
        date.setReferenceTime(answer.creationDate * DateUtils.SECOND_IN_MILLIS)
        answerBody.text = removeXmlFormatting(answer.body)
        votesCounter.text = (answer.upVoteCount - answer.downVoteCount).toString()
        profileName.text = answer.owner.displayName
        if (!answer.owner.link.isEmpty()) {
            Picasso.with(itemView.context)
                    .load(answer.owner.profileImage)
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
    fun onSponsorClick(){
        listener?.onClick(adapterPosition)
    }
}