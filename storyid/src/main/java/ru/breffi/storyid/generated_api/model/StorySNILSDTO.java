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

/**
 * StorySNILSDTO
 */

public class StorySNILSDTO {
  public static final String SERIALIZED_NAME_SNILS = "snils";
  @SerializedName(SERIALIZED_NAME_SNILS)
  private String snils;


  public StorySNILSDTO snils(String snils) {
    
    this.snils = snils;
    return this;
  }

   /**
   * Get snils
   * @return snils
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getSnils() {
    return snils;
  }


  public void setSnils(String snils) {
    this.snils = snils;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorySNILSDTO storySNILSDTO = (StorySNILSDTO) o;
    return Objects.equals(this.snils, storySNILSDTO.snils);
  }

  @Override
  public int hashCode() {
    return Objects.hash(snils);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorySNILSDTO {\n");
    sb.append("    snils: ").append(toIndentedString(snils)).append("\n");
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

