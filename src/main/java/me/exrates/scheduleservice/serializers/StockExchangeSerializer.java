package me.exrates.scheduleservice.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;

import java.io.IOException;

public class StockExchangeSerializer extends JsonSerializer<StockExchangeDto> {

    @Override
    public void serialize(StockExchangeDto value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.getName());
    }
}
