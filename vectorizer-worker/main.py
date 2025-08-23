from fastapi import FastAPI
from contextlib import asynccontextmanager
import threading
from consumer import start_kafka_consumer
from create_topic import create_topic_if_not_exists

# Background Kafka thread reference
consumer_thread = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    global consumer_thread
    try:
        create_topic_if_not_exists("doc-status-events")
        print("✅ Topic created or already exists. Starting Kafka consumer...")

        # Run consumer in background thread
        consumer_thread = threading.Thread(target=start_kafka_consumer, daemon=True)
        consumer_thread.start()

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
    return {"status": "ok", "message": "Vector indexing microservice is running"}


@app.get("/status")
def service_status():
    running = consumer_thread.is_alive() if consumer_thread else False
    return {"kafka_consumer_running": running}
