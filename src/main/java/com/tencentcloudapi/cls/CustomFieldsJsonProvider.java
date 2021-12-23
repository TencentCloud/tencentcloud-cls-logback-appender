package com.tencentcloudapi.cls;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * @author farmerx
 */
public class CustomFieldsJsonProvider {
    private String customFields;

    private ObjectNode customFieldsNode;

    private JsonFactory jsonFactory = new MappingJsonFactory().enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

    public void initializeCustomFields() {
        if (customFieldsNode != null || customFields == null) {
            return;
        }
        if (jsonFactory == null) {
            throw new IllegalStateException("JsonFactory has not been set");
        }

        try {
            this.customFieldsNode = JsonReadingUtils.readFullyAsObjectNode(this.jsonFactory, this.customFields);
        } catch (IOException e) {
            throw new IllegalStateException("[customFields] is not a valid JSON object", e);
        }
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
        this.customFieldsNode = null;
    }

    public String getCustomFields() {
        return customFields;
    }

    public ObjectNode getCustomFieldsNode() {
        return this.customFieldsNode;
    }

    public void setCustomFieldsNode(ObjectNode customFields) {
        this.customFieldsNode = customFields;
        this.customFields = null;
    }

}