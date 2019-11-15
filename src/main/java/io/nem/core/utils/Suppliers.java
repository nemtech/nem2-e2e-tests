package io.nem.core.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by fernando on 02/08/19.
 *
 * @author Fernando Boucquez
 */
public class Suppliers {

    /**
     * Private constructor for this utility class.
     */
    private Suppliers() {

    }

    /**
     * It generates a cached version of the supplier. The delegate supplier is only called once
     * regardless of how may the client calls get().
     *
     * @param delegate the delegate
     * @param <T> the type of the supplier response.
     * @return a cached version of the supplier.
     */
    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        AtomicReference<T> value = new AtomicReference<>();
        return () -> {
            T val = value.get();
            if (val == null) {
                val = value.updateAndGet(cur -> cur == null ?
                    Objects.requireNonNull(delegate.get()) : cur);
            }
            return val;
        };
    }
}
