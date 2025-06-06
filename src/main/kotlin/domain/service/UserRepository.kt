package hu.bme.aut.domain.service

import hu.bme.aut.domain.model.User

interface UserRepository {
    fun save(user: User): User
    fun findByPhoneNumber(phoneNumber: String): User?
    fun findByLoginKey(loginKey: String): User?
}