package com.example.onlinecodecompilerapi.helpers;

import java.util.Map;

public class Parser {

    private Map<String[], String> values;

    public Parser(Map<String[], String> values) {
        this.values = values;
    }

    public Map<String[], String> getValues() {
        return values;
    }

    public void setValues(Map<String[], String> values) {
        this.values = values;
    }

    public String getParseValues() {

        StringBuilder parsedValues = new StringBuilder();

        for (Map.Entry<String[], String> typeValue : values.entrySet()) {

            parsedValues.append("[");
            parsedValues.append(typeValue.getKey()[0]);
            parsedValues.append("|");
            parsedValues.append(typeValue.getKey()[1]);
            parsedValues.append("|");
            parsedValues.append(typeValue.getValue());
            parsedValues.append("]");
            parsedValues.append("\n");

        }


        return parsedValues.toString();

    }

}
