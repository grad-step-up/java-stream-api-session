package com.thoughtworks.training.session;

import java.util.Arrays;
import java.util.List;

public class Main {

  public static void main(String[] args) throws Exception {
    List<Person> persons =
        Arrays.asList(
            new Person("Max", 18),
            new Person("Peter", 23),
            new Person("Pamela", 23),
            new Person("David", 12));   


    Integer ageSum = persons
        .stream()
            .parallel()
        .reduce(0,
            (sum, p) -> {
                System.out.format("accumulator: sum=%s; person=%s\n", sum, p);
                return sum + p.age;
            },
            (sum1, sum2) -> {
                System.out.format("combiner: sum1=%s; sum2=%s\n", sum1, sum2);
                return sum1 + sum2;
            });

    System.out.println(ageSum);
  }

}
class Person {
  String name;
  int age;

  Person(String name, int age) {
      this.name = name;
      this.age = age;
  }

  @Override
  public String toString() {
      return name;
  }
}