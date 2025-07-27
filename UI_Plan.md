## ✅ Phase 1: High-Level Frontend Plan

### 🔐 Authentication (via JWT)

* Login Page
* Store JWT in `localStorage`
* Attach JWT to every chat request
* Protect Chat Page with a `PrivateRoute`

### 💬 Chat Page

* Chat window with scrolling history
* Message bubbles (user + AI assistant)
* Streaming responses (typing effect)
* Upload documents (multi-select)
* Folder switcher (to change document context)
* Dark/light mode toggle (optional)

### 📂 My Documents Page

* Show uploaded files
* Status (Processing ✅ / Pending ⏳ / Failed ❌)
* Option to delete or re-upload

---

## 🧱 Tech Stack

| Feature            | Choice                                         |
| ------------------ | ---------------------------------------------- |
| Frontend Framework | React                                          |
| State Management   | `useState`, `useEffect`, context (lightweight) |
| UI Framework       | TailwindCSS + Headless UI                      |
| File Uploads       | Axios or Fetch with `FormData`                 |
| HTTP Client        | Axios                                          |
| Routing            | React Router DOM                               |
| Token Storage      | `localStorage`                                 |
| Auth Header        | Axios Interceptors                             |
| Streaming Chat     | EventSource or SSE polyfill                    |
| Build Tool         | Vite or CRA                                    |

---

## ⚙️ Directory Structure (React + Vite)

```
src/
├── components/
│   ├── ChatWindow.jsx
│   ├── MessageBubble.jsx
│   ├── FileUpload.jsx
│   ├── FolderSwitcher.jsx
│   └── Navbar.jsx
├── pages/
│   ├── ChatPage.jsx
│   ├── LoginPage.jsx
│   └── MyDocuments.jsx
├── services/
│   ├── api.js         # Axios config with token
│   └── authService.js
├── context/
│   └── AuthContext.jsx
├── App.jsx
├── main.jsx
└── index.css
```

---

## 💡 Phase 2: Initial Focus — **Login + Chat Page**

Would you like to:

1. Start with **setting up the project (React + Tailwind)**, or
2. Begin with the **Chat Page UI with backend integration (assuming JWT already exists)?**

You can also upload your JWT-auth login API response format if you want me to build the login too.