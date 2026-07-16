package com.example.test2.data.dao.behaviors

import com.example.test2.data.entities.behaviors.Timelineable


interface TimelineableDAO < TIMELINEABLE: Timelineable> {
    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    fun delete(timelineable: TIMELINEABLE) : Boolean
}