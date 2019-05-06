package me.exrates.scheduleservice.models.logging;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MethodsLog {

    private String methodName;
    private String arguments;
    private Object result;
    private String userEmail;
    private long processingTime;
    private String error;
}
