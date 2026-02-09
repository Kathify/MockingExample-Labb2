package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test för BookingSystem
 * Alla externa beroenden mockas
 */
@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingSystem bookingSystem;

    private final LocalDateTime now = LocalDateTime.of(2026, 1, 20, 10, 0);

    @Nested
    class BookingSystemFlowTests {

        @BeforeEach
        void setUp() {
            when(timeProvider.getCurrentTime()).thenReturn(now);
        }

        // bookRoom

        @Test
        void book_room_with_valid_input_should_succeed() throws NotificationException {
            String roomId = "room1";
            Room room = new Room(roomId, "Suite");

            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

            boolean result = bookingSystem.bookRoom(
                    roomId, now.plusDays(1), now.plusDays(2));

            assertThat(result).isTrue();
            verify(roomRepository).save(room);
            verify(notificationService).sendBookingConfirmation(any());
        }

        @Test
        void booking_should_succeed_even_if_notification_fails() throws NotificationException {
            Room room = new Room("Room1", "Suite");

            when(roomRepository.findById("Room1")).thenReturn(Optional.of(room));
            doThrow(new NotificationException("fail"))
                    .when(notificationService).sendBookingConfirmation(any());

            boolean result = bookingSystem.bookRoom(
                    "Room1", now.plusDays(1), now.plusDays(2));

            assertThat(result).isTrue();
            verify(roomRepository).save(room);
        }

        @Test
        void booking_with_end_before_start_should_throw_exception() throws NotificationException {
            assertThatThrownBy(() ->
                    bookingSystem.bookRoom(
                            "room1", now.plusDays(2), now.plusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(roomRepository, never()).save(any());
        }

        @Test
        void booking_in_the_past_should_throw_exception() throws NotificationException {
            assertThatThrownBy(() ->
                    bookingSystem.bookRoom(
                            "room1", now.minusDays(1), now.plusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void booking_non_existing_room_should_throw_exception() throws NotificationException {
            assertThatThrownBy(() ->
                    bookingSystem.bookRoom(
                            "room1", now.plusDays(1), now.plusDays(2)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void booking_occupied_room_should_return_false() throws NotificationException {
            Room room = new Room("Room1", "Suite");
            room.addBooking(new Booking(
                    "B1", "Room1", now.plusDays(1), now.plusDays(2)));

            when(roomRepository.findById("Room1")).thenReturn(Optional.of(room));

            boolean result = bookingSystem.bookRoom(
                    "Room1", now.plusDays(1), now.plusDays(2));

            assertThat(result).isFalse();
            verify(roomRepository, never()).save(any());
        }

        // cancelBooking

        @Test
        void cancel_future_booking_should_succeed() throws NotificationException {
            Booking booking = new Booking(
                    "B1", "Room1", now.plusDays(1), now.plusDays(2));
            Room room = new Room("Room1", "Suite");
            room.addBooking(booking);

            when(roomRepository.findAll()).thenReturn(List.of(room));

            boolean result = bookingSystem.cancelBooking("B1");

            assertThat(result).isTrue();
            verify(roomRepository).save(room);
            verify(notificationService).sendCancellationConfirmation(booking);
        }

        @Test
        void cancel_started_booking_should_throw_exception() throws NotificationException {
            Booking booking = new Booking(
                    "B1", "Room1", now.minusDays(1), now.plusDays(1));
            Room room = new Room("Room1", "Suite");
            room.addBooking(booking);

            when(roomRepository.findAll()).thenReturn(List.of(room));

            assertThatThrownBy(() ->
                    bookingSystem.cancelBooking("B1"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void cancellation_should_succeed_even_if_notification_fails() throws NotificationException {
            Booking booking = new Booking(
                    "B1", "Room1", now.plusDays(1), now.plusDays(2));
            Room room = new Room("Room1", "Suite");
            room.addBooking(booking);

            when(roomRepository.findAll()).thenReturn(List.of(room));
            doThrow(new NotificationException("fail"))
                    .when(notificationService).sendCancellationConfirmation(any());

            boolean result = bookingSystem.cancelBooking("B1");

            assertThat(result).isTrue();
            verify(roomRepository).save(room);
        }
    }

    @Nested
    class BookingSystemValidationTests {

        @ParameterizedTest
        @MethodSource("invalidBookingArguments")
        void booking_with_null_values_should_throw_exception(
                String roomId, LocalDateTime start, LocalDateTime end)
                throws NotificationException {

            assertThatThrownBy(() ->
                    bookingSystem.bookRoom(roomId, start, end))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        static Stream<Arguments> invalidBookingArguments() {
            return Stream.of(
                    Arguments.of(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                    Arguments.of("Room1", null, LocalDateTime.now().plusDays(1)),
                    Arguments.of("Room1", LocalDateTime.now(), null)
            );
        }

        @Test
        void get_available_rooms_should_filter_booked_rooms() {
            Room freeRoom = new Room("room1", "Free");
            Room bookedRoom = new Room("room2", "Booked");
            bookedRoom.addBooking(new Booking(
                    "B1", "room2", now.plusDays(1), now.plusDays(2)));

            when(roomRepository.findAll()).thenReturn(List.of(freeRoom, bookedRoom));

            List<Room> result = bookingSystem.getAvailableRooms(
                    now.plusDays(1), now.plusDays(2));

            assertThat(result).containsExactly(freeRoom);
        }

        @Test
        void cancel_unknown_booking_should_return_false() throws NotificationException {
            when(roomRepository.findAll())
                    .thenReturn(List.of(new Room("Room1", "Suite")));

            boolean result = bookingSystem.cancelBooking("UNKNOWN");

            assertThat(result).isFalse();
        }
    }
}
