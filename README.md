# Smart Home Project

## Backend with Django Rest Framework (DRF)

### Setup Backend

```bash
cd Backend
```

```bash
py -m venv venv
```

```bash
.\venv\Scripts\activate
```

```bash
pip install -r requirements.txt
```

#### Update pip packages if needed (Latest version: 25.0.1)

```bash
py -m pip install --upgrade pip
```

#### Install torch version helping CUDA

```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu117
```

#### Run server

```bash
python manage.py runserver 0.0.0.0:<host>
```

---

## Frontend

### Setup Frontend

```bash
cd Frontend
```

```bash
npm install
```

### Run Development Server

```bash
npm run dev
```

---

