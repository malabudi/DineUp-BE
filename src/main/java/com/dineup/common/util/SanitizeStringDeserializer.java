package com.dineup.common.util;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@JacksonComponent
public class SanitizeStringDeserializer extends ValueDeserializer<String> {

    private static final PolicyFactory SANITIZER_POLICY = new HtmlPolicyBuilder().toFactory();

    @Override
    public String deserialize(JsonParser parser, DeserializationContext ctx) {
        String value = parser.getValueAsString();

        if (value == null) {
            return null;
        }

        // Time and normalize whitespaces
        value = value.trim().replaceAll("\\s+", " ");

        // Strip HTML tags and escape dangerous characters to prevent HTML injection and XSS attacks
        value = SANITIZER_POLICY.sanitize(value);

        return StringEscapeUtils.unescapeHtml4(value);
    }
}