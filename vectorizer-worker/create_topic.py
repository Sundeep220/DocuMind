from kafka.admin import KafkaAdminClient, NewTopic
from config import KAFKA_BOOTSTRAP_SERVERS

def create_topic_if_not_exists(topic_name):
    """
    Create a Kafka topic if it does not already exist.
    """
    admin = KafkaAdminClient(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)
    existing_topics = admin.list_topics()
    if topic_name not in existing_topics:
        topic = NewTopic(name=topic_name)
        admin.create_topics([topic])
    admin.close()

