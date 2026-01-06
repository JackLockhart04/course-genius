import os, shutil, subprocess, zipfile

# CONFIG: Change ARCH to 'aarch64' if your Lambda is ARM64
ARCH = "x86_64" 
PYTHON_VER = "314" # 314 for Python 3.14

def build_on_pc():
    # 1. Clean up
    if os.path.exists('package'): shutil.rmtree('package')
    os.makedirs('package')

    # 2. Download the EXACT Linux wheels
    print(f"--- Downloading Linux Wheels for {ARCH} ---")
    subprocess.run([
        "pip", "download",
        "--platform", f"manylinux2014_{ARCH}",
        "--only-binary=:all:",
        "--dest", "package",
        "--python-version", PYTHON_VER,
        "--implementation", "cp",
        "-r", "requirements.txt"
    ], check=True)

    # 3. UNZIP the wheels into the package folder
    print("--- Unpacking Wheels ---")
    for file in os.listdir('package'):
        if file.endswith(".whl"):
            with zipfile.ZipFile(os.path.join('package', file), 'r') as wheel:
                wheel.extractall('package')
            os.remove(os.path.join('package', file)) # Remove the .whl after extracting

    # 4. Copy your code & Zip
    shutil.copy("main.py", "package/main.py")
    with zipfile.ZipFile("deployment.zip", "w", zipfile.ZIP_DEFLATED) as z:
        for root, _, files in os.walk("package"):
            for f in files:
                # Skip unnecessary metadata to keep it under 10MB
                if ".dist-info" in root or "__pycache__" in root: continue
                path = os.path.join(root, f)
                z.write(path, os.path.relpath(path, "package"))

    print(f"Done! Created deployment.zip ({os.path.getsize('deployment.zip')/1024/1024:.2f}MB)")

if __name__ == "__main__": build_on_pc()