package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(tableName = "scan")
public class scan {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  private long id;

  @ColumnInfo(name = "user_id")
  // TODO: 3/13/2026 create index
  // TODO: 3/13/2026 mark foreign key?
  private Long userId;

  @ColumnInfo(name = "image_path")
  @NonNull
  private String imagePath;

  @ColumnInfo(name = "timestamp")
  @NonNull
  private Instant timestamp;

  @ColumnInfo(name = "note")
  @NonNull
  private String note;

  @ColumnInfo(name = "favorite")
  private boolean favorite;
}
