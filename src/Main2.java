/*
This Java code is an application that manages meeting scheduling between employees located in different time zones. Here's an explanation of its functionalities:
Employee Class: Defines an employee with a name and a time zone. It has a GetLocalDateTime() method that returns the current local time of the employee.
Main Method: Executes the program. It creates two employees with their respective time zones and obtains the table of available meeting times between the two employees. Then, it prints the available meeting times.
GetMeetingAvailableHoursBetweenTwoEmployee() Method: Computes the available meeting times between two employees. It uses their time zones and current local times to determine the time intervals when both employees are available for meetings.
The code also utilizes helper methods for time management and conversion between time zones.
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

public class Main2 {

    record Employee(String name, ZoneId zoneId) {

        Employee(String name, ZoneId zoneId) {
            this.name = name;
            this.zoneId = zoneId;
        }

        LocalDateTime GetLocalDateTime() {
            return LocalDateTime.now(zoneId);
        }

        @Override
        public String toString() {
            LocalDateTime localDateTime = GetLocalDateTime();

            /*DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.US)*/

            return "%s [%s] : %s, %s %s, %d, %s ".formatted(
                    name,
                    zoneId.getId(),
                    localDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US),
                    localDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
                    localDateTime.getDayOfMonth(),
                    localDateTime.getYear(),
                    localDateTime.format(DateTimeFormatter.ofPattern("h:mm a").withLocale(Locale.US))
            );
        }
    }

    public static void main(String[] args) {


        Employee employee_A = new Employee("Matteo", ZoneId.of("ECT", ZoneId.SHORT_IDS));
        Employee employee_B = new Employee("Andrea", ZoneId.of("Asia/Tokyo"));

        Map<ZoneId, List<List<LocalDateTime>>> timeTableMap;
        //LocalDateTime ldtEmployeeAU = NextDay(employeeAU);
    if(employee_B.GetLocalDateTime().isBefore(employee_B.GetLocalDateTime())) {
        timeTableMap = GetMeetingAvailableHoursBetweenTwoEmployee(
                employee_B.zoneId(), employee_B.GetLocalDateTime(),
                employee_A.zoneId(), employee_A.GetLocalDateTime(),
                LocalTime.of(7, 00),
                LocalTime.of(21, 00)
        );
    }
    else{
        timeTableMap = GetMeetingAvailableHoursBetweenTwoEmployee(
                employee_A.zoneId(), employee_A.GetLocalDateTime(),
                employee_B.zoneId(), employee_B.GetLocalDateTime(),
                LocalTime.of(7, 00),
                LocalTime.of(21, 00)

        );
    }


        for (List<LocalDateTime> localDateTimes_A : timeTableMap.get(employee_A.zoneId)) {

            List<LocalDateTime> localDateTimes_B = timeTableMap.get(employee_B.zoneId)
                    .get(timeTableMap.get(employee_A.zoneId).indexOf(localDateTimes_A));

            for (int i = 0; i < localDateTimes_B.size(); i++) {

                LocalDateTime ldt_B = localDateTimes_B.get(i);
                LocalDateTime ldt_A = localDateTimes_A.get(i);

                var us = "%s [%s] : %s, %s %s, %d, %s ".formatted(
                        employee_B.name,
                        employee_B.zoneId.getId(),
                        ldt_B.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US),
                        ldt_B.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
                        ldt_B.getDayOfMonth(),
                        ldt_B.getYear(),
                        ldt_B.format(DateTimeFormatter.ofPattern("h:mm a").withLocale(Locale.US))
                );

                var au = "%s [%s] : %s, %s %s, %d, %s ".formatted(
                        employee_A.name,
                        employee_A.zoneId.getId(),
                        ldt_A.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US),
                        ldt_A.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
                        ldt_A.getDayOfMonth(),
                        ldt_A.getYear(),
                        ldt_A.format(DateTimeFormatter.ofPattern("h:mm a").withLocale(Locale.US))
                );
                System.out.println(au + " <---> " + us);

            }
        }
    }

    private static ZonedDateTime SincronizzaOrarioAconOrarioB(ZoneId zoneID_synchronizeWith, LocalDateTime ldtA, ZoneId zoneID_toSynchronize) {
        ZonedDateTime zdt_timeToSyncWith = ZonedDateTime.of(ldtA, zoneID_synchronizeWith);
        ZonedDateTime zdt_timeWeNeedToSync = zdt_timeToSyncWith.withZoneSameInstant(zoneID_toSynchronize);
        return zdt_timeWeNeedToSync;

        // var t = SincronizzaOrariAconOrarioB(employeeAU.zoneId(),ldtEmployeeAU, employeeUS.zoneId());
    }

    private static LocalDateTime ZoneDateTimeToLocalDateTime(ZonedDateTime zdt) {
        LocalDateTime localDateTime = LocalDateTime.of(zdt.toLocalDate(), zdt.toLocalTime());
        return localDateTime;
    }

    private static LocalDateTime NextDay(Employee employee) {
        return employee.GetLocalDateTime().plusDays(1);
    }

    private static Map<ZoneId, List<List<LocalDateTime>>> GetMeetingAvailableHoursBetweenTwoEmployee(ZoneId beforeZoneID, LocalDateTime beforeLDT, ZoneId afterZoneId, LocalDateTime afterLDT, LocalTime timeFrom,LocalTime timeTo) {



        Map<ZoneId, List<List<LocalDateTime>>> employeeListMap = new HashMap<>();
        List<List<LocalDateTime>> listOfDaysWithAvailableHoursEmployeeBefore = new ArrayList<>();
        List<List<LocalDateTime>> listOfDaysWithAvailableHoursEmployeeAfter = new ArrayList<>();


        while (
                !((beforeLDT.toLocalTime().isAfter(timeFrom) && beforeLDT.toLocalTime().isBefore(timeTo))
                        &&
                        (afterLDT.toLocalTime().isAfter(timeFrom) && afterLDT.toLocalTime().isBefore(timeTo)))
        ) {


            beforeLDT = beforeLDT.plusHours(1);

            ZonedDateTime employeeAfterZoneDateTime = SincronizzaOrarioAconOrarioB(beforeZoneID, beforeLDT, afterZoneId);
            afterLDT = ZoneDateTimeToLocalDateTime(employeeAfterZoneDateTime); // Use a new variable
        }

        var beforeLDTcopy = beforeLDT;

        beforeLDTcopy.toLocalDate().plusDays(1)
                .datesUntil(beforeLDT.toLocalDate().plusDays(10), Period.ofDays(1))
                .forEach(localDate -> {

                    List<LocalDateTime> sameDayAvailableHoursEmployeeBefore;
                    List<LocalDateTime> sameDayAvailableHoursEmployeeAfter;

                    boolean isNotWeekEnd =
                            !localDate.getDayOfWeek().toString().equalsIgnoreCase("saturday") &&
                                    !localDate.getDayOfWeek().toString().equalsIgnoreCase("sunday");

                    if (isNotWeekEnd) {

                        LocalDateTime currentLdtEmployeeBefore = LocalDateTime.of(localDate, beforeLDTcopy.toLocalTime()); // Use a new variable
                        ZonedDateTime employeeAfterZoneDateTime = SincronizzaOrarioAconOrarioB(beforeZoneID, currentLdtEmployeeBefore, afterZoneId);
                        LocalDateTime currentLdtEmployeeAfter = ZoneDateTimeToLocalDateTime(employeeAfterZoneDateTime); // Use a new variable


                        if (currentLdtEmployeeAfter.toLocalTime().isBefore(LocalTime.of(21, 0)) &&
                                !currentLdtEmployeeAfter.toLocalDate().getDayOfWeek().toString().equalsIgnoreCase("saturday") &&
                                !currentLdtEmployeeAfter.getDayOfWeek().toString().equalsIgnoreCase("sunday")) {

                            LocalDateTime localDateTimeStart = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), beforeLDTcopy.toLocalTime().getHour(), 0);
                            long until = localDateTimeStart.until(LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 21, 0), ChronoUnit.HOURS);

                            sameDayAvailableHoursEmployeeBefore = Stream.iterate(localDateTimeStart, time -> time.plusHours(1))
                                    .limit(until)
                                    .toList();

                            listOfDaysWithAvailableHoursEmployeeBefore.add(sameDayAvailableHoursEmployeeBefore);

                            sameDayAvailableHoursEmployeeAfter = sameDayAvailableHoursEmployeeBefore.stream().
                                    map(localDateTimeEmployeeBefore -> {
                                        var employeeAfterZDT = SincronizzaOrarioAconOrarioB(beforeZoneID, localDateTimeEmployeeBefore, afterZoneId);
                                        var currentLocalDateTimeEmployeeAfter = ZoneDateTimeToLocalDateTime(employeeAfterZDT); // Use a new variable
                                        return currentLocalDateTimeEmployeeAfter;
                                    })
                                    .toList();
                            listOfDaysWithAvailableHoursEmployeeAfter.add(sameDayAvailableHoursEmployeeAfter);
                        }
                    }
                });

        employeeListMap.put(beforeZoneID, listOfDaysWithAvailableHoursEmployeeBefore);
        employeeListMap.put(afterZoneId, listOfDaysWithAvailableHoursEmployeeAfter);

        return employeeListMap;
    }

}

/*---------------------------------------------------------------------------------------------------*//*
                ::Per sapere che ore sono in Italia ad una certa ora in Australia::

        // Ora corrente in Australia
        LocalDateTime dateTimeInAustralia = LocalDateTime.now();

        // Fuso orario in Australia (Australia/Sydney)
        ZoneId fusoOrarioAustralia = ZoneId.of("Australia/Sydney");

        // Converti LocalDateTime ---> ZonedDateTime
        Questa conversione aggiunge alle informazioni gia presenti nella classe LocalDateTime anche il fuso orario

        ZonedDateTime zonedDateTimeAustralia = ZonedDateTime.of(dateTimeInAustralia, fusoOrarioAustralia);

        // Fuso orario del posto di cui vogliamo sapere l orario rispetto ad un altro paese ( In questo caso l'Italia )
        ZoneId fusoOrarioItalia = ZoneId.of("Europe/Rome");

        // Troviamo l ora italiana corrispondente ad una certa ora in Australia
        ZonedDateTime zonedDateTimeItalia = zonedDateTimeAustralia.withZoneSameInstant(fusoOrarioItalia);

*/


