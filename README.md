# DocuMind
Intelligent Project Knowledge Base with GenAI Assistant


---

## 🏗️ 1. **High-Level Architecture**

```
                    +--------------------+
                    |     Frontend UI    |
                    | (React / Streamlit)|
                    +---------+----------+
                              |
                              ▼
                      [API Gateway / Gateway Service]
                              |
            +----------------+------------------+----------------+
            |                |                  |                |
            ▼                ▼                  ▼                ▼
   [Document Service]  [User Service]   [Chat Service]   [Vector Store Service]
                              |                                ▲
                              ▼                                |
                    [Kafka Broker (Doc Upload Events)]         |
                              |                                |
                       [Vectorizer Worker] ---------------------
                              |
                       [LangChain + Embedding Model]
                              |
                          [Qdrant / FAISS]
```

---

## 🧱 2. **Microservices List**

| Service Name           | Description                                       |
| ---------------------- | ------------------------------------------------- |
| `user-service`         | Handles user auth, JWT, roles                     |
| `document-service`     | Uploads/validates docs (PDF, DOCX, Notion export) |
| `chat-service`         | Receives queries and returns answers via LLM      |
| `vector-store-service` | Interacts with LangChain + FAISS/Qdrant DB        |
| `vectorizer-worker`    | Background Kafka consumer to extract + embed docs |
| `gateway-service`      | Routes all API traffic to correct services        |

---

## ⚙️ 3. **Tools & Tech Stack**

| Category           | Tech Stack                                    |
| ------------------ | --------------------------------------------- |
| **Language**       | Java (Spring Boot), Python (LangChain worker) |
| **Message Broker** | Kafka (Apache Kafka + Spring Kafka)           |
| **LLMs**           | OpenAI / Ollama / Groq / Gemini via LangChain |
| **Embeddings**     | HuggingFace Transformers / OpenAI embeddings  |
| **Vector DB**      | FAISS (in-memory) or Qdrant (persistent)      |
| **Storage**        | MinIO / AWS S3 / Local FS                     |
| **Database**       | PostgreSQL (for metadata, users)              |
| **Security**       | JWT-based Auth, Spring Security               |
| **Infra/Deploy**   | Docker, Docker Compose, Kubernetes (optional) |
| **Frontend**       | React.js or Streamlit (optional)              |

---

## 🧩 4. **Service-by-Service Design**

---

### ✅ `User Service`

Handles authentication/authorization.

* **Endpoints**:

    * `POST /register`
    * `POST /login`
    * `GET /me`
* **Tech**: Spring Security + JWT

---

### ✅ `Document Service`

Uploads and validates documents, emits Kafka events.

* **Endpoints**:

    * `POST /documents/upload` (multipart file upload)
    * `GET /documents/{id}`
* **Kafka Event**:

  ```json
  {
    "docId": "123",
    "filePath": "path/on/disk/or/s3",
    "uploader": "userId"
  }
  ```
* **Responsibilities**:

    * Save metadata to DB
    * Store file
    * Produce Kafka event

---

### ✅ `Vectorizer Worker` (Kafka Consumer)

* Listens to Kafka topic `doc-upload`
* Loads document → extracts content → converts to chunks
* Generates vector embeddings using LangChain
* Stores in **Qdrant / FAISS**
* Updates DB with vector status

---

### ✅ `Vector Store Service`

Abstraction layer to talk to the vector DB (like Qdrant)

* **Endpoints** (internal):

    * `POST /vectors/upsert`
    * `POST /vectors/query`

---

### ✅ `Chat Service`

Takes user queries and runs LangChain-style QA pipeline.

* **Endpoints**:

    * `POST /chat/query`

      ```json
      {
        "query": "What are the business goals for Q2?",
        "docId": "123"
      }
      ```
* **Pipeline**:

    * Load document chunks from Qdrant
    * Run retrieval + QA chain
    * Return answer + source snippets

---

### ✅ `Gateway Service`

* Spring Cloud Gateway
* JWT filtering
* Routes traffic to correct services

---

## 🧮 5. **Core Class Diagrams (UML Sketch)**

### Document Upload Flow (Spring Boot classes)

```
DocumentController
    |
    └── DocumentService
            └── KafkaProducer
            └── FileStorageService
            └── DocumentRepository (JPA)
```

### Vectorizer Worker (Python LangChain-based service)

```
KafkaConsumer
    |
    └── DocumentLoader (PDF, DOCX, etc.)
            └── TextSplitter
            └── Embedder (OpenAI, HF)
            └── VectorStoreClient (Qdrant/FAISS)
```

### Chat Service

```
ChatController
    |
    └── ChatService
            └── VectorStoreClient
            └── LLMClient (LangChain QA chain)
```

---

## 🔁 6. **System Interaction: End-to-End Flow**

1. **User uploads a document** to `/documents/upload`
2. `document-service` stores the file and metadata → produces Kafka event
3. `vectorizer-worker` consumes event:

    * Extracts text → splits into chunks → embeds → stores in vector DB
4. User asks question via `/chat/query`
5. `chat-service` fetches relevant chunks → sends to LLM → returns answer

---

## 📬 7. **Kafka Topics**

| Topic Name         | Description                  |
| ------------------ | ---------------------------- |
| `doc-upload`       | Events for new documents     |
| `embedding-status` | Optional: doc indexed status |

---

## 📁 8. **Folder Structure (Simplified)**

```
project-knowledge-base/
│
├── gateway-service/
├── user-service/
├── document-service/
├── chat-service/
├── vector-store-service/
├── vectorizer-worker/   ← Python + LangChain
│
├── docker-compose.yml
├── common-lib/ (DTOs, shared utils)
└── README.md
```

---

## 🚀 Next Steps

Shall we start implementing **one service at a time**?

I suggest this order:

1. `user-service` (auth)
2. `document-service` (upload + Kafka)
3. `vectorizer-worker` (LangChain + Qdrant)
4. `chat-service` (QA Chain)
5. `gateway-service`

Let me know and I’ll scaffold the first service step-by-step for you (with code).

## Steps to run the python app
- Go to app folder directory
    `cd <app_name>`
- Create a virtual environment:
    - Using pip:
        `python -m venv <environment_name>`
    - Using uv:
        `uv venv`
- Install all the dependency libraries:
    - Using pip:
        `pip install -r requirements.txt`
    - Using uv:
        - Install all libraries from requirements.txt
            `uv pip install -r requirements.txt`
        - Add the libraries to uv project.toml file to manage them using uv
            `uv add -r ../requirements.txt`
        - Bonus tip:
            - If you want to add another dependency to your project
                `uv add <dependency_name>`
            - If you want to remove a dependency from your project
                `uv remove <dependency_name>`
            - If you want to update a dependency in your project
                `uv update <dependency_name>`
- Run the application:
    - Using pip:
        `uvicorn main:app --reload`
    - Using uv:
        `uv run uvicorn main:app --reload`
