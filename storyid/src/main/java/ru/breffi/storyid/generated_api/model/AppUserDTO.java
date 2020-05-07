/*
 * Id service
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.9.0
 * Contact: support@breffi.ru
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package ru.breffi.storyid.generated_api.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import org.joda.time.DateTime;

/**
 * AppUserDTO
 */

public class AppUserDTO {
  public static final String SERIALIZED_NAME_USER_ID = "userId";
  @SerializedName(SERIALIZED_NAME_USER_ID)
  private String userId;

  public static final String SERIALIZED_NAME_BLOCK_DESCRIPTION = "blockDescription";
  @SerializedName(SERIALIZED_NAME_BLOCK_DESCRIPTION)
  private String blockDescription;

  public static final String SERIALIZED_NAME_BLOCKED = "blocked";
  @SerializedName(SERIALIZED_NAME_BLOCKED)
  private Boolean blocked;

  public static final String SERIALIZED_NAME_BLOCKED_UNTIL = "blockedUntil";
  @SerializedName(SERIALIZED_NAME_BLOCKED_UNTIL)
  private DateTime blockedUntil;

  public static final String SERIALIZED_NAME_BLOCKED_AT = "blockedAt";
  @SerializedName(SERIALIZED_NAME_BLOCKED_AT)
  private DateTime blockedAt;

  public static final String SERIALIZED_NAME_BLOCKED_BY = "blockedBy";
  @SerializedName(SERIALIZED_NAME_BLOCKED_BY)
  private String blockedBy;


  public AppUserDTO userId(String userId) {
    
    this.userId = userId;
    return this;
  }

   /**
   * Get userId
   * @return userId
  **/
  @ApiModelProperty(required = true, value = "")

  public String getUserId() {
    return userId;
  }


  public void setUserId(String userId) {
    this.userId = userId;
  }


  public AppUserDTO blockDescription(String blockDescription) {
    
    this.blockDescription = blockDescription;
    return this;
  }

   /**
   * Get blockDescription
   * @return blockDescription
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getBlockDescription() {
    return blockDescription;
  }


  public void setBlockDescription(String blockDescription) {
    this.blockDescription = blockDescription;
  }


  public AppUserDTO blocked(Boolean blocked) {
    
    this.blocked = blocked;
    return this;
  }

   /**
   * Get blocked
   * @return blocked
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public Boolean getBlocked() {
    return blocked;
  }


  public void setBlocked(Boolean blocked) {
    this.blocked = blocked;
  }


  public AppUserDTO blockedUntil(DateTime blockedUntil) {
    
    this.blockedUntil = blockedUntil;
    return this;
  }

   /**
   * Get blockedUntil
   * @return blockedUntil
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public DateTime getBlockedUntil() {
    return blockedUntil;
  }


  public void setBlockedUntil(DateTime blockedUntil) {
    this.blockedUntil = blockedUntil;
  }


  public AppUserDTO blockedAt(DateTime blockedAt) {
    
    this.blockedAt = blockedAt;
    return this;
  }

   /**
   * Get blockedAt
   * @return blockedAt
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public DateTime getBlockedAt() {
    return blockedAt;
  }


  public void setBlockedAt(DateTime blockedAt) {
    this.blockedAt = blockedAt;
  }


  public AppUserDTO blockedBy(String blockedBy) {
    
    this.blockedBy = blockedBy;
    return this;
  }

   /**
   * Get blockedBy
   * @return blockedBy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getBlockedBy() {
    return blockedBy;
  }


  public void setBlockedBy(String blockedBy) {
    this.blockedBy = blockedBy;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppUserDTO appUserDTO = (AppUserDTO) o;
    return Objects.equals(this.userId, appUserDTO.userId) &&
        Objects.equals(this.blockDescription, appUserDTO.blockDescription) &&
        Objects.equals(this.blocked, appUserDTO.blocked) &&
        Objects.equals(this.blockedUntil, appUserDTO.blockedUntil) &&
        Objects.equals(this.blockedAt, appUserDTO.blockedAt) &&
        Objects.equals(this.blockedBy, appUserDTO.blockedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, blockDescription, blocked, blockedUntil, blockedAt, blockedBy);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppUserDTO {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    blockDescription: ").append(toIndentedString(blockDescription)).append("\n");
    sb.append("    blocked: ").append(toIndentedString(blocked)).append("\n");
    sb.append("    blockedUntil: ").append(toIndentedString(blockedUntil)).append("\n");
    sb.append("    blockedAt: ").append(toIndentedString(blockedAt)).append("\n");
    sb.append("    blockedBy: ").append(toIndentedString(blockedBy)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

