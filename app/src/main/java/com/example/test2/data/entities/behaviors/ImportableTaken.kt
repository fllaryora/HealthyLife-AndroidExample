package com.example.test2.data.entities.behaviors

interface ImportableTaken<
        IMPORTABLE_TAKEN,
        OWNER,
        COMPARABLE : Comparable<COMPARABLE>
        > where OWNER : Importable<OWNER, COMPARABLE> {

    fun prepareForImport(owner: OWNER): IMPORTABLE_TAKEN
}

/*
fun <IMPORTABLE, COMPARABLE: Comparable<COMPARABLE>>
        List<IMPORTABLE>.prepareForImport(): List<IMPORTABLE>
        where IMPORTABLE : Importable<IMPORTABLE, COMPARABLE> {
    return this.map { it.prepareForImport() }
}*/