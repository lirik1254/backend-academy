package backend.academy.samples;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Instancio is a library for instantiating and populating objects with random data, making your tests more dynamic.
 * Each test run is against a new set of inputs.
 * <p>
 * <b>Why use it?</b>
 * <p>
 * The goal is to reduce the time and lines of code spent on manual data setup in unit tests,
 * and potentially to uncover bugs that may have gone unnoticed with static test data.
 * <p>
 * <b>How does it work?</b>
 * <p>
 * Instancio uses reflection to populate objects, including nested objects and collections.
 * A single method call provides you with a fully populated instance of a class, ready to be used as an input to your test case.
 * <p>
 * <a href="https://www.instancio.org/getting-started/">Library documentation</a>
 */
public class InstancioExamplesTest {
    public record Person(String name, int age, Address address, String phone) {
    }

    public record Address(String city) {
    }

    @Test
    public void create() {
        // Create by specifying the class
        Person person = Instancio.create(Person.class);
        assertThat(person).hasNoNullFieldsOrProperties();

        // Using type tokens
        ImmutablePair<String, Long> pair = Instancio.create(new TypeToken<>() {
        });
        assertThat(pair).isNotNull();

        Map<Integer, List<String>> map = Instancio.create(new TypeToken<>() {
        });
        assertThat(map).isNotEmpty();

        // Create from a model
        Model<Person> personModel = Instancio.of(Person.class)
            .ignore(field(Person::age))
            .toModel();
        Person personWithoutAgeAndAddress = Instancio.of(personModel)
            .ignore(field(Person::address))
            .create();
        assertThat(personWithoutAgeAndAddress)
            .hasNoNullFieldsOrPropertiesExcept("address");
    }

    @Test
    public void createCollections() {
        List<Person> list1 = Instancio.ofList(Person.class).size(10).create();
        assertThat(list1).hasSize(10);
        var list2 = Instancio.<ImmutablePair<String, Integer>>ofList(new TypeToken<>() {
        }).create();
        assertThat(list2).hasSizeBetween(1, 10);

        Map<UUID, Address> map = Instancio.ofMap(UUID.class, Address.class).size(3)
            .set(field(Address::city), "Vancouver")
            .create();
        assertThat(map).hasSize(3);

        // Create from a model
        Model<Person> personModel = Instancio.of(Person.class)
            .ignore(field(Person::age))
            .toModel();
        Set<Person> set = Instancio.ofSet(personModel).size(5).create();
        assertThat(set).hasSize(5);
    }

    @Test
    public void generateData() {
        Person person = Instancio.of(Person.class)
            .generate(field("age"), gen -> gen.ints().range(18, 65))
            .generate(field(Address.class, "city"), gen -> gen.string().length(10))
            .generate(field("phone"), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
            .create();

        assertThat(person.phone()).containsPattern("\\d{3}-\\d{2}-\\d{2}");
    }
}
