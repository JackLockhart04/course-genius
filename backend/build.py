import os, shutil, subprocess, zipfile

# CONFIG: Match your Lambda 'Runtime settings'
ARCH = "x86_64" 
PYTHON_VER = "312" # For python 3.12
# Use manylinux2014 or manylinux_2_17 for broader compatibility
PLATFORM = f"manylinux2014_{ARCH}" 

def build_on_pc():
    if os.path.exists('package'): shutil.rmtree('package')
    os.makedirs('package')

    print(f"--- Downloading Linux Wheels for {PLATFORM} ---")
    
    # We use 'pip install' with --platform because it is more reliable than 'download' 
    # for resolving the sub-dependencies of Pydantic.
    subprocess.run([
        "pip", "install",
        "--platform", PLATFORM,
        "--target", "package",
        "--implementation", "cp",
        "--python-version", PYTHON_VER,
        "--only-binary=:all:",
        "--upgrade",
        "-r", "requirements.txt"
    ], check=True)

    # NEW: Safety check for pydantic_core
    core_path = os.path.join("package", "pydantic_core")
    if os.path.exists(core_path):
        # Look for the .so file to ensure it's Linux, not .pyd (Windows)
        has_so = any(f.endswith(".so") for f in os.listdir(core_path))
        if not has_so:
            print("WARNING: No Linux binary (.so) found in pydantic_core. Fixing...")
            # Force a targeted download of the core binary
            subprocess.run([
                "pip", "install", "--platform", PLATFORM, "--target", "package",
                "--implementation", "cp", "--python-version", PYTHON_VER,
                "--only-binary=:all:", "--upgrade", "pydantic-core"
            ], check=True)

    print("--- Copying Application Code ---")
    # Ensure __init__.py exists in app folder
    if not os.path.exists("app/__init__.py"):
        with open("app/__init__.py", "w") as f: pass
        
    shutil.copytree('app', 'package/app', dirs_exist_ok=True)

    # 5. Create Zip
    with zipfile.ZipFile("deployment.zip", "w", zipfile.ZIP_DEFLATED) as z:
        for root, _, files in os.walk("package"):
            for f in files:
                if ".dist-info" in root or "__pycache__" in root: continue
                path = os.path.join(root, f)
                z.write(path, os.path.relpath(path, "package"))

    print(f"Done! Created deployment.zip ({os.path.getsize('deployment.zip')/1024/1024:.2f}MB)")

if __name__ == "__main__": build_on_pc()