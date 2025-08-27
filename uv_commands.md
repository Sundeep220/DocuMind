Got it 🚀 — you want a **complete cheat sheet** of `uv` commands for managing a Python project. Since `uv` is designed as a modern, fast replacement for `pip`, `pip-tools`, `virtualenv`, and `poetry`, it covers dependency management, environment handling, and project workflows.

Here’s a **one-stop cheat sheet** for managing Python projects with `uv`:

---

# 🐍 `uv` Python Project Management Cheat Sheet

## 🔹 Installation

```bash
# Install uv (recommended via pipx)
pipx install uv

# Or via pip
pip install uv
```

---

## 📦 Project Setup

```bash
# Initialize a new project (creates pyproject.toml)
uv init my-project
cd my-project
```

---

## 📥 Dependency Management

```bash
# Add a package
uv add requests

# Add with version
uv add requests==2.32.0

# Add multiple
uv add numpy pandas matplotlib

# Add dev dependencies
uv add pytest --dev
uv add black isort --dev

# Remove a package
uv remove requests

# Update dependencies
uv update
uv update requests

# Sync dependencies (reinstall exact versions from lockfile)
uv sync
```

---

## 🛠 Virtual Environment

```bash
# Create venv in .venv (auto if not exists)
uv venv

# Activate venv (Linux/macOS)
source .venv/bin/activate

# Activate venv (Windows PowerShell)
.venv\Scripts\Activate.ps1

# Use venv automatically (recommended)
uv run python script.py
```

---

## ▶️ Running Code

```bash
# Run a script with dependencies
uv run python main.py

# Run installed CLI from deps
uv run black .

# Run module
uv run -m mypackage.module
```

---

## 📋 Lockfile & Reproducibility

```bash
# Export requirements.txt
uv export > requirements.txt

# Install from requirements.txt
uv pip install -r requirements.txt
```

---

## 📦 Package Management

```bash
# Show installed packages
uv pip list

# Show outdated packages
uv pip list --outdated

# Uninstall a package
uv pip uninstall requests
```

---

## 🔍 Info & Debugging

```bash
# Show dependency graph
uv tree

# Show where a package is installed from
uv pip show requests

# Check for security issues
uv audit
```

---

## 🧪 Testing & Linting

```bash
# Run tests (if pytest in deps)
uv run pytest

# Run lint/format
uv run black .
uv run isort .
```

---

## 🏗 Building & Publishing

```bash
# Build package (wheel & sdist)
uv build

# Publish to PyPI
uv publish
```

---

## 🔄 Other Useful Commands

```bash
# Clean caches
uv cache clean

# Check Python versions available
uv python list

# Install a specific Python version
uv python install 3.12
```

---


# ⚖️ `uv add` vs `uv pip install`

## 🔹 `uv add`

* **Purpose:** Manage **project dependencies** (like `poetry add` or `npm install`).
* **Updates**:

  * `pyproject.toml` → adds the dependency under `[project]` or `[tool.uv.dependencies]`.
  * `uv.lock` → updates lockfile with exact versions.
* **Use case:** When you want a dependency to become part of your **project’s managed environment**.
* **Example:**

  ```bash
  uv add requests
  ```

  This will:

  1. Add `requests` to your `pyproject.toml`
  2. Update `uv.lock`
  3. Install it into the project’s virtual environment

---

## 🔹 `uv pip install`

* **Purpose:** Low-level package installation (like `pip install`).
* **Does NOT** update `pyproject.toml` or `uv.lock`.
* Just installs packages directly into the environment.
* **Use case:** Quick experimentation, installing local tools, or when you don’t want to track the dependency in your project.
* **Example:**

  ```bash
  uv pip install requests
  ```

  This will:

  * Install `requests` into the active environment
  * **No changes** to `pyproject.toml` or `uv.lock`

---

## 🔑 Rule of Thumb

* Use **`uv add`** when working inside a **project** → keeps deps tracked & reproducible.
* Use **`uv pip install`** for **ad-hoc installs** (like a Jupyter notebook or debugging session) where you don’t care about project metadata.

---

👉 Think of it like this:

* `uv add` = **project-level, tracked dependency**
* `uv pip install` = **environment-level, untracked dependency**

---

# Updating project python version using uv
Perfect 👌 — since you already have Python **3.13** installed and visible in `uv python list`, you don’t need to install it again. You just need to **switch your project to use it**.

Here’s exactly what to do:

---

# 🔄 Switch Existing `uv` Project to Python 3.13

1. **Pin your project to Python 3.13**

   ```bash
   uv python pin 3.13
   ```

   This updates your project metadata so `.venv` will use Python 3.13.

---

2. **Re-sync dependencies with the new interpreter**

   ```bash
   uv sync --python 3.13
   ```

   This will rebuild your `.venv` using Python 3.13 and reinstall everything from `uv.lock`.

---

3. **Verify**

   ```bash
   uv run python --version
   ```

   Expected:

   ```
   Python 3.13.x
   ```

---

⚡ That’s it — now all your libraries and your environment are running under Python 3.13, with your lockfile ensuring consistency.

---

