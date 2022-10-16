import os

dir_start = "/Users/roy/dev/git/java-android-developertools/app/src/main/res/"

start = 3
end = 60

for i in range(start, end + 1):
    dir = dir_start + f"values-v{i}"
    if not os.path.exists(dir):
        os.makedirs(dir, exist_ok=True)
    f = open(f"{dir}/strings.xml", "w")
    f.write(f"""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="qualifier_platform_version">v{i}</string>
</resources>
""")
    f.close()
