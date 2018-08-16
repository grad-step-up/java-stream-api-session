package com.thoughtworks.training.session;

import org.junit.Test;

public class FunctionalInterfaceTest {

    public Integer toNumber(String value, Converter<Integer> converter) {
        return converter.convert(value);
    }

    @Test
    public void traditionalImplementation() {
        toNumber("123", new IntegerConverter());
    }

    @Test
    public void anonymousClass() {
        toNumber("123", new Converter<Integer>() {
            @Override
            public Integer convert(String value) {
                return Integer.valueOf(value);
            }
        });
    }

    @Test
    public void lambda() {
        toNumber("123", value -> Integer.valueOf(value));
    }

    @Test
    public void methodReference() {
        toNumber("123", Integer::valueOf);
    }

    @FunctionalInterface
    private interface Converter<T extends Number> {
        T convert(String value);

    }

    private class IntegerConverter implements Converter<Integer> {
        @Override
        public Integer convert(String value) {
            return Integer.valueOf(value);
        }
    }
}
