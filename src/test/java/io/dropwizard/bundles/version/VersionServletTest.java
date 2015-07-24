package io.dropwizard.bundles.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;


public class VersionServletTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String PATH = "/version";

    private final ServletTester tester = new ServletTester();
    private final VersionSupplier supplier = mock(VersionSupplier.class);

    @Before
    public void setup() throws Exception {
        tester.addServlet(VersionServlet.class, PATH).setServlet(new VersionServlet(supplier, OBJECT_MAPPER, true));
        tester.start();
    }

    @After
    public void teardown() throws Exception {
        tester.stop();
    }

    @Test
    public void testNonNullApplicationVersion() {
        when(supplier.getApplicationVersion()).thenReturn("version");

        HttpTester response = get();
        assertEquals(200, response.getStatus());

        JsonNode root = fromJson(response.getContent());
        assertEquals("version", root.get("version").textValue());
    }

    @Test
    public void testNullApplicationVersion() {
        when(supplier.getApplicationVersion()).thenReturn(null);

        HttpTester response = get();
        assertEquals(200, response.getStatus());

        JsonNode root = fromJson(response.getContent());
        assertNull(root.get("version").textValue());
    }

    @Test
    public void testThrowsApplicationVersionException() {
        RuntimeException exception = new RuntimeException();
        when(supplier.getApplicationVersion()).thenThrow(exception);

        HttpTester response = get();
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testNonNullDependencyVersion() {
        when(supplier.getDependencyVersions()).thenReturn(map("guava", "version"));

        HttpTester response = get();
        assertEquals(200, response.getStatus());

        JsonNode root = fromJson(response.getContent());
        assertEquals("version", root.get("dependencies").get("guava").textValue());
    }

    @Test
    public void testNullDependencyVersion() {
        when(supplier.getDependencyVersions()).thenReturn(map("guava", null));

        HttpTester response = get();
        assertEquals(200, response.getStatus());

        JsonNode root = fromJson(response.getContent());
        assertNull(root.get("dependencies").get("guava").textValue());
    }

    @Test
    public void testThrowsDependencyVersionException() {
        RuntimeException exception = new RuntimeException();
        when(supplier.getDependencyVersions()).thenThrow(exception);

        HttpTester response = get();
        assertEquals(500, response.getStatus());
    }

        private HttpTester get() {
        HttpTester request = new HttpTester();
        HttpTester response = new HttpTester();
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI(PATH);
        try {
            ByteArrayBuffer reqsBuff = new ByteArrayBuffer(request.generate().getBytes());
            //TODO
            ByteArrayBuffer respBuff = tester.getResponses(reqsBuff);
            response.parse(respBuff.asArray());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return response;
    }

    private static JsonNode fromJson(String s) {
        try {
            return OBJECT_MAPPER.readTree(s);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Map<String, String> map(String key, String value) {
        Map<String, String> m = Maps.newHashMap();
        m.put(key, value);
        return m;
    }
}
