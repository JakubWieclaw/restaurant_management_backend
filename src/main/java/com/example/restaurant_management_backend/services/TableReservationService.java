package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDayDTO;
import com.example.restaurant_management_backend.exceptions.InvalidReservationException;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import com.example.restaurant_management_backend.jpa.model.TableReservation;
import com.example.restaurant_management_backend.jpa.repositories.TableReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableReservationService {
    public static final String CANNOT_MAKE_RESERVATION_IN_PAST = "Nie można dokonać rezerwacji w przeszłości";
    public static final String NO_TABLE_FOUND_FOR_THIS_TIME = "Brakuje stolika w podanym przedziale czasowym";
    private final ConfigService configService;
    private final TableService tableService;
    private final TableReservationRepository tableReservationRepository;

    public List<LocalTime> checkPossibleHoursForDay(LocalDate day, int reservationDuration, int minutesToAdd, int numberOfPeople) {
        List<LocalTime> possibleReservationStartHour = new ArrayList<>();
        List<OpeningHour> openingHours = configService.getOpeningHours();
        Optional<OpeningHour> hoursForSpecificDay = openingHours.stream()
                .filter(openingHour -> openingHour.getDay().equals(day.getDayOfWeek()))
                .findAny();
        if (hoursForSpecificDay.isEmpty()) {
            return possibleReservationStartHour;
        }
        OpeningHour openingHour = hoursForSpecificDay.get();
        LocalTime possibleStartTime = openingHour.getOpeningTime();
        LocalTime closingTime = openingHour.getClosingTime();
        List<TableReservation> tableReservations = getTableReservationsForDay(day);
        int tables = tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople);
        while (ChronoUnit.MINUTES.between(possibleStartTime, closingTime) >= reservationDuration) {
            LocalTime possibleEndTime = ChronoUnit.MINUTES.addTo(possibleStartTime, reservationDuration);
            List<TableReservation> reservationsInConflict = getReservationsInConflict(possibleStartTime, possibleEndTime, tableReservations);
            if (tables > reservationsInConflict.size()) {
                possibleReservationStartHour.add(possibleStartTime);
            }
            possibleStartTime = possibleStartTime.plusMinutes(minutesToAdd);
        }
        return possibleReservationStartHour;
    }

    public List<PossibleReservationHoursForDayDTO> checkPossibleHoursForDays(List<LocalDate> days, int reservationDuration, int minutesToAdd, int numberOfPeople) {
        final var possibleReservationHours = new ArrayList<PossibleReservationHoursForDayDTO>();
        for (LocalDate day : days) {
            List<LocalTime> possibleHours = checkPossibleHoursForDay(day, reservationDuration, minutesToAdd, numberOfPeople);
            final var reservation = new PossibleReservationHoursForDayDTO(day, possibleHours);
            possibleReservationHours.add(reservation);
        }
        return possibleReservationHours;
    }

    public TableReservation makeReservation(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId) {
        if (day.isBefore(ZonedDateTime.now().toLocalDate())) {
            throw new InvalidReservationException(CANNOT_MAKE_RESERVATION_IN_PAST);
        }
        int tables = tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople);
        final var tableReservationList = getTableReservationsForDay(day);

        List<TableReservation> reservationsInConflict = getReservationsInConflict(startTime, endTime, tableReservationList);
        if (reservationsInConflict.size() >= tables) {
            // Bajer: Return the first possible time
            throw new InvalidReservationException(NO_TABLE_FOUND_FOR_THIS_TIME);
        }

        TableReservation tableReservation = new TableReservation();

        tableReservation.setEndTime(endTime);
        tableReservation.setStartTime(startTime);
        tableReservation.setPeople(numberOfPeople);
        tableReservation.setCustomerId(customerId);
        tableReservation.setDay(day);
        tableReservation.setDuration(ChronoUnit.MINUTES.between(startTime, endTime));

        return tableReservationRepository.save(tableReservation);
    }

    public List<TableReservation> getTableReservationsForDay(LocalDate day) {
        return tableReservationRepository.findAllByDay(day);
    }

    public List<TableReservation> getAllTableReservations() {
        return tableReservationRepository.findAll();
    }

    private List<TableReservation> getReservationsInConflict(LocalTime startTime, LocalTime endTime, List<TableReservation> tableReservations) {
        return tableReservations.stream()
                .filter(dbReservation ->
                        isTimeBetween(startTime, dbReservation.getStartTime(), dbReservation.getEndTime()) ||
                                isTimeBetween(endTime, dbReservation.getStartTime(), dbReservation.getEndTime())
                ).toList();
    }

    public boolean isTimeBetween(LocalTime localTime, LocalTime localTime1, LocalTime localTime2) {
        return (localTime.equals(localTime1) || localTime.isAfter(localTime1)) &&
                (localTime.equals(localTime2) || localTime.isBefore(localTime2));
    }
}
