package com.dineup.common;

import com.dineup.common.util.SanitizeStringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import static org.assertj.core.api.Assertions.assertThat;

class SanitizeStringDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new SanitizeStringDeserializer());

        objectMapper = JsonMapper.builder()
                .addModule(module)
                .build();
    }

    @Test
    void shouldReturnNull_whenValueIsNull() {
        String result = objectMapper.readValue("null", String.class);
        assertThat(result).isNull();
    }

    @Test
    void shouldTrimWhitespace() {
        String result = objectMapper.readValue("\"    Trim Whitespace    \"", String.class);
        assertThat(result).isEqualTo("Trim Whitespace");
    }

    @Test
    void shouldNormalizeWhitespace() {
        String result = objectMapper.readValue("\"Normalize   the     Whitespaces\"", String.class);
        assertThat(result).isEqualTo("Normalize the Whitespaces");
    }

    @Test
    void shouldStripHtmlTags() {
        String result = objectMapper.readValue("\"<script>alert('xss')</script>Html Tag\"", String.class);
        assertThat(result).isEqualTo("Html Tag");
    }
}
