package ar.edu.iua.iw3.integration.cli2.model;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ComponentCli2JsonSerializer extends StdSerializer<ComponentCli2> {

    public ComponentCli2JsonSerializer() {
        this(null);
    }

    public ComponentCli2JsonSerializer(Class<ComponentCli2> t) {
        super(t);
    }

    @Override
    public void serialize(ComponentCli2 value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        // {
        //     "id": 123,
        //     "component": "Harina"
        // }
        gen.writeStartObject();
        if (value.getId() != null) {
            gen.writeNumberProperty("id", value.getId());
        } else {
            gen.writeNullProperty("id");
        }
        gen.writeStringProperty("component", value.getComponent());
        gen.writeEndObject();
    }
}

