package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @Override
    public String toString() {
        return "id=\"" + id + '\"' +
                ", name=\"" + name + '\"';
    }
}
