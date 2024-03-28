package com.example.bestesttheaters.controller;

import com.example.bestesttheaters.data.BookingStatus;

public record BookingDto(int bookingId, int showId, int numberOfTickets, BookingStatus status) {
}
