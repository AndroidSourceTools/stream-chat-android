package io.getstream.chat.android.livedata.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ChannelUserRead
import io.getstream.chat.android.client.models.User
import java.util.*


/**
 * ChannelStateEntity stores both the channel information as well as references
 * to all of the channel's state
 *
 * note that we don't store channel watchers or watcher_count.
 * as that information is likely to go stale when you go offline.
 *
 * messages are stored on their own table for easier pagination and updates
 *
 */
@Entity(tableName = "stream_chat_channel_state")
data class ChannelStateEntity(var type: String, var channelId: String) {
    @PrimaryKey
    var cid: String = "%s:%s".format(type, channelId)

    /** created by user id */
    var createdByUserId: String? = null

    /** if the channel is frozen or not (new messages wont be allowed) */
    var frozen: Boolean = false

    /** list of the channel members, can be regular members, moderators or admins */
    // TODO: do we prefer a map? a map seems better..
    var members: MutableList<MemberEntity> = mutableListOf()

    /** list of how far each user has read */
    var reads: MutableMap<String, ChannelUserReadEntity> = mutableMapOf()

    /** denormalize the last message date so we can sort on it */
    var lastMessageAt: Date? = null

    /** when the channel was created */
    var createdAt: Date? = null
    /** when the channel was updated */
    var updatedAt: Date? = null
    /** when the channel was deleted */
    var deletedAt: Date? = null
    /** all the custom data provided for this channel */
    var extraData = mutableMapOf<String, Any>()

    /** if the channel has been synced to the servers */
    var syncStatus: Int? = null

    /** create a ChannelStateEntity from a Channel object */
    constructor(c: Channel): this(c.type, c.id) {
        frozen = c.frozen
        createdAt = c.createdAt
        updatedAt = c.updatedAt
        deletedAt = c.deletedAt
        extraData = c.extraData

        members = c.members.map { MemberEntity(it) }.toMutableList()
        reads = mutableMapOf()
        for (r in c.read) {
            reads[r.getUserId()] = ChannelUserReadEntity(r)
        }
        lastMessageAt = c.lastMessageAt
        createdByUserId = c.createdBy.getUserId()
    }

    /** convert a channelEntity into a channel object */
    fun toChannel(userMap: Map<String, User>): Channel {
        val c = Channel()
        c.type = type
        c.id = channelId
        c.cid = cid
        c.frozen = frozen
        c.createdAt = createdAt
        c.updatedAt = updatedAt
        c.deletedAt = deletedAt
        c.extraData = extraData
        c.lastMessageAt = lastMessageAt

        c.members = members.map { it.toMember(userMap) }

        c.read = reads.values.map { it.toChannelUserRead(userMap) }

        c.createdBy = userMap[createdByUserId] ?: error("userMap doesnt contain the user $createdByUserId for the channel.created_by channel $cid")

        return c
    }

    /** updates last message and lastmessagedate on this channel entity */
    fun addMessage(messageEntity: MessageEntity) {
        checkNotNull(messageEntity.createdAt) { "created at cant be null, be sure to set message.createdAt"}

        if (lastMessageAt == null || messageEntity.createdAt!!.after(lastMessageAt)) {
            lastMessageAt = messageEntity.createdAt
        }
    }

    /** updates last message and lastmessagedate on this channel entity */
    fun updateReads(read: ChannelUserRead) {
        val readEntity = ChannelUserReadEntity(read)
        reads.set(read.getUserId(), readEntity)
    }


}