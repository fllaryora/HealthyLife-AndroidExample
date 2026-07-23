package com.example.test2.features.numbertwo.data.local

import com.example.test2.data.entities.behaviors.Importable
import com.example.test2.data.entities.behaviors.TodoLineable
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Entity
@Serializable
data class NumberTwoEntity (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    // provide default values for all parameters to ensure a default constructor exists
    @Serializable(with = TimeConverterForKotlinxSerializable::class)
    @Unique
    val date: OffsetDateTime = OffsetDateTime.now(),
    val isTaken: Boolean = true //always true otherwise the row would not exist
) : TodoLineable, Importable<NumberTwoEntity, OffsetDateTime> {
    override fun getTime() = date
    override fun getRatingT() = if(isTaken) 10 else 0
    override fun getShowRating() = false
    override fun isTakenT() = isTaken
    override fun prepareForImport(): NumberTwoEntity {
        return copy( id= 0L)
    }

    override fun importSortKey(): OffsetDateTime {
        return date
    }
}