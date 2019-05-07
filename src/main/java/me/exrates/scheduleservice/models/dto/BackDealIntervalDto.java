package me.exrates.scheduleservice.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.exceptions.UnsupportedIntervalFormatException;
import me.exrates.scheduleservice.exceptions.UnsupportedIntervalTypeException;
import me.exrates.scheduleservice.models.enums.IntervalType;

@Data
@NoArgsConstructor
public class BackDealIntervalDto {

    private Integer intervalValue;
    private IntervalType intervalType;

    public BackDealIntervalDto(Integer intervalValue, IntervalType intervalType) {
        this.intervalValue = intervalValue;
        this.intervalType = intervalType;
    }

    public String getInterval() {
        return intervalValue + " " + intervalType;
    }

    public BackDealIntervalDto(String intervalString) {
        try {
            this.intervalValue = Integer.valueOf(intervalString.split(" ")[0]);
            this.intervalType = IntervalType.convert(intervalString.split(" ")[1]);
        } catch (UnsupportedIntervalTypeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnsupportedIntervalFormatException(intervalString);
        }
    }
}