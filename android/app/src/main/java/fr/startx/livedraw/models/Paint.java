package fr.startx.livedraw.models;

import android.text.format.DateFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paint {
    public static String TYPE = "paint";

    @JsonProperty("id") private String id;
    @JsonProperty("type") private String type = TYPE;
    @JsonProperty("name") private String name;
    @JsonProperty("created_at") private Date createDate;

    public Paint() {}

    public Paint(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.createDate = new Date();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", createDate).toString();
    }
}
