"""Launch the user-installed JDT LS with Java 25 and a project-private cache."""

import hashlib
import os
from pathlib import Path
import re
import shutil
import sys


root = Path(__file__).resolve().parent.parent
server = Path(os.environ.get(
    "JDTLS_HOME", Path.home() / ".local/share/jdtls/1.61.0"
)).expanduser()
launcher = server / "bin/jdtls"
if not launcher.is_file():
    sys.exit(f"JDT LS not found at {launcher}; see README.md: Java language server.")

# Prefer the user's JDK; otherwise reuse a Java 25 installation provisioned by Gradle.
candidates = []
if os.environ.get("JAVA_HOME"):
    candidates.append(Path(os.environ["JAVA_HOME"]).expanduser())
else:
    java = shutil.which("java")
    if java:
        candidates.append(Path(java).resolve().parent.parent)
    gradle_home = Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle"))
    candidates.extend(sorted((gradle_home / "jdks").glob("*")))

java_home = None
for candidate in candidates:
    release = candidate / "release"
    if release.is_file() and (candidate / "bin/javac").is_file():
        if re.search(r'^JAVA_VERSION="25(?:[.\"+\-])', release.read_text(), re.MULTILINE):
            java_home = candidate.resolve()
            break
if java_home is None:
    sys.exit("JDK 25 required: set JAVA_HOME to a JDK 25 installation (or provision it with Gradle).")

os.environ["JAVA_HOME"] = str(java_home)
os.environ["PATH"] = str(java_home / "bin") + os.pathsep + os.environ.get("PATH", "")
# Eclipse requires the workspace outside the imported project tree.
# Hash the full path so equally named checkouts do not share workspace locks.
cache_home = Path(os.environ.get("XDG_CACHE_HOME", Path.home() / ".cache"))
project_key = hashlib.sha256(os.fsencode(root)).hexdigest()
cache = cache_home / "schmaloogium/jdtls" / project_key
cache.mkdir(parents=True, exist_ok=True)
os.chdir(root)
os.execv(sys.executable, [
    sys.executable, str(launcher),
    "--jvm-arg=-Djava.import.generatesMetadataFilesAtProjectRoot=false",
    "--java-executable", str(java_home / "bin/java"),
    "-configuration", str(cache / "configuration"),
    "-data", str(cache / "workspace"),
    *sys.argv[1:],
])
