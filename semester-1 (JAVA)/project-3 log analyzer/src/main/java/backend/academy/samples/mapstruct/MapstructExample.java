package backend.academy.samples.mapstruct;

import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct is a compile-time declarative code generator that simplifies conversion between Java classes.
 * All you need is to define the interface with annotations – mapstruct will generate an implementation.
 * <p>
 * <a href="https://mapstruct.org/documentation/installation/">Library documentation</a>
 */
@Log4j2
public class MapstructExample {
    public static void main(String[] args) {
        // or define public static final instance directly in CarMapper interface
        CarMapper mapper = Mappers.getMapper(CarMapper.class);

        CarDto volvo = mapper.carToCarDto(new Car("Volvo", 4, 3.65));
        log.info(volvo);
        PersonDto person = mapper.personToPersonDto(new Person("Alexander Biryukov"));
        log.info(person);

    }
}
