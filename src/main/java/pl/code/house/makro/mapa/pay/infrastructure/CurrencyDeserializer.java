package pl.code.house.makro.mapa.pay.infrastructure;

import static java.util.Locale.UK;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Currency;

class CurrencyDeserializer extends JsonDeserializer<Currency> {

  @Override
  public Currency deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    String value = ((JsonNode) jsonParser.getCodec().readTree(jsonParser)).asText().toUpperCase(UK);
    return Currency.getInstance(value);
  }
}
