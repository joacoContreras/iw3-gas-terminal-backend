package ar.edu.iua.iw3.util;

import java.text.SimpleDateFormat;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class JsonUtiles {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObjectMapper getObjectMapper(Class clazz, ValueSerializer ser, String dateFormat) {
        SimpleModule module = new SimpleModule();
        if (ser != null) {
            module.addSerializer(clazz, ser);
        }
        JsonMapper.Builder builder = JsonMapper.builder().addModule(module);
        if (dateFormat != null) {
            builder.defaultDateFormat(new SimpleDateFormat(dateFormat));
        }
        return builder.build();
    }
}
