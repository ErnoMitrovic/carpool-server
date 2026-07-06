<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<br />
<div align="center">
  <a href="https://github.com/ErnoMitrovic/carpool-server">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Shared Uni (Carpool Server)</h3>

  <p align="center">
    Spring Boot backend for ride sharing, booking, chat, and live location updates.
    <br />
    <a href="https://github.com/ErnoMitrovic/carpool-server"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/ErnoMitrovic/carpool-server">View Demo</a>
    ·
    <a href="https://github.com/ErnoMitrovic/carpool-server/issues">Report Bug</a>
    ·
    <a href="https://github.com/ErnoMitrovic/carpool-server/issues">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About The Project

[![Product Name Screen Shot][product-screenshot]](https://example.com)

Carpool Server is a backend API for a carpool platform. It provides endpoints for authentication, ride management, booking flow, and real-time communication.

Current implemented modules include:

- Auth and role-based access
- Rides (create, search, update, cancel)
- Bookings (create, list, status updates, cancel)
- Chat history and WebSocket messaging
- Real-time location sharing over WebSocket

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java-shield]][Java-url]
* [![Spring Boot][SpringBoot-shield]][SpringBoot-url]
* [![Maven][Maven-shield]][Maven-url]
* [![PostgreSQL][PostgreSQL-shield]][PostgreSQL-url]
* [![PostGIS][PostGIS-shield]][PostGIS-url]
* [![Redis][Redis-shield]][Redis-url]
* [![Swagger][Swagger-shield]][Swagger-url]
* [![Firebase][Firebase-shield]][Firebase-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

To run the project locally, set up the required services and environment variables, then start the Spring Boot application.

### Prerequisites

- JDK 23 (recommended, matches compiler settings)
- Docker (optional, but recommended for PostgreSQL/PostGIS and Redis)
- Maven (or use Maven Wrapper included in this repository)

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/ErnoMitrovic/carpool-server.git
   cd repo_name
   ```
2. Start PostgreSQL with PostGIS and enable the extension in your database
   ```sql
   CREATE EXTENSION IF NOT EXISTS postgis;
   ```
3. Start Redis on port `6379`
4. Set required environment variables (`SQL_HOST`, `SQL_DB`, `SQL_USER`, `SQL_PASSWORD`, `FIREBASE_PROJECT_ID`)
5. Run the application
   ```sh
   ./mvnw spring-boot:run
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

Default local URL:

- `http://localhost:8080`

Main API groups:

- `/api/{version}/auth/*`
- `/api/{version}/ride/*`
- `/api/{version}/ride/{rideId}/booking/*`
- `/api/{version}/user/{userId}/booking/*`
- `/chat/history`

WebSocket endpoints:

- `/chat`
- `/location`

Swagger documentation:

- `/swagger-ui/index.html`
- `/v3/api-docs`

_Add request/response examples here._

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [ ] Add complete API examples for all major endpoints
- [ ] Add Docker Compose setup for local dependencies
- [ ] Add deployment and environment profile documentation
- [ ] Add architecture and data model diagrams

See the [open issues](https://github.com/ErnoMitrovic/carpool-server/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributing

Contributions are welcome and appreciated.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - email@email_client.com

Project Link: [https://github.com/ErnoMitrovic/carpool-server](https://github.com/ErnoMitrovic/repo_name)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Acknowledgments

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Springdoc OpenAPI](https://springdoc.org/)
* [PostGIS](https://postgis.net/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/ErnoMitrovic/carpool-server.svg?style=for-the-badge
[contributors-url]: https://github.com/ErnoMitrovic/carpool-server/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/ErnoMitrovic/carpool-server.svg?style=for-the-badge
[forks-url]: https://github.com/ErnoMitrovic/carpool-server/network/members
[stars-shield]: https://img.shields.io/github/stars/ErnoMitrovic/carpool-server.svg?style=for-the-badge
[stars-url]: https://github.com/ErnoMitrovic/carpool-server/stargazers
[issues-shield]: https://img.shields.io/github/issues/ErnoMitrovic/carpool-server.svg?style=for-the-badge
[issues-url]: https://github.com/ErnoMitrovic/carpool-server/issues
[license-shield]: https://img.shields.io/github/license/ErnoMitrovic/carpool-server.svg?style=for-the-badge
[license-url]: https://github.com/ErnoMitrovic/carpool-server/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/ernomitrovic
[product-screenshot]: images/screenshot.png

[Java-shield]: https://img.shields.io/badge/Java-23-007396?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[SpringBoot-shield]: https://img.shields.io/badge/Spring%20Boot-3.3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[Maven-shield]: https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/
[PostgreSQL-shield]: https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[PostGIS-shield]: https://img.shields.io/badge/PostGIS-Spatial-336791?style=for-the-badge&logo=postgresql&logoColor=white
[PostGIS-url]: https://postgis.net/
[Redis-shield]: https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white
[Redis-url]: https://redis.io/
[Swagger-shield]: https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black
[Swagger-url]: https://swagger.io/
[Firebase-shield]: https://img.shields.io/badge/Firebase-Auth-FFCA28?style=for-the-badge&logo=firebase&logoColor=black
[Firebase-url]: https://firebase.google.com/
