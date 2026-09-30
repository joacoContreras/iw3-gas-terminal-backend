package ar.edu.iua.iw3.util;

import java.text.SimpleDateFormat;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObjectMapper getObjectMapper(Class clazz, ValueDeserializer deser, String dateFormat) {
        SimpleModule module = new SimpleModule();
        if (deser != null) {
            module.addDeserializer(clazz, deser);
        }
        JsonMapper.Builder builder = JsonMapper.builder().addModule(module);
        if (dateFormat != null) {
            builder.defaultDateFormat(new SimpleDateFormat(dateFormat));
        }
        return builder.build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObjectMapper getObjectMapper(Class clazz, ValueSerializer ser, ValueDeserializer deser, String dateFormat) {
        SimpleModule module = new SimpleModule();
        if (ser != null) {
            module.addSerializer(clazz, ser);
        }
        if (deser != null) {
            module.addDeserializer(clazz, deser);
        }
        JsonMapper.Builder builder = JsonMapper.builder().addModule(module);
        if (dateFormat != null) {
            builder.defaultDateFormat(new SimpleDateFormat(dateFormat));
        }
        return builder.build();
    }

    public static String getString(JsonNode node, String[] keys, String defaultValue) {
        if (node != null && keys != null) {
            for (String key : keys) {
                key = key.trim();
                if (node.hasNonNull(key)) {
                    return node.get(key).asString(defaultValue);
                }
            }
        }
        return defaultValue;
    }

    public static double getDouble(JsonNode node, String[] keys, double defaultValue) {
        if (node != null && keys != null) {
            for (String key : keys) {
                key = key.trim();
                if (node.hasNonNull(key)) {
                    return node.get(key).asDouble(defaultValue);
                }
            }
        }
        return defaultValue;
    }

    public static boolean getBoolean(JsonNode node, String[] keys, boolean defaultValue) {
        if (node != null && keys != null) {
            for (String key : keys) {
                key = key.trim();
                if (node.hasNonNull(key)) {
                    return node.get(key).asBoolean(defaultValue);
                }
            }
        }
        return defaultValue;
    }
}

