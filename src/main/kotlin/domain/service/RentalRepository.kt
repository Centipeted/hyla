package hu.bme.aut.domain.service

import hu.bme.aut.domain.model.Rental

interface RentalRepository {
    fun save(rental: Rental): Rental

    fun findById(id: Long): Rental?

    fun findByBike(bike: String): List<Rental>

    fun all(): List<Rental>
}