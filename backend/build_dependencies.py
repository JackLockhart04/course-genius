import os, shutil, subprocess, zipfile

ARCH = "x86_64" 
PYTHON_VER = "312" 
PLATFORM = f"manylinux2014_{ARCH}" 

def build_dependencies():
    # 1. Clean up old builds
    if os.path.exists('layer_temp'): shutil.rmtree('layer_temp')
    
    # IMPORTANT: Dependencies MUST be in a folder named 'python'
    lib_path = os.path.join('layer_temp', 'python')
    os.makedirs(lib_path)

    print(f"--- Downloading Linux Wheels for Layer ---")
    subprocess.run([
        "pip", "install",
        "--platform", PLATFORM,
        "--target", lib_path,
        "--implementation", "cp",
        "--python-version", PYTHON_VER,
        "--only-binary=:all:",
        "--upgrade",
        "-r", "requirements.txt"
    ], check=True)

    # 2. Zip the 'python' folder
    # The zip must contain the 'python' folder at its root
    print("--- Zipping Dependencies ---")
    with zipfile.ZipFile("python_layer.zip", "w", zipfile.ZIP_DEFLATED) as z:
        for root, _, files in os.walk("layer_temp"):
            for f in files:
                if "__pycache__" in root: continue
                # This ensures the internal path starts with 'python/'
                path = os.path.join(root, f)
                z.write(path, os.path.relpath(path, "layer_temp"))

    shutil.rmtree('layer_temp')
    size_mb = os.path.getsize('python_layer.zip')/1024/1024
    print(f"Done! Created python_layer.zip ({size_mb:.2f}MB)")

if __name__ == "__main__":
    build_dependencies()