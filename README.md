# Sensor Collector

## Description

Sensor Collector is a microservice developed in Java with Spring Boot that receives sensor data through an HTTP API.
This data is processed and sent to a Kafka messaging system for further processing by other microservices, such as
sensor-monitor.

## Technologies Used

- Java 11 (or higher)
- Spring Boot
- Apache Kafka (for communication with other microservices)
- Log4j 2 (for logging)
- Maven (for dependency management)

## Architecture

The sensor-collector microservice acts as a receiver for sensor data. When a client sends an HTTP POST request with the 
sensor data, the service validates the data and sends it to a Kafka topic for processing by other microservices. 
Additionally, it can receive data via the MQTT protocol, enabling more efficient communication with IoT devices.

The basic flow is:

1. Client sends sensor data via POST to the API or through MQTT.
2. Sensor Collector receives and validates the data.
3. For HTTP POST requests and MQTT, Kafka is used to send the data to the corresponding topic.
4. Other microservices, like sensor-monitor, consume that data from Kafka for analysis or further processing.

### Prerequisites

Before running the project, make sure you have the following installed:

- Java 11 (or higher)
- Apache Kafka installed and configured locally or on an accessible server
- Zookeeper (if running Kafka locally)
- Mosquitto (for MQTT communication)
- Maven

## Installation and configuration

1. Clone the repository.
   2. Configure Kafka and Mqtt: Open the src/main/resources/application.yml file and configure the Kafka connection details:

       ```yaml
        spring:
          kafka:
            bootstrap-servers: localhost:9092
            producer:
              key-serializer: org.apache.kafka.common.serialization.StringSerializer
              value-serializer: org.apache.kafka.common.serialization.StringSerializer
          mqtt:
            host: 127.0.0.1
            port: 1883
            username: admin
            password: admin
            generalTopic: sensor/#
       ```

3. Build the project: Run the following command in the project root directory:

    ```shell
    mvn clean install
    ```

4. Run the project: Run the following command in the project root directory:

    ```shell
    mvn spring-boot:run
    ```

## Usage

To run the project, follow these steps:

1. Start Zookeeper (if running Kafka locally).
2. Start Kafka.
3. Start Mosquitto (for MQTT).
3. Start the sensor-collector microservice.
4. Send sensor data to the microservice via HTTP POST or MQTT.

## API

## Endpoint: Receive Sensor Data

- URL: /api/sensor
- Method: POST
- Description: Receives data from a sensor and sends it to Kafka.

### Request

- Body example:
    ```json
    {
        "sensorId": "sensor-1",
        "sensorType": "temperature",
        "sensorValue": 25.5,
        "sensorUnit": "C",
        "timestamp": "2021-08-01T12:00:00Z"
    }
    ```

### Response

- Response example:
    ```json
    {
        "message": "Data received successfully.",
        "data": {
          "sensorId": "123",
          "temperature": 70.5,
          "timestamp": "2023-09-21T10:15:30Z"
        } 
    }
    ```

## Scripts

With the following script, you can send sensor data to the `sensor-collector` microservice via HTTP POST. Make sure 
the microservice is running before executing the script, and ensure that the requests library is installed.

```python
import requests
import random
import time
from datetime import datetime

def generate_sensor_data():
    sensor_id = f"sensor-{random.randint(1000,5000)}"
    sensor_type = random.choice(["temperature", "humidity", "preassure"])
    sensor_value = round(random.uniform(1, 100), 2)
    sensor_unit = random.choice(["°C", "%", "hPa"])
    sensor_timestamp = datetime.now().isoformat()

    return {
            "sensorId": sensor_id,
            "sensorType": sensor_type,
            "sensorValue": sensor_value,
            "sensorUnit": sensor_unit,
            "sensorTimestamp": sensor_timestamp
     }


def main():
    url = "http://localhost:8080/sensor"


    while True:
        sensor_data = generate_sensor_data()

        try:
            response = requests.post(url, json=sensor_data)
            print(f"Message sent: {sensor_data}")
            print(f"Status code: {response.status_code}")
        except :
            print("Error sendig sensor data")

        time.sleep(3)

if __name__ == "__main__":
    main()
```

With the following script, you can send sensor data to the `sensor-collector` microservice via MQTT. Make sure
the microservice is running before executing the script, and ensure that the requests library is installed.

```python
import json
import paho.mqtt.client as mqtt
import random
import time
from datetime import datetime

def generate_sensor_data():
    sensor_id = f"sensor-{random.randint(1000,5000)}"
    sensor_type = random.choice(["temperature", "humidity", "preassure"])
    sensor_value = round(random.uniform(1, 100), 2)
    sensor_unit = random.choice(["°C", "%", "hPa"])
    sensor_timestamp = datetime.now().isoformat()

    return {
            "sensorId": sensor_id,
            "sensorType": sensor_type,
            "sensorValue": sensor_value,
            "sensorUnit": sensor_unit,
            "sensorTimestamp": sensor_timestamp
     }

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker!")
    else:
        print(f"Failed to connect, return code {rc}")

def main():
    broker = "127.0.0.1"
    port = 1883
    username = "admin"
    password = "admin"
    

    client = mqtt.Client()
    client.username_pw_set(username, password)
    client.on_connect = on_connect

    try:
        client.connect(broker, port)
    except:
        print("Error connecting to MQTT Broker")
        return

    while True:
        topic = f"sensor/{random.randint(0,20)}"
        sensor_data = generate_sensor_data()
        sensor_json = json.dumps(sensor_data)

        try:
            client.publish(topic, sensor_json)
            print(f"Message sent: {sensor_data}")
        except:
            print("Error sending sensor data")

        time.sleep(3)

if __name__ == "__main__":
    main()
```