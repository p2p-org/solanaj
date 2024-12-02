package org.p2p.solanaj.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Base64;
import org.junit.Test;

public class VersionedTransactionTest {
    /// decode a versioned transaction from jupiter
    @Test
    public void decodeTest() throws IOException {

        // base64 format transaction
        String tx = "AQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAQAJDqrCTYbyMoYXIoSuFqDY1HT81wTRHrlON+KLkERP5qolG7kmQ9n8VzfnOBHAyio/rp6mHwe1Y2cGcMnysm6mbVv1bmMTMLzFs9bjyTTohOzlA9Rr9a53CHt0Iz2EoUf7QZbNjff2LuN7YA6337LNNnFVDzAA9eVX0XMcjlG/GKEHyre+vbxjfB2Nef5PF6BSZF5Z5XXHENtvGR0a8C80qJcDBkZv5SEXMv/srbpyw5vnvIzlu8X3EmssQ5s6QAAAAIyXJY9OJInxuz0QKRSODYMLWhOZ2v8QhASOe9jb6fhZBpuIV/6rgYT7aH9jRhjANdrEOdwa6ztVmKDwAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpxvp6877brTo9ZfNqq8l0MbG75MLS9uDkfKYCA0UvXWEKw0qWwWZxWmDBIz7KJYoN8wseyFjgdFxzahJiZmNLIhCJic3MYY8GdnSsZ34pmq2xKP4g2czYKOUIyfG/mzDrkn9sh+xDEAWX2S7IDh4PwuUsi7/8dQ+DB2YEV+4zsuE9kEG+e4PYPhpf4PU7zFnCIKWrFflhKcyMP2EcGxGk9ggFAAUCYNoBAAUACQPftgkAAAAAAAYGAAEABwgJAQEIAgABDAIAAACA8PoCAAAAAAkBAQERBgYAAgAKCAkBAQsPAAADBAEMCwcKCQkIBg0LIoVuSq9wn/WflkXXqlLAdQWA8PoCAAAAAADh9QUAAAAAAAAJAwEAAAEJAA==";

        byte[] txBytes = Base64.getDecoder().decode(tx);
        VersionedTransaction versionedTransaction = VersionedTransaction.fromEncodeedTransaction(txBytes);

        byte[] serializeTx = versionedTransaction.serialize();
        String base64EncodeTx = Base64.getEncoder().encodeToString(serializeTx);

        assertEquals(tx, base64EncodeTx);
    }
}
