package me.exrates.scheduleservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BigDecimalConverter {

    private static final double BOUND_1 = 0.0001;
    private static final double BOUND_2 = 0.001;
    private static final double BOUND_3 = 0.01;
    private static final double BOUND_4 = 0.1;
    private static final double BOUND_5 = 1;
    private static final double BOUND_6 = 10;
    private static final double BOUND_7 = 100;
    private static final double BOUND_8 = 10000;
    private static final double BOUND_9 = 100000;

    private int precision1;
    private int precision2;
    private int precision3;
    private int precision4;
    private int precision5;
    private int precision6;
    private int precision7;
    private int precision8;
    private int precision9;
    private int precision10;

    public BigDecimalConverter(@Value("${precision.value1}") int precision1,
                               @Value("${precision.value2}") int precision2,
                               @Value("${precision.value3}") int precision3,
                               @Value("${precision.value4}") int precision4,
                               @Value("${precision.value5}") int precision5,
                               @Value("${precision.value6}") int precision6,
                               @Value("${precision.value7}") int precision7,
                               @Value("${precision.value8}") int precision8,
                               @Value("${precision.value9}") int precision9,
                               @Value("${precision.value10}") int precision10) {
        this.precision1 = precision1;
        this.precision2 = precision2;
        this.precision3 = precision3;
        this.precision4 = precision4;
        this.precision5 = precision5;
        this.precision6 = precision6;
        this.precision7 = precision7;
        this.precision8 = precision8;
        this.precision9 = precision9;
        this.precision10 = precision10;
    }

    public BigDecimal convert(BigDecimal initialValue) {
        if (BigDecimal.valueOf(BOUND_1).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision1, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_1).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_2).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision2, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_2).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_3).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision3, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_3).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_4).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision4, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_4).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_5).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision5, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_5).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_6).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision6, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_6).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_7).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision7, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_7).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_8).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision8, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_8).compareTo(initialValue) < 0 && BigDecimal.valueOf(BOUND_9).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(precision9, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(BOUND_9).compareTo(initialValue) < 0) {
            initialValue = initialValue.setScale(precision10, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(initialValue.doubleValue());
    }
}