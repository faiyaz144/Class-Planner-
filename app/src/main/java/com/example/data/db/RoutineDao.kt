package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
  @Query("SELECT * FROM class_routines ORDER BY dayOfWeek, startTime ASC")
  fun getAllRoutines(): Flow<List<RoutineEntity>>

  @Query("SELECT * FROM class_routines WHERE dayOfWeek = :day ORDER BY startTime ASC")
  fun getRoutinesForDay(day: String): Flow<List<RoutineEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoutine(routine: RoutineEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(routines: List<RoutineEntity>)

  @Update
  suspend fun updateRoutine(routine: RoutineEntity)

  @Delete
  suspend fun deleteRoutine(routine: RoutineEntity)

  @Query("DELETE FROM class_routines WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM class_routines")
  suspend fun deleteAll()
}
