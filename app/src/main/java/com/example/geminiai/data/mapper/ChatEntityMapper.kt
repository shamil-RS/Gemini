package com.example.geminiai.data.mapper

import com.example.geminiai.data.room.entity.ChatEntity
import com.example.geminiai.model.Chat

object ChatEntityMapper : EntityMapper<ChatEntity, Chat> {
    override fun asEntity(domain: Chat): ChatEntity {
        return ChatEntity(
            id = domain.id,
            title = domain.title,
            createdAt = domain.createdAt
        )
    }

    override fun asDomain(entity: ChatEntity): Chat {
        return Chat(
            id = entity.id,
            title = entity.title,
            createdAt = entity.createdAt
        )
    }
}

fun Chat.asEntity(): ChatEntity {
    return ChatEntityMapper.asEntity(this)
}

fun ChatEntity?.asDomain(): Chat {
    return ChatEntityMapper.asDomain((this ?: orEmpty()) as ChatEntity)
}