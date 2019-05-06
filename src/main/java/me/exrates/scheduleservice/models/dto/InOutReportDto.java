package me.exrates.scheduleservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.utils.BigDecimalProcessingUtil;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InOutReportDto {

    private Integer orderNum;

    private int currencyId;
    private String currencyName;

    private BigDecimal rateToUSD;
    private int inputCount;
    private BigDecimal input;
    private BigDecimal inputCommission;
    private int outputCount;
    private BigDecimal output;
    private BigDecimal outputCommission;


    public static String getTitle() {
        return Stream.of("No.", "cur_id", "currency", "rateToUSD", "input", "output", "input_commission", "output_commission")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(currencyId), currencyName, BigDecimalProcessingUtil.formatNoneComma(rateToUSD, false),
                BigDecimalProcessingUtil.formatNoneComma(input, false),
                BigDecimalProcessingUtil.formatNoneComma(output, false),
                BigDecimalProcessingUtil.formatNoneComma(inputCommission, false),
                BigDecimalProcessingUtil.formatNoneComma(outputCommission, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}