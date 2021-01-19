package service.health.check.server.sharding;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class HashFunction {
    public static long getHashValue(String s) {
        return Hashing.sha256()
                .hashString(s, Charset.defaultCharset()).asInt();
    }

    public static long MIN_VALUE_INCLUSIVE = Integer.MIN_VALUE;
    public static long MAX_VALUE_EXCLUSIVE = (long) Integer.MAX_VALUE + 1;
}
