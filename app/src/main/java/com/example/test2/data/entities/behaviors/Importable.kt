package com.example.test2.data.entities.behaviors

interface Importable <IMPORTABLE, COMPARABLE: Comparable<COMPARABLE>>{
    fun prepareForImport() : IMPORTABLE
    fun importSortKey(): COMPARABLE
    fun getIdForImport(): Long
    fun setIdForImport(newId: Long)
}

fun <IMPORTABLE, COMPARABLE: Comparable<COMPARABLE>>
        List<IMPORTABLE>.prepareForImport(): List<IMPORTABLE>
        where IMPORTABLE : Importable<IMPORTABLE, COMPARABLE> {
    return this.sortedBy { it.importSortKey() }.map { it.prepareForImport() }
}

fun <IMPORTABLE, COMPARABLE : Comparable<COMPARABLE>>
        List<IMPORTABLE>.importAndGetComparableIDsMap(
    insert: (entity: IMPORTABLE) -> Long
): Map<Long, IMPORTABLE>
        where IMPORTABLE : Importable<IMPORTABLE, COMPARABLE> {

    val importedEntitiesByOldId: MutableMap<Long, IMPORTABLE> =
        mutableMapOf<Long, IMPORTABLE>()

    this.sortedBy { entity: IMPORTABLE ->
        entity.importSortKey()
    }.forEach { entity: IMPORTABLE ->

        val oldId: Long = entity.getIdForImport()
        val importedEntity: IMPORTABLE = entity.prepareForImport()
        val newId: Long = insert(importedEntity)
        importedEntity.setIdForImport(newId)
        importedEntitiesByOldId[oldId] = importedEntity
    }

    return importedEntitiesByOldId
}
