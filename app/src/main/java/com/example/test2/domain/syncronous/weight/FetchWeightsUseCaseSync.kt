package com.example.test2.domain.syncronous.weight

import com.example.test2.data.dao.implementations.WeightDAO
import com.example.test2.data.entities.implementations.Weight

/**
 * Map dao functions to Use case functions
 */
class FetchWeightsUseCaseSync {

    /**
     * Synchronous calls
     */
    fun fetchWeightsAndFirstDay(): Pair<List<Weight>, Float?> {
        return WeightDAO.getWeightsAndFirstDay(0L, 20L)
    }

    fun insert(weight: Weight) : Long {
        return WeightDAO.insert(weight)
    }

}