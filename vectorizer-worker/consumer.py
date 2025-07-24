import json
from kafka import KafkaConsumer
from config import KAFKA_BOOTSTRAP_SERVERS, KAFKA_TOPIC, GROUP_ID
from embedding_utils import process_and_embed_doc
from pathlib import Path
import struct

def start_kafka_consumer():
    consumer = KafkaConsumer(
        KAFKA_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id=GROUP_ID,
        key_deserializer=lambda k: struct.unpack(">q", k)[0] if k else None,  # For Long key
        value_deserializer=lambda v: json.loads(v.decode('utf-8')),   
        auto_offset_reset="latest",
        enable_auto_commit=True,
    )

    print("Listening for Kafka events...")

    for message in consumer:
        # print("Message received:", message)
        event = message.value
        print("Event:", event)
        try:
            doc_id = event["id"]  # ✅ Corrected field name
            file_path = event["storagePath"].replace("\\", "/")
            user_id = event["userId"]
            file_name = event["fileName"]

            # print(f"[+] Received document {file_name} for vectorization.")
            print(f"Doc ID: {doc_id}, File Path: {file_path}, File Name: {file_name}, User ID: {user_id}")
            process_and_embed_doc(doc_id, file_path, file_name, user_id)

        except Exception as e:
            print(f"[!] Error processing message: {e}")
