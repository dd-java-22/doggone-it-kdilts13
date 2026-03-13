package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "breed_prediction")
public class breed_prediction {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  private long id;

  @ColumnInfo(name = "scan_id")
  // TODO: 3/13/2026 create index
  // TODO: 3/13/2026 mark foreign key?
  private long scanId;

  @ColumnInfo(name = "breed_fact_id")
  // TODO: 3/13/2026 create index
  // TODO: 3/13/2026 mark foreign key?
  private Long breedFactId;

  @ColumnInfo(name = "confidence")
  private double confidence; // TODO: 3/13/2026 update ERD / UML to be 'confidence' instead of probability?
}
