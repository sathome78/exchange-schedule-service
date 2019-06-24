package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyDto {

    private int id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private boolean hidden;

    public CurrencyDto(int id) {
        this.id = id;
    }
}