package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.dto.PossibleReservationHoursForDayDTO;
import com.example.restaurant_management_backend.exceptions.InvalidReservationException;
import com.example.restaurant_management_backend.exceptions.NotFoundException;
import com.example.restaurant_management_backend.jpa.model.OpeningHour;
import com.example.restaurant_management_backend.jpa.model.Table;
import com.example.restaurant_management_backend.jpa.model.TableReservation;
import com.example.restaurant_management_backend.jpa.model.command.MakeReservationCommand;
import com.example.restaurant_management_backend.jpa.repositories.TableReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableReservationService {
    public static final String CANNOT_MAKE_RESERVATION_IN_PAST = "Nie można dokonać rezerwacji w przeszłości";
    public static final String NO_TABLE_FOUND_FOR_THIS_TIME = "Brakuje stolika w podanym przedziale czasowym";
    public static final String TABLE_WITH_THIS_ID_IS_ALREADY_TAKEN = "Stolik o podanym id jest już zajęty";
    public static final String TABLE_DOES_NOT_EXIST_OR_CAPACITY_IS_TOO_LOW = "Podany stolik nie istnieje bądź nie jest w stanie pomieścić podanej liczby osób";
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
        int tables = tableService.countTablesWithGreaterOrEqualCapacity(numberOfPeople);
        List<TableReservation> tableReservations = getTableReservationsForDay(day);
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

    public TableReservation makeReservation(MakeReservationCommand request) {
        return makeReservation(request.getDay(), request.getStartTime(), request.getEndTime(), request.getNumberOfPeople(), request.getCustomerId());
    }

    public TableReservation makeReservation(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId) {
        if (day.isBefore(ZonedDateTime.now().toLocalDate())) {
            throw new InvalidReservationException(CANNOT_MAKE_RESERVATION_IN_PAST);
        }
        final var tables = tableService.findTablesWithGreaterOrEqualCapacity(numberOfPeople);
        final var tableReservationList = getTableReservationsForDay(day);

        List<TableReservation> reservationsInConflict = getReservationsInConflict(startTime, endTime, tableReservationList);
        if (reservationsInConflict.size() >= tables.size()) {
            // Bajer: Return the first possible time
            throw new InvalidReservationException(NO_TABLE_FOUND_FOR_THIS_TIME);
        }

        Table tableForReservation = getTableForReservation(reservationsInConflict, tables);

        return fillTableReservation(day, startTime, endTime, numberOfPeople, customerId, tableForReservation);
    }

    public TableReservation makeReservation(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId, String requestedTableId) {
        checkIfReservationIsNotInPast(day);

        var tables = tableService.findTablesWithGreaterOrEqualCapacity(numberOfPeople);
        var conflictingReservations = getReservationsInConflict(startTime, endTime, getTableReservationsForDay(day));

        Table table = checkIfRequestedTableIsValid(conflictingReservations, tables, requestedTableId);

        return fillTableReservation(day, startTime, endTime, numberOfPeople, customerId, table);
    }

    private void checkIfReservationIsNotInPast(LocalDate day) {
        if (day.isBefore(ZonedDateTime.now().toLocalDate())) {
            throw new InvalidReservationException(CANNOT_MAKE_RESERVATION_IN_PAST);
        }
    }

    public TableReservation findOrCreateReservation(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId, String requestedTableId) {
        // Check if a suitable reservation already exists
        List<TableReservation> existingReservations = getTableReservationsForDay(day);
        for (TableReservation reservation : existingReservations) {
            if (reservation.getPeople() == numberOfPeople && reservation.getCustomerId().equals(customerId) && reservation.getTableId().equals(requestedTableId) &&
                    startTime.isBefore(reservation.getEndTime()) && endTime.isAfter(reservation.getStartTime())) {
                return reservation; // Return existing reservation if found
            }
        }

        // No existing reservation found, so create a new one
        return makeReservation(day, startTime, endTime, numberOfPeople, customerId, requestedTableId);
    }

    public List<TableReservation> getAllTableReservations() {
        return tableReservationRepository.findAll();
    }

    public List<TableReservation> getTableReservationsForDay(LocalDate day) {
        return tableReservationRepository.findAllByDay(day);
    }

    private List<TableReservation> getReservationsInConflict(LocalTime startTime, LocalTime endTime, List<TableReservation> tableReservations) {
        return tableReservations.stream()
                .filter(dbReservation ->
                        isTimeBetween(startTime, dbReservation.getStartTime(), dbReservation.getEndTime()) ||
                                isTimeBetween(endTime, dbReservation.getStartTime(), dbReservation.getEndTime())
                ).toList();
    }

    private Table getTableForReservation(List<TableReservation> reservationsInConflict, List<Table> tables) {
        List<String> takenTables = reservationsInConflict.stream()
                .map(TableReservation::getTableId)
                .toList();
        return tables.stream()
                .filter(table -> !takenTables.contains(table.getId()))
                .min(Comparator.comparingInt(Table::getCapacity))
                .orElseThrow(() -> new InvalidReservationException(NO_TABLE_FOUND_FOR_THIS_TIME));
    }

    private Table checkIfRequestedTableIsValid(List<TableReservation> reservationsInConflict, List<Table> tables, String requestedTableId) {
        List<String> idsOfTakenTables = reservationsInConflict.stream()
                .map(TableReservation::getTableId)
                .toList();
        if (idsOfTakenTables.contains(requestedTableId)) {
            throw new InvalidReservationException(TABLE_WITH_THIS_ID_IS_ALREADY_TAKEN);
        }
        return tables.stream()
                .filter(table -> table.getId().equals(requestedTableId))
                .findFirst().orElseThrow(() -> new InvalidReservationException(TABLE_DOES_NOT_EXIST_OR_CAPACITY_IS_TOO_LOW));
    }

    public TableReservation getTableReservationById(Long id) {
        return tableReservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono rezerwacji o podanym id " + id));
    }

    private TableReservation fillTableReservation(LocalDate day, LocalTime startTime, LocalTime endTime, int numberOfPeople, Long customerId, Table tableForReservation) {
        TableReservation tableReservation = new TableReservation();

        tableReservation.setTableId(tableForReservation.getId());
        tableReservation.setEndTime(endTime);
        tableReservation.setStartTime(startTime);
        tableReservation.setPeople(numberOfPeople);
        tableReservation.setCustomerId(customerId);
        tableReservation.setDay(day);
        tableReservation.setDuration(ChronoUnit.MINUTES.between(startTime, endTime));

        return tableReservationRepository.save(tableReservation);
    }

    public boolean isTimeBetween(LocalTime localTime, LocalTime localTime1, LocalTime localTime2) {
        return (localTime.equals(localTime1) || localTime.isAfter(localTime1)) &&
                (localTime.equals(localTime2) || localTime.isBefore(localTime2));
    }

    public List<TableReservation> getTableReservationsForCustomer(Long customerId) {
        return tableReservationRepository.findAllByCustomerId(customerId);
    }

    public List<TableReservation> getReservationsForTableOnDay(String tableId, LocalDate day) {
        return tableReservationRepository.findAllByTableIdAndDay(tableId, day);
    }
}
