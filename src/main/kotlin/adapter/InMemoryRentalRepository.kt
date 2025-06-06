package hu.bme.aut.adapter

import hu.bme.aut.domain.model.Rental
import hu.bme.aut.domain.service.RentalRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class InMemoryRentalRepository : RentalRepository {
    private val store = ConcurrentHashMap<Long, Rental>()
    private val idGen = AtomicLong(1)

    override fun save(rental: Rental): Rental {
        val id = if (rental.id == 0L) idGen.getAndIncrement() else rental.id
        val toSave = rental.copy(id = id)
        store[id] = toSave
        return toSave
    }

    override fun findById(id: Long): Rental? = store[id]

    override fun findByBike(bike: String): List<Rental> =
        store.values.filter { it.bike == bike }

    override fun all(): List<Rental> = store.values.toList()
}