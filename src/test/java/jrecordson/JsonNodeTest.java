package jrecordson;

import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class JsonNodeTest {

    private final String inputJson = """
            {
              "id": 123,
              "name": "Bob Loblaw",
              "roles": [
                {
                  "id": 1,
                  "name": "Manager"
                }
              ]
            }""";
    private final UserRecord userRecord = new UserRecord(
        123,
        "Bob Loblaw",
        singletonList(new RoleRecord(1, "Manager"))
    );

    @BeforeClass
    public static void setUp() {
        JsonNode.setPretty();
    }

    @Test
    public void serializationOfUser() {
        var node = JsonNode.parse(inputJson);
        var unpretty = "{" +
            "\"id\":123,\"name\":\"Bob Loblaw\"," +
            "\"roles\":[{\"id\":1,\"name\":\"Manager\"}]" +
        "}";
        assertEquals(unpretty, node.toString());
    }

    @Test
    public void parseAndMapToRecord() {
        var node = JsonNode.parse(inputJson);
        var user = node.asA(UserRecord.class);
        assertEquals(userRecord, user);
    }

    @Test
    public void prettyOutput() {
        var node = JsonNode.parse(inputJson);
        assertEquals(inputJson, node.jsonString());
    }

}
