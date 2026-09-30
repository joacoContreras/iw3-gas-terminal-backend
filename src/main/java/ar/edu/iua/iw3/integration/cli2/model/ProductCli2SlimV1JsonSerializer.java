package ar.edu.iua.iw3.integration.cli2.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ProductCli2SlimV1JsonSerializer extends StdSerializer<ProductCli2> {

    public ProductCli2SlimV1JsonSerializer() {
        this(null);
    }

    public ProductCli2SlimV1JsonSerializer(Class<ProductCli2> t) {
        super(t);
    }

    @Override
    public void serialize(ProductCli2 value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        gen.writeStartObject();
        if (value.getId() != null) {
            gen.writeNumberProperty("id", value.getId());
        } else {
            gen.writeNullProperty("id");
        }
        gen.writeStringProperty("product", value.getProduct());
        gen.writeBooleanProperty("stock", value.isStock());
        gen.writeNumberProperty("price", value.getPrice());

        if (value.getCategory() != null) {
            gen.writeObjectPropertyStart("category");
            if (value.getCategory().getId() != null) {
                gen.writeNumberProperty("id", value.getCategory().getId());
            } else {
                gen.writeNullProperty("id");
            }
            gen.writeStringProperty("category", value.getCategory().getCategory());
            gen.writeEndObject();
        } else {
            gen.writeNullProperty("category");
        }

        gen.writePOJOProperty("expirationDate", value.getExpirationDate());

        if (value.getExpirationDate() != null) {
            long daysExpired = ChronoUnit.DAYS.between(
                    Instant.ofEpochMilli(value.getExpirationDate().getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),
                    LocalDateTime.now());
            gen.writeNumberProperty("daysExpired", daysExpired);
        } else {
            gen.writeNullProperty("daysExpired");
        }

        if (value.getComponents() != null) {
            gen.writeArrayPropertyStart("components");
            ComponentCli2JsonSerializer componentSerializer = new ComponentCli2JsonSerializer();
            for (ComponentCli2 component : value.getComponents()) {
                componentSerializer.serialize(component, gen, provider);
            }
            gen.writeEndArray();
        } else {
            gen.writeNullProperty("components");
        }

        gen.writeEndObject();
    }
}

