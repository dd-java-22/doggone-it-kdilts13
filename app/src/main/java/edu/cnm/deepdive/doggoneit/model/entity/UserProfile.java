package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a user profile in the Doggone It application.
 * Renamed from "User" to "UserProfile" to avoid conflicts with the reserved SQL keyword "user".
 * The table name is "user_profile" for clarity and to prevent potential SQL issues.
 */
@Entity(
    tableName = "user_profile",
    indices = {
        @Index(value = "email", unique = true)
    }
)
public class UserProfile {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_profile_id")
  private long id;

  // TODO: 3/16/2026 consider removing column info unless I need to set something other than a matching name
  // TODO: 3/16/2026 check for missing indexes
  // TODO: 3/16/2026 set missing default values on nonnull fields
  // TODO: 3/16/2026 collation nocase annotation - for case insensitive sort or filter
  @ColumnInfo(name = "name")
  @NonNull
  private String name = "";

  @ColumnInfo(name = "email")
  @NonNull
  private String email = "";

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
