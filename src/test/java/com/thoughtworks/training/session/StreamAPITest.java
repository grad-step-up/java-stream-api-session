package com.thoughtworks.training.session;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class StreamAPITest {

    @Test
    public void getStream() {
        Integer[] array = {1, 2, 3, 4, 5};
        Arrays.stream(array);

        List<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);
        ((Collection) list).stream();

        Map<Integer, String> map = ImmutableMap.of(1, "a", 2, "b");
        map.entrySet().stream().forEach(entry -> String.format("%s, %s", entry.getKey(), entry.getValue()));
    }

    @Test
    public void foreach() {
        List<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);
        list.forEach(integer -> System.out.println(integer));
        list.stream().forEach(integer -> System.out.println(integer));
        Map<Integer, String> map = ImmutableMap.of(1, "a", 2, "b");
        map.forEach((key, value) -> String.format("%s, %s", key, value));
        map.entrySet().forEach(entry -> String.format("%s, %s", entry.getKey(), entry.getValue()));
    }

    @Test
    public void filter() {
        List<Integer> result = IntStream.range(1, 10).boxed()
                .filter(integer -> integer % 2 == 0)
                .collect(Collectors.toList());

        assertThat(StringUtils.join(result, ""), is("2468"));
    }

    @Test
    public void map() {
        List<Integer> result = IntStream.range(1, 10).boxed()
                .map(value -> value - 1)
                .collect(Collectors.toList());

        assertThat(StringUtils.join(result, ""), is("012345678"));
    }

    @Test
    public void flattenMap() {
        List<List<Integer>> lists = ImmutableList.of(
                ImmutableList.of(1, 3, 5),
                ImmutableList.of(2, 4)
        );
        List<Integer> result = lists.stream().flatMap(list -> list.stream())
                .collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void collectToMap() {
        Map<Integer, String> result = IntStream.range(1, 10).boxed()
                .collect(Collectors.toMap(Function.identity(),
                        Object::toString));
        System.out.println(result);

        result = IntStream.range(1, 10).boxed()
                .map(number -> Pair.of(number, number.toString()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        System.out.println(result);
    }

    @Test
    public void find() {
        Optional<Integer> anyMatch = IntStream.range(1, 10).boxed()
                .parallel()
                .filter(number -> number % 2 == 0)
                .findAny();

        Optional<Integer> firstMatch = IntStream.range(1, 10).boxed()
                .parallel()
                .filter(number -> number % 2 == 0)
                .findFirst();

        assertThat(anyMatch.get(), isIn(ImmutableList.of(2, 4, 6, 8)));
        assertThat(firstMatch.get(), isIn(ImmutableList.of(2)));
    }

    @Test
    public void distinct() {
        List<Integer> result = IntStream.range(1, 10).boxed()
                .map(integer -> integer / 2)
                .distinct()
                .collect(Collectors.toList());

        assertThat(StringUtils.join(result, ""), is("01234"));
    }

    @Test
    public void limit() {
        List<Integer> result = IntStream.range(1, 10).boxed()
                .limit(5)
                .collect(Collectors.toList());

        assertThat(StringUtils.join(result, ""), is("12345"));
    }

    @Test
    public void sort() {
        List<Integer> result = IntStream.range(1, 10).boxed()
                .sorted((lhs, rhs) -> rhs - lhs)
                .collect(Collectors.toList());

        assertThat(StringUtils.join(result, ""), is("987654321"));
    }

    @Test
    public void Matchers() {
        assertTrue(IntStream.range(1, 10).boxed().allMatch(value -> value < 10));
        assertFalse(IntStream.range(1, 10).boxed().allMatch(value -> value < 5));
        assertTrue(IntStream.range(1, 10).boxed().anyMatch(value -> value < 5));
        assertTrue(IntStream.range(1, 10).boxed().noneMatch(value -> value > 10));
    }

    @Test
    public void forEach() {
        IntStream.range(1, 10).boxed()
                .parallel()
                .forEach(integer -> System.out.println(integer));
        System.out.println("=======================");
        IntStream.range(1, 10).boxed()
                .parallel()
                .forEachOrdered(integer -> System.out.println(integer));
    }

    @Test
    public void reduce() {
        assertThat(IntStream.range(1, 10).boxed()
                        .reduce(0, (sum, value) -> sum + value),
                is(45));
        assertThat(IntStream.range(1, 10).boxed()
                        .reduce((sum, value) -> sum + value)
                        .get(),
                is(45));
        assertThat(IntStream.range(1, 10).boxed()
                        .reduce(0,
                                (sum, value) -> sum + value,
                                (sum, accumulatedValue) -> sum + accumulatedValue),

                is(45));
        Map<Object, Object> result = IntStream.range(1, 10).boxed()
                .reduce(ImmutableMap.of(),
                        (map, value) -> ImmutableMap.builder()
                                .putAll(map).put(value, value.toString())
                                .build(),
                        (map, accumulatedMap) -> ImmutableMap.builder()
                                .putAll(map).putAll(accumulatedMap)
                                .build());
        System.out.println(result);
    }

    @Test
    public void reduceInternals() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        assertThat(IntStream.range(1, 10).boxed()
                        .parallel()
                        .reduce(0,
                                (sum, value) -> {
                                    int result = sum + value;
                                    sleep(500);
                                    System.out.println(String.format("[THREAD:%s]accumulator: sum:%s value:%s result:%s",
                                            Thread.currentThread().getName(), sum, value, result));
                                    return result;
                                },
                                (sum, accumulatedValue) ->
                                {
                                    int result = sum + accumulatedValue;
                                    sleep(500);
                                    System.out.println(String.format("[THREAD:%s]combiner: sum:%s value:%s result:%s",
                                            Thread.currentThread().getName(), sum, accumulatedValue, result));
                                    return result;
                                }),

                is(45));
        System.out.println(String.format("total costs:%sms", stopwatch.elapsed(TimeUnit.MILLISECONDS)));
    }

    @Test
    public void reduceToMapInternals() {
        Map result = IntStream.range(1, 10).boxed()
                .parallel()
                .reduce((Map) new HashMap<Integer, String>() {{
                            put(-1, "asd");
                        }},
                        (map, value) -> {
                            sleep(500);
                            System.out.println(String.format("[THREAD:%s]accumulator: put %s into %s",
                                    Thread.currentThread().getName(), value, map));
//                            map.put(value, value.toString());
//                            return map;
                            return new HashMap<Integer, String>() {{
                                put(value, value.toString());
                            }};
                        },
                        (map, accumulatedMap) -> {
                            System.out.println(String.format("[THREAD:%s]start  combiner: put %s into %s",
                                    Thread.currentThread().getName(), accumulatedMap, map));
                            sleep(500);
                            System.out.println(String.format("[THREAD:%s]finish combiner: put %s into %s",
                                    Thread.currentThread().getName(), accumulatedMap, map));
                            map.putAll(accumulatedMap);
                            return map;
                        });
        System.out.println(result);
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}