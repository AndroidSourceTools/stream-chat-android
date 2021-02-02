package io.getstream.chat.android.client.api2

import io.getstream.chat.android.client.api.ChatApi
import io.getstream.chat.android.client.api2.mapping.toDomain
import io.getstream.chat.android.client.api2.mapping.toDto
import io.getstream.chat.android.client.api2.model.dto.DeviceDto
import io.getstream.chat.android.client.api2.model.dto.ReactionDto
import io.getstream.chat.android.client.api2.model.requests.AddDeviceRequest
import io.getstream.chat.android.client.api2.model.requests.MessageRequest
import io.getstream.chat.android.client.api2.model.requests.ReactionRequest
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.call.map
import io.getstream.chat.android.client.models.Device
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction

internal class MoshiChatApi(
    private val apiKey: String,
    private val legacyApiDelegate: ChatApi,
    private val messageApi: MessageApi,
    private val deviceApi: DeviceApi,
) : ChatApi by legacyApiDelegate {

    private lateinit var userId: String
    private lateinit var connectionId: String

    override fun setConnection(userId: String, connectionId: String) {
        this.userId = userId
        this.connectionId = connectionId
    }

    override fun sendMessage(channelType: String, channelId: String, message: Message): Call<Message> {
        return messageApi.sendMessage(
            channelType = channelType,
            channelId = channelId,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId,
            message = MessageRequest(message.toDto()),
        ).map { response -> response.message.toDomain() }
    }

    override fun updateMessage(message: Message): Call<Message> {
        return messageApi.updateMessage(
            messageId = message.id,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId,
            message = MessageRequest(message.toDto()),
        ).map { response -> response.message.toDomain() }
    }

    override fun getMessage(messageId: String): Call<Message> {
        return messageApi.getMessage(
            messageId = messageId,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId
        ).map { response -> response.message.toDomain() }
    }

    override fun deleteMessage(messageId: String): Call<Message> {
        return messageApi.deleteMessage(
            messageId = messageId,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId,
        ).map { response -> response.message.toDomain() }
    }

    override fun getReactions(
        messageId: String,
        offset: Int,
        limit: Int,
    ): Call<List<Reaction>> {
        return messageApi.getReactions(
            messageId = messageId,
            apiKey = apiKey,
            connectionId = connectionId,
            offset = offset,
            limit = limit
        ).map { response -> response.reactions.map(ReactionDto::toDomain) }
    }

    override fun sendReaction(reaction: Reaction, enforceUnique: Boolean): Call<Reaction> {
        return messageApi.sendReaction(
            messageId = reaction.messageId,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId,
            request = ReactionRequest(
                reaction = reaction.toDto(),
                enforce_unique = enforceUnique
            )
        ).map { response -> response.reaction.toDomain() }
    }

    override fun deleteReaction(
        messageId: String,
        reactionType: String,
    ): Call<Message> {
        return messageApi.deleteReaction(
            messageId = messageId,
            reactionType = reactionType,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId
        ).map { response -> response.message.toDomain() }
    }

    override fun addDevice(firebaseToken: String): Call<Unit> {
        return deviceApi.addDevices(
            apiKey,
            userId,
            connectionId,
            AddDeviceRequest(firebaseToken)
        ).toUnitCall()
    }

    override fun deleteDevice(firebaseToken: String): Call<Unit> {
        return deviceApi.deleteDevice(
            deviceId = firebaseToken,
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId
        ).toUnitCall()
    }

    override fun getDevices(): Call<List<Device>> {
        return deviceApi.getDevices(
            apiKey = apiKey,
            userId = userId,
            connectionId = connectionId
        ).map { response -> response.devices.map(DeviceDto::toDomain) }
    }

    private fun Call<*>.toUnitCall() = map {}
}
