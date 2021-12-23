package com.tencentcloudapi.cls;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * @author farmerx
 */
public class JsonReadingUtils {

    public static JsonNode readFully(JsonFactory jsonFactory, String json) throws IOException {
        if (json == null) {
            return null;
        }

        final String trimmedJson = json.trim();
        try (JsonParser parser = jsonFactory.createParser(trimmedJson)) {
            final JsonNode tree = parser.readValueAsTree();

            if (parser.getCurrentLocation().getCharOffset() < trimmedJson.length()) {
                throw new JsonParseException(parser, "unexpected character");
            }

            return tree;
        }
    }

    public static ObjectNode readFullyAsObjectNode(JsonFactory jsonFactory, String json) throws IOException {
        final JsonNode node = readFully(jsonFactory, json);

        if (node != null && !(node instanceof ObjectNode)) {
            throw new JsonParseException(null, "expected a JSON object representation");
        }

        return (ObjectNode) node;
    }
}
