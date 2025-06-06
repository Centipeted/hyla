package hu.bme.aut.adapter

import hu.bme.aut.domain.model.User
import hu.bme.aut.domain.service.UserRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserRepository : UserRepository {
    private val users = ConcurrentHashMap<String, User>()
    private val keyIndex = ConcurrentHashMap<String, String>()

    override fun save(user: User): User {
        users[user.phoneNumber] = user
        user.loginKey?.let { keyIndex[it] = user.phoneNumber }
        return user
    }

    override fun findByPhoneNumber(phoneNumber: String): User? {
        return users[phoneNumber]
    }

    override fun findByLoginKey(loginKey: String): User? {
        return keyIndex[loginKey]?.let { users[it] }
    }
}