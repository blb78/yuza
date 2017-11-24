package com.skillogs.yuza;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface TestUtils {

    @SafeVarargs
    static <T> T build(Supplier<T> factory, Consumer<T>... cons) {
        T instance = factory.get();
        for (Consumer<T> con : cons) {
            con.accept(instance);
        }
        return instance;
    }

    static <U, T> List<T> extract(Collection<U> errors, Function<U, T> mapper) {
        return errors.stream().map(mapper).collect(Collectors.toList());
    }

}
