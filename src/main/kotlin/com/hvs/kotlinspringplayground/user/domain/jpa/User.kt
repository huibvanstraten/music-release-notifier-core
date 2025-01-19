package com.hvs.kotlinspringplayground.user.domain.jpa

import com.fasterxml.jackson.annotation.JsonFilter
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
@JsonFilter("userFilter")
data class User(

    @Id
    val id: UUID = UUID.randomUUID(),

    val username: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "artist_id_list")
    val artistIdList: JsonNode?,
) {

    companion object {
        fun from(userData: UserDataDto): User = with(userData) {
            User(
                username = this.username,
                artistIdList = jacksonObjectMapper().convertValue(this.artistIdList),
            )
        }
    }
}
