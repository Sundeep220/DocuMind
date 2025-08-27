from fastapi import FastAPI
from contextlib import asynccontextmanager
import threading
from consumer import start_kafka_consumer
from create_topic import create_topic_if_not_exists

# Background Kafka thread reference
consumer_thread = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Async context manager to manage the lifecycle of the Kafka consumer.

    This method is called by FastAPI once the app is fully started. It creates
    the "doc-status-events" topic if it doesn't exist and then starts the
    Kafka consumer in a background thread. The consumer will run until the
    application is shut down.

    If an exception occurs during the creation of the topic or the start of
    the consumer, it is logged and the context manager yields to allow the
    app to continue running.

    When the app is shut down, the context manager will wait for the consumer
    thread to finish before exiting.
    """
    global consumer_thread
    try:
        create_topic_if_not_exists("doc-status-events")
        print("✅ Topic created or already exists. Starting Kafka consumer...")

        # Run consumer in background thread
        consumer_thread = threading.Thread(target=start_kafka_consumer, daemon=True)
        consumer_thread.start()
        print("✅ Kafka consumer started.")

        yield   # 👈 this runs while the app is alive

    except Exception as e:
        print(f"❌ Failed to create topic or start consumer: {e}")
        yield

    finally:
        print("🛑 Shutting down FastAPI service (Kafka consumer will stop).")


app = FastAPI(
    title="Vector Indexing Microservice",
    lifespan=lifespan
)


@app.get("/health")
def health_check():
    """
    A simple health check that returns a JSON object with a "status" key and
    a value of "ok" if the service is running, and a human-readable message.

    This endpoint is useful for load balancers and monitoring tools to check
    if the service is alive.

    Returns:
        dict: A JSON object with a "status" key and a "message" key.
    """
    return {"status": "ok", "message": "Vector indexing microservice is running"}


@app.get("/status")
def service_status():
    
    """
    Returns a JSON object with one key-value pair, 
    where the key is "kafka_consumer_running" and the value is a boolean 
    indicating whether the Kafka consumer thread is currently running.

    This endpoint is useful for monitoring the service.
    """
    running = consumer_thread.is_alive() if consumer_thread else False
    return {"kafka_consumer_running": running}

