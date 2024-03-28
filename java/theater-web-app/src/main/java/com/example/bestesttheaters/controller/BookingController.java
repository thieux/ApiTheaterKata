/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bestesttheaters.controller;

import com.example.bestesttheaters.data.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
class BookingController {

	private final InMemoryRepository showRepository;

    public BookingController(InMemoryRepository showRepository) {
		this.showRepository = showRepository;
    }

	@ModelAttribute("show")
	public Show loadShow(@PathVariable("showId") int showId) {
        return getShowById(showId).get();
	}

	private Optional<Show> getShowById(int showId) {
		return showRepository.findAll()
			.stream()
			.filter(show1 -> show1.getId() == showId)
			.findFirst();
	}

	@GetMapping("/shows/{showId}/booking/new")
	public String initNewVisitForm() {
		return "shows/createBookingForm";
	}

	@PostMapping("/shows/{showId}/booking/new")
	public String processNewBookingForm(@ModelAttribute("show") Show show,
										int numberOfTickets,
										RedirectAttributes redirectAttributes) {
		BookingRequestDto bookingRequest = new BookingRequestDto(show.getId(), numberOfTickets);
		ShowIndex showIndex = new ShowIndex(showRepository.findAll());

		Show show1 = showIndex.getShow(bookingRequest.showId());
		if (show1 == null) {
			throw new IllegalArgumentException(String.format("Unknown show ID: %d", bookingRequest.showId()));
		}
		BookingStatus booked = BookingStatus.BOOKED;
		assert show1 != null;
		if (bookingRequest.numberOfTickets() > show1.getCapacity()) {
			booked = BookingStatus.CANCELLED;
		}
		int newBookingId = showRepository.findAllBookings().size() + 1;
		showRepository.saveBooking(Booking.createBooking(newBookingId, show1, bookingRequest.numberOfTickets(), booked));
		BookingDto bookingDto = new BookingDto(newBookingId, bookingRequest.showId(), bookingRequest.numberOfTickets(), booked);

		if (bookingDto.status() == BookingStatus.BOOKED) {
			redirectAttributes.addFlashAttribute("message", "Your show has been booking successfully");
			return "redirect:/bookings.html";
		} else {
			redirectAttributes.addFlashAttribute("message", "Sorry, your show cannot be booked");
			return "redirect:/bookings.html";
		}

	}

}
