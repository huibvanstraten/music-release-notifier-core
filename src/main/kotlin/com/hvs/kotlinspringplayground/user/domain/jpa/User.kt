package com.hvs.kotlinspringplayground.user.domain.jpa

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "users")
data class User(

    @Id
    val id: UUID,

    val username: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val artists: JsonNode?,
) {

    companion object {
        fun from(userData: UserDataDto): User = with(userData) {
            User(
                id = this.id,
                username = this.name,
                artists = jacksonObjectMapper().convertValue(this.artistIdList),
            )
        }
    }
}
