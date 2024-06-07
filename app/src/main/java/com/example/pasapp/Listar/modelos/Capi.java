package com.example.pasapp.Listar.modelos;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Capi {

@SerializedName("season")
@Expose
private Integer season;
@SerializedName("episode")
@Expose
private Integer episode;
@SerializedName("name")
@Expose
private String name;
@SerializedName("rating")
@Expose
private Double rating;
@SerializedName("description")
@Expose
private String description;
@SerializedName("airDate")
@Expose
private String airDate;
@SerializedName("thumbnailUrl")
@Expose
private String thumbnailUrl;
@SerializedName("id")
@Expose
private Integer id;

public Integer getSeason() {
return season;
}

public void setSeason(Integer season) {
this.season = season;
}

public Integer getEpisode() {
return episode;
}

public void setEpisode(Integer episode) {
this.episode = episode;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Double getRating() {
return rating;
}

public void setRating(Double rating) {
this.rating = rating;
}

public String getDescription() {
return description;
}

public void setDescription(String description) {
this.description = description;
}

public String getAirDate() {
return airDate;
}

public void setAirDate(String airDate) {
this.airDate = airDate;
}

public String getThumbnailUrl() {
return thumbnailUrl;
}

public void setThumbnailUrl(String thumbnailUrl) {
this.thumbnailUrl = thumbnailUrl;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

}