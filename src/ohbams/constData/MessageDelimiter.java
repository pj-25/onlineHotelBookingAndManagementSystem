package ohbams.constData;

public enum MessageDelimiter {

    REQUEST_TYPE_DELIMITER("/"),
    RESPONSE_TYPE_DELIMITER("~"),
    EVENT_TYPE_DELIMITER(">"),
    ENTITY_ATTRIBUTES_DELIMITER(";"),
    ENTITY_LIST_DELIMITER(",")
    ;
    private final String delimiter;

    MessageDelimiter(String delimiter){
        this.delimiter = delimiter;
    }

    @Override
    public String toString() {
        return delimiter;
    }
}
