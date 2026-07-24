package com.example.test2.data.entities.behaviors

interface ImportableTaken<
        IMPORTABLE_TAKEN,
        OWNER,
        COMPARABLE : Comparable<COMPARABLE>
        > where OWNER : Importable<OWNER, COMPARABLE> {

    fun prepareForImport(owner: OWNER): IMPORTABLE_TAKEN
    fun getOwnerId(): Long

}

fun < IMPORTABLE_TAKEN, OWNER, COMPARABLE : Comparable<COMPARABLE>>
        List<IMPORTABLE_TAKEN>.groupByOwnerId(): Map<Long, List<IMPORTABLE_TAKEN>>
        where OWNER: Importable<OWNER, COMPARABLE>,
              IMPORTABLE_TAKEN: ImportableTaken< IMPORTABLE_TAKEN, OWNER, COMPARABLE> {
    val takenGroupedByImportable : Map <Long, List<IMPORTABLE_TAKEN>> =
        this.groupBy { importableTakenEntity : IMPORTABLE_TAKEN ->
            importableTakenEntity.getOwnerId()
        }
    return takenGroupedByImportable
}

/**
 * Imports a collection of grouped "taken" entities while rebuilding their
 * relationship with previously imported owner entities.
 *
 * Each map entry contains:
 * - The exported owner identifier as the key.
 * - The list of taken entities associated with that owner.
 *
 * During the import process:
 * 1. The owner is resolved using its exported identifier.
 * 2. Each taken entity is prepared for import by invoking
 *    [ImportableTaken.prepareForImport].
 * 3. The prepared entity is inserted using the supplied [insert] function.
 *
 * The method fails if an exported owner identifier cannot be found in
 * [importedOwnersByOldId].
 *
 * Example:
 *
 * ```kotlin
 * groupedTakenEntities.importTakenEntitiesResolvingOwners(
 *     importedOwnersByOldId = importedActivitiesByOldId,
 *     insert = ActivityTakenDAOImpl::insert
 * )
 * ```
 *
 * @param importedOwnersByOldId Mapping between exported owner IDs and the
 * corresponding owner entities already imported into the destination database.
 * @param insert Function responsible for persisting each prepared entity.
 *
 * @throws IllegalStateException If an owner referenced by an exported ID
 * cannot be resolved.
 */
fun < IMPORTABLE_TAKEN, OWNER, COMPARABLE : Comparable<COMPARABLE>>
        Map<Long, List<IMPORTABLE_TAKEN>>.importTakenEntitiesResolvingOwners(
        importedOwnersByOldId: Map<Long, OWNER>,
        insert: (entity: IMPORTABLE_TAKEN) -> Unit
)
    where OWNER: Importable<OWNER, COMPARABLE>,
          IMPORTABLE_TAKEN: ImportableTaken< IMPORTABLE_TAKEN, OWNER, COMPARABLE> {

    this.forEach { (oldOwnerId: Long, takenList: List<IMPORTABLE_TAKEN>) ->

        val importedOwner: OWNER = importedOwnersByOldId[oldOwnerId]
                ?: error("Owner not found. oldId=$oldOwnerId")

        takenList.forEach { taken: IMPORTABLE_TAKEN ->
            val prepared: IMPORTABLE_TAKEN = taken.prepareForImport(owner = importedOwner)
            insert(prepared)
        }
    }
}


fun < IMPORTABLE_TAKEN, OWNER, COMPARABLE : Comparable<COMPARABLE>>
        List<IMPORTABLE_TAKEN>.groupAndImportResolvingOwners(
    importedOwnersByOldId: Map<Long, OWNER>,
    insert: (entity: IMPORTABLE_TAKEN) -> Unit
): Unit
    where OWNER: Importable<OWNER, COMPARABLE>,
          IMPORTABLE_TAKEN: ImportableTaken< IMPORTABLE_TAKEN, OWNER, COMPARABLE> {

    val takenGroupedByImportable : Map <Long, List<IMPORTABLE_TAKEN>> = this.groupByOwnerId()
    takenGroupedByImportable.importTakenEntitiesResolvingOwners( importedOwnersByOldId, insert)
}

