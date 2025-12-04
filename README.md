<!-- 
This README file provides an overview of the "practice-backend" project. 
The project focuses on studying backend development using Kotlin, Spring WebFlux, MongoDB, and GraphQL. 
-->

# practice-backend

A project to study backend development with the following technologies:

- **Kotlin**: A modern, concise, and safe programming language for Spring framework.
- **Spring WebFlux**: A reactive programming framework for building scalable web applications.
- **MongoDB**: A NoSQL database for flexible and scalable data storage.
- **GraphQL**: A query language for APIs to fetch only the data you need.

## Features

- Reactive programming with **Spring WebFlux**.
- Integration with **MongoDB** for data persistence.
- **GraphQL API** for flexible data querying.
- Hands-on learning of backend development best practices.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher.
- MongoDB installed and running locally or accessible remotely. (I use MongoDB on Docker)
- Gradle build tool.
- Developed and tested on Windows 11.

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/kuarnty/practice-backend.git
    ```

2. Navigate to the project directory:

    ```bash
    cd practice-backend
    ```

3. Complete the [SSL configuration](#ssl-configuration) as described below.

4. Build the project:

    ```bash
    ./gradlew build
    ```

<a id="ssl-configuration"></a>

#### SSL(HTTPS) Configuration

- `keystore.p12` file and `self-signed certificate`(only for development purpose) are required for HTTPS communication.
- Configurations are in `src/main/resources/application.properties`.
- `keystore.p12` file is required for SSL configuration.
    - Set environment variable `KEYSTORE_PASSWORD` with the password for the keystore.
    - Generate keystore.p12 file using **keytool** and place it in src/main/resources/ directory.
    - Generate self-signed certificate for development purposes.
    - When you access to the application, trust the self-signed certificate in your browser to avoid security warnings.

### Running the Application

- MongoDB connection is required.

Start the application using the following command:

```bash
./gradlew bootRun
```

- The application is accessible at `https://localhost:8443`.
    - Redirected to a test page for GraphQL communications.
- GraphQL endpoint is available at `https://localhost:8443/graphql`.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- Inspired by the need to learn basic architecture of backend development.
- Thanks to the open-source community for providing excellent tools and frameworks.
- Special thanks to Kotlin, Spring, MongoDB, and GraphQL teams for their amazing work.
