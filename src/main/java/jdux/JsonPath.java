package jdux;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdux.JsonPickType.CHILD;

// TODO cleanup
public class JsonPath {

    private static final Pattern PATH_ELEM = Pattern.compile("(?<type>^|\\.\\.?)(?<key>\\w+)");

    static JsonSelector parse(String str) {
        if (str == null || str.isBlank())
            return null; // TODO?
        Matcher matcher = PATH_ELEM.matcher(str);
        if (!matcher.find())
            throw new JsonPathParseException("Invalid path " + str);
        JsonPathElem head = readNextSegment(0, matcher), current = head;
        int index = matcher.end();
        while (matcher.find(index)) {
            current = current.next = readNextSegment(index, matcher);
            index = matcher.end();
        }
        return head;
    }

    private static JsonPathElem readNextSegment(int index, Matcher matcher) {
        if (matcher.start() != index)
            throw new JsonPathParseException("Unexpected characters");
        JsonPickType type = matcher.group("type").length() > 1
            ? JsonPickType.DESCENDANT
            : CHILD;
        return new JsonPathElem(type, matcher.group("key"));
    }

    private static class JsonPathElem implements JsonSelector {

        final JsonPickType type;
        final String key;
        JsonPathElem next;

        JsonPathElem(JsonPickType type, String key) {
            this.type = type;
            this.key = key;
        }

        @Override
        public JsonPickType type() {
            return type;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public boolean contains(JsonSelector other) {
            return other.toString("", "!")
                .matches(toString("", ".*"));
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public JsonSelector next() {
            return next;
        }

        @Override
        public String toString(String child, String ancestor) {
            StringBuilder sb = new StringBuilder();
            sb.append(type == CHILD ? key : ancestor + key);
            for (JsonPathElem e = next; e != null; e = e.next)
                sb.append(e.type == CHILD ? child : ancestor).append(e.key);
            return sb.toString();
        }

        @Override
        public String toString() {
            return toString(".", "..");
        }

    }

}