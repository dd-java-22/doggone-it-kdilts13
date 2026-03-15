package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a user in the Doggone It application.
 * Note: "user" is a reserved SQL keyword in some databases, but the ERD specifies this name,
 * so we keep it. Room should handle escaping appropriately.
 */
@Entity(
    tableName = "user",
    indices = {
        @Index(value = "email", unique = true)
    }
)
public class User {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_id")
  private long id;

  @ColumnInfo(name = "name")
  @NonNull
  private String name;

  @ColumnInfo(name = "email")
  @NonNull
  private String email;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  @NonNull
  public String getEmail() {
    return email;
  }

  public void setEmail(@NonNull String email) {
    this.email = email;
  }
}
