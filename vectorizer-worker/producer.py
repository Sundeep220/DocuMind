from kafka import KafkaProducer
import json
from config import KAFKA_BOOTSTRAP_SERVERS, PRODUCER_TOPIC

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
    key_serializer=lambda k: str(k).encode('utf-8'),
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
)

def send_status_update(document_id, status):
    event = {
        "documentId": document_id,
        "status": status
    }
    producer.send(PRODUCER_TOPIC, key=document_id, value=event)
    producer.flush()
