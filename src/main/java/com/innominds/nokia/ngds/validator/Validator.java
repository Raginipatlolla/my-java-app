package com.innominds.nokia.ngds.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class Validator {
    public static List<String> validateJsonAgainstSchema(Map<String, Object> requestBody) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ObjectMapper objectMapper = new ObjectMapper();
       JsonNode jsonNode = objectMapper.valueToTree(requestBody);
      //  JsonNode dataNode = objectMapper.readTree(classLoader.getResourceAsStream("data.json"));

        JsonNode schemaNode = objectMapper.readTree(classLoader.getResourceAsStream("schema.json"));

        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaNode.toString()));
        Schema schema = SchemaLoader.load(rawSchema);

        try {
            schema.validate(new JSONObject(jsonNode.toString())); // Will throw ValidationException on error
            System.out.println("JSON is valid");
        } catch (ValidationException e) {
            System.out.println("JSON validation failed:");
            errorMessages = printCustomErrors(e, schemaNode);
        }
        return errorMessages;
    }
    private static List<String> printCustomErrors(ValidationException e, JsonNode schemaNode) {
        List<String> errors = new ArrayList<>();
        for (ValidationException cause : e.getCausingExceptions().isEmpty() ? List.of(e) : e.getCausingExceptions()) {
            String pointer = cause.getPointerToViolation(); // JSON path like "#/sites/0/sites/geography_type"
            String[] pathParts = pointer.replaceFirst("^#/", "").split("/");

            JsonNode currentNode = schemaNode;
            for (String part : pathParts) {
                if (currentNode == null) break;
                if (currentNode.has("properties")) currentNode = currentNode.get("properties");
                if (currentNode.has("items") && part.matches("\\d+")) currentNode = currentNode.get("items");
                else currentNode = currentNode.get(part);
            }

            Optional<String> customError = Optional.ofNullable(currentNode)
                    .map(n -> n.get("errorMessage"))
                    .map(JsonNode::asText);

            String errorMsg = pointer + " - " +
                    customError.orElseGet(() -> "Default: " + cause.getMessage());
            errors.add(errorMsg);
        }
        return errors;
    }



}
