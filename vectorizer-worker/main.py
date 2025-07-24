from consumer import start_kafka_consumer
from create_topic import create_topic_if_not_exists

if __name__ == "__main__":
    try:
        create_topic_if_not_exists("doc-status-events")
        print("✅ Topic created or already exists. Starting Kafka consumer...")
        start_kafka_consumer()
    except Exception as e:
        print(f"❌ Failed to create topic or start consumer: {e}")