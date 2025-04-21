# MiFinca - Property Rental Platform Backend

## Overview

MiFinca is the backend system for a web application designed to connect property owners (Landlords) with users seeking short-term property rentals (like vacation homes or "fincas"). It provides the core logic and API endpoints necessary for managing users, properties, bookings, payments, and ratings.

## What it Does

The application facilitates the entire rental process:

1.  **User Management:** Handles user registration (with email confirmation), secure login, and differentiates between regular Users and Landlords with distinct permissions.
2.  **Property Listings:** Allows Landlords to create detailed property listings, including descriptions, amenities (pool, BBQ), pet policies, number of rooms/bathrooms, pricing per night, and upload property photos.
3.  **Search & Discovery:** Enables Users to search and filter available properties based on criteria like location (using Colombian departments fetched from an external API), price range, and capacity.
4.  **Booking System:** Users can request to book a property for specific dates. The system checks for availability conflicts.
5.  **Request Management:** Landlords can view incoming booking requests and choose to either approve or deny them.
6.  **Payment Flow:** Once a request is approved, the system guides the User through a payment step (details like bank and account number are captured).
7.  **Scheduling & Status:** Tracks the status of each rental (Requested, Approved, Paid, In Progress, Completed, Denied, etc.).
8.  **Rating System:** After a stay is completed, both the User and the Landlord can rate their experience and leave comments.

## How it Works (Technology Highlights)

*   **Backend:** Built with **Java** using the **Spring Boot** framework, providing a robust structure for RESTful API development.
*   **Authentication:** Secures endpoints using **JSON Web Tokens (JWT)**, managing user sessions with access and refresh tokens.
*   **Database Interaction:** Uses **Spring Data JPA** to interact with a relational database, managing entities like Accounts, Properties, Schedules (Bookings), Ratings, etc.
*   **Security:** Passwords are encrypted before storage (using AES encryption).
*   **File Handling:** Manages property image uploads, storing them on the server's filesystem and providing access URLs.
*   **Email Notifications:** Integrates with **SendGrid** to send transactional emails, such as account confirmation links.
*   **External Services:** Connects to an external API (`api-colombia.com`) to fetch geographical data (Departments).

This backend provides a comprehensive foundation for a full-stack property rental application.
