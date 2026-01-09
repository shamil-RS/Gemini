package com.example.geminiai.data.mapper

import com.example.geminiai.data.room.entity.MessageEntity
import com.example.geminiai.model.Message

object MessageEntityMapper : EntityMapper<MessageEntity, Message> {
    override fun asEntity(domain: Message): MessageEntity {
        return MessageEntity(
            id = domain.id,
            chatId = domain.chatId,
            senderId = domain.senderId,
            text = domain.text,
            image = domain.image,
            timestamp = domain.timestamp,
        )
    }

    override fun asDomain(entity: MessageEntity): Message {
        return Message(
            id = entity.id,
            chatId = entity.chatId,
            senderId = entity.senderId,
            text = entity.text,
            image = entity.image,
            timestamp = entity.timestamp,
        )
    }
}

fun Message.asEntity(): MessageEntity {
    return MessageEntityMapper.asEntity(this)
}

fun MessageEntity?.asDomain(): Message {
    return MessageEntityMapper.asDomain((this ?: orEmpty()) as MessageEntity)
}