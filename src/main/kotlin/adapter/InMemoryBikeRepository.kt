package hu.bme.aut.adapter

import hu.bme.aut.domain.model.Bike
import hu.bme.aut.domain.service.BikeRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryBikeRepository : BikeRepository {
    private val store = ConcurrentHashMap<String, Bike>()

    override fun save(bike: Bike): Bike {
        store[bike.bikeNumber] = bike
        return bike
    }

    override fun findByNumber(bikeNumber: String): Bike? = store[bikeNumber]

    override fun all(): List<Bike> = store.values.toList()
}