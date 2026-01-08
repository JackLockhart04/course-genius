import os, shutil, zipfile

def build_code_only():
    zip_name = "deployment.zip"
    if os.path.exists(zip_name): os.remove(zip_name)

    with zipfile.ZipFile(zip_name, "w", zipfile.ZIP_DEFLATED) as z:
        # Just zip your app code
        for root, _, files in os.walk("app"):
            for f in files:
                if "__pycache__" in root: continue
                path = os.path.join(root, f)
                z.write(path, path) # Zips as app/...

    print(f"Done! Created code-only {zip_name}")

if __name__ == "__main__": build_code_only()