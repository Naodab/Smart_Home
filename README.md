## Backend

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

#### Cập nhật pip packages nếu cần (Latest version: 25.0.1)

```
    py -m pip install --upgrade pip
```

#### Install torch version helping CUDA

```
    pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu117
```