package com.example.test2.data.dao.behaviors

import com.example.test2.data.entities.behaviors.TodoLineable

interface TodoLineableDAO < TODOLINEABLE: TodoLineable> {
    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    fun delete(todoLineable: TODOLINEABLE) : Boolean
}