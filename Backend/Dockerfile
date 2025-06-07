FROM python:3.11-slim

RUN apt-get update && apt-get install -y \
    bash \
    build-essential \
    gcc \
    gfortran \
    libfreetype6-dev \
    libopenblas-dev \
    python3-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .

ARG USE_TF_CPU=1

RUN if [ "$USE_TF_CPU" = "1" ]; then \
        sed -i 's/tensorflow/tensorflow-cpu/' requirements.txt; \
    fi && \
    pip install --upgrade pip && pip install -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["sh", "-c", "python manage.py migrate && python manage.py runserver 0.0.0.0:8000"]