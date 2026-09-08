import os
import shutil
import glob

build_libs = r"e:\minecraft\myMods\onter_ic2\build\libs"
jars = [f for f in glob.glob(os.path.join(build_libs, "onter_ic2-*.jar")) if not f.endswith("-sources.jar")]
if not jars:
    print("No built JAR found in build/libs!")
    exit(1)

src = max(jars, key=os.path.getmtime)
jar_name = os.path.basename(src)
print(f"Deploying latest JAR: {jar_name}")

destinations = [
    r"C:\Users\onter\AppData\Roaming\QuantumLauncher\instances\1.21.1\.minecraft\mods",
    r"E:\minecraft\ATM\client\mods",
    r"E:\minecraft\ATM\server\mods"
]

for dst in destinations:
    if os.path.exists(dst):
        for f in glob.glob(os.path.join(dst, "onter_ic2*.jar")):
            try:
                os.remove(f)
                print(f"Removed old {f}")
            except Exception as e:
                print(f"Could not remove {f}: {e}")
        shutil.copy(src, dst)
        print(f"Copied {jar_name} -> {dst}")
    else:
        print(f"Directory not found: {dst}")

print("Deployment complete!")
