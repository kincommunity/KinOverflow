package kin.kinoverflow.network

import io.reactivex.Single
import kin.kinoverflow.model.Answer
import kin.kinoverflow.model.Question
import kin.kinoverflow.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


internal interface StackOverflowApiService {

    @GET("search")
    fun getQuestions(@Query("order") order: String = "desc", @Query("pagesize") pagesize: Int = 20,
                     @Query("sort") sort: String = "activity", @Query("tagged") tags: String,
                     @Query("site") site: String = "stackoverflow", @Query("filter") filter:
                     String = "withbody"): Single<StackOverflowQuery<Question>>

    @GET("questions/{id}/answers")
    fun getAnswers(@Path("id") questionId: Int, @Query("order") order: String = "desc",
                   @Query("pagesize") pagesize: Int = 20, @Query("sort") sort: String = "activity",
                   @Query("site") site: String = "stackoverflow",
                   @Query("filter") filter: String = "withbody"): Single<StackOverflowQuery<Answer>>

    @GET("users/{id}")
    fun getUser(@Path("id") id: Int, @Query("site") site: String = "stackoverflow", @Query("filter") filter: String = "unsafe"): Single<StackOverflowQuery<User>>
}