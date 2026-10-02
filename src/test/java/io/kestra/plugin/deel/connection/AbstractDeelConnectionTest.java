package io.kestra.plugin.deel.connection;

import io.kestra.plugin.deel.AbstractDeelTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AbstractDeelConnectionTest extends AbstractDeelTest {

    @Test
    void testDefaultBaseUrl() {
        // AbstractDeelConnection is abstract, but we can test defaults via subclass
        // The defaults are set in the field declarations with @Builder.Default
        assertThat(AbstractDeelConnection.DEFAULT_BASE_URL, is("https://api.letsdeel.com/rest"));
        assertThat(AbstractDeelConnection.DEFAULT_API_VERSION, is("2026-01-01"));
    }
}