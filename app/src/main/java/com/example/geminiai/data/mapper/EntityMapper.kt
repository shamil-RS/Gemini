package com.example.geminiai.data.mapper

interface EntityMapper<Entity, Domain> {
    fun asEntity(domain: Domain): Entity
    fun asDomain(entity: Entity): Domain
}