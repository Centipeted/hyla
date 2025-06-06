package hu.bme.aut.domain.service

import hu.bme.aut.domain.model.Bike

interface BikeRepository {
    fun save(bike: Bike): Bike
    fun findByNumber(bikeNumber: String): Bike?
    fun all(): List<Bike>
}