package com.example.test2.data.entities.behaviors

interface Importable <IMPORTABLE, COMPARABLE: Comparable<COMPARABLE>>{
    fun prepareForImport() : IMPORTABLE
    fun importSortKey(): COMPARABLE
}

fun <IMPORTABLE, COMPARABLE: Comparable<COMPARABLE>>
        List<IMPORTABLE>.prepareForImport(): List<IMPORTABLE>
        where IMPORTABLE : Importable<IMPORTABLE, COMPARABLE> {
    return this.sortedBy { it.importSortKey() }.map { it.prepareForImport() }
}

