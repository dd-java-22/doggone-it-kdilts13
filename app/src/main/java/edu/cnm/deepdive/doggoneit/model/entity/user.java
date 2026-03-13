package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user") // TODO: 3/13/2026 change name because user is reserved sql word
public class user {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  private long id;

  @ColumnInfo(name = "name")
  @NonNull
  private String name;

  @ColumnInfo(name = "email")
  @NonNull
  private String email;
}
