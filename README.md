# SpeedTest Recorder

A Playwright-based Speedtest recorder that triggers a speed test from Ookla based on a configured iteration count, records the results, and writes them to a CSV file.

## Tech Stack

- Java
- Playwright
- Maven

## Features

- Automated Ookla Speedtest execution
- Configurable number of test iterations
- CSV result export
- Screenshot capture per test run
- Captures:
    - ISP
    - IP Address
    - Location
    - Download Speed
    - Upload Speed

## Project Structure

```text
src/main/java/com/engrkirky/speedtestrecorder
├── model
│   └── Result.java
├── pages
│   └── SpeedtestPage.java
└── utils
    ├── BrowserUtils.java
    ├── WriterUtils.java
    └── XPathUtils.java
```

## Configuration

Create a `config.properties` file under:

```text
src/main/resources/config.properties
```

Example:

```properties
url=https://www.speedtest.net/
iterations=3
```

## Running the Application

Install dependencies and run:

```bash
mvn clean install
mvn exec:java
```

## Output

The application generates:

- CSV result files

```text
speedtest-results-<timestamp>.csv
```

- Screenshot snapshots

```text
speedtest-<timestamp>.png
```

## Sample CSV Output

```csv
timestamp	isp	ip	location	download_speed	upload_speed	idle_latency	download_latency	upload_latency
2026-05-25T06:03:44.255704Z	Spectrum	35.144.158.14	Kingsport, TN	1036.72	39.36	27	34	26
```

## Author

- [Engr. Kirk Alyn Santos](https://github.com/kirkalyn13)