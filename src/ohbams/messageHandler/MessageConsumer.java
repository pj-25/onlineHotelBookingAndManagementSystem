package ohbams.messageHandler;

import java.io.IOException;

@FunctionalInterface
public interface MessageConsumer {
    void consume(String ...data) throws IOException;
}
