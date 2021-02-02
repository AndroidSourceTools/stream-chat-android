package io.getstream.chat.android.client.api2

import io.getstream.chat.android.client.api.AuthenticatedApi
import io.getstream.chat.android.client.api2.model.requests.MessageRequest
import io.getstream.chat.android.client.api2.model.requests.ReactionRequest
import io.getstream.chat.android.client.api2.model.response.MessageResponse
import io.getstream.chat.android.client.api2.model.response.ReactionResponse
import io.getstream.chat.android.client.api2.model.response.ReactionsResponse
import io.getstream.chat.android.client.call.RetrofitCall
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@MoshiApi
@AuthenticatedApi
internal interface MessageApi {

    @POST("/channels/{type}/{id}/message")
    fun sendMessage(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
        @Body message: MessageRequest,
    ): RetrofitCall<MessageResponse>

    @GET("/messages/{id}")
    fun getMessage(
        @Path("id") messageId: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
    ): RetrofitCall<MessageResponse>

    @POST("/messages/{id}")
    fun updateMessage(
        @Path("id") messageId: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
        @Body message: MessageRequest,
    ): RetrofitCall<MessageResponse>

    @DELETE("/messages/{id}")
    fun deleteMessage(
        @Path("id") messageId: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
    ): RetrofitCall<MessageResponse>

    @POST("/messages/{id}/reaction")
    fun sendReaction(
        @Path("id") messageId: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
        @Body request: ReactionRequest,
    ): RetrofitCall<ReactionResponse>

    @DELETE("/messages/{id}/reaction/{type}")
    fun deleteReaction(
        @Path("id") messageId: String,
        @Path("type") reactionType: String,
        @Query("api_key") apiKey: String,
        @Query("user_id") userId: String,
        @Query("client_id") connectionId: String,
    ): RetrofitCall<MessageResponse>

    @GET("/messages/{id}/reactions")
    fun getReactions(
        @Path("id") messageId: String,
        @Query("api_key") apiKey: String,
        @Query("client_id") connectionId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): RetrofitCall<ReactionsResponse>
}
