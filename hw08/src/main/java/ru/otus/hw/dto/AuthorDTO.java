package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("full_name")
    private String fullName;

    @Override
    public String toString() {
        return "id=\"" + id + '\"' +
                ", fullName=\"" + fullName + '\"';
    }
}
