package backend.academy.samples.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface CarMapper {
    @Mapping(target = "manufacturer", source = "make")
    @Mapping(target = "seatCount", source = "numberOfSeats")
    @Mapping(target = "mileageInKilometers", source = "mileageInMiles", qualifiedByName = "milesToKilometers")
    CarDto carToCarDto(Car car);

    @Mapping(target = "fullName", source = "name")
    PersonDto personToPersonDto(Person person);

    // the default method is used for custom complex mapping
    @Named("milesToKilometers")
    default double milesToKilometers(double miles) {
        return miles * 1.609344;
    }
}
