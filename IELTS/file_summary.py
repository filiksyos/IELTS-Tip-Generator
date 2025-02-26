import os

# Define paths
ROOT_BUILD_FILE = "build.gradle"
MODULE_DIRS = ["app", "data", "domain", "presentation", "test-results"]  # Include all modules
EXCLUDE_DIRS = ["app/build", "data/build", "domain/build", "presentation/build"]  # Exclude build directories
SUMMARY_FILE = "summary.txt"

def should_include_file(file_path):
    # Exclude files in the build directories
    for exclude_dir in EXCLUDE_DIRS:
        if exclude_dir in file_path:
            return False
    return True

def get_files_to_include():
    # Collect files from build.gradle and module directories
    files_to_include = []


    # Add the root build.gradle file
    if os.path.isfile(ROOT_BUILD_FILE):
        files_to_include.append(ROOT_BUILD_FILE)

    # Add all files in the module directories except build directories
    for module_dir in MODULE_DIRS:
        for root, _, files in os.walk(module_dir):
            for file in files:
                file_path = os.path.join(root, file)
                if should_include_file(file_path):
                    files_to_include.append(file_path)

    return files_to_include

def write_summary_file(files_to_include):
    # Write content to summary.txt
    with open(SUMMARY_FILE, 'w') as summary:
        for file_path in files_to_include:
            summary.write(f"File: {file_path}\n")
            summary.write("-" * 40 + "\n")
            try:
                with open(file_path, 'r') as file_content:
                    summary.write(file_content.read())
            except Exception as e:
                summary.write(f"Error reading file: {e}\n")
            summary.write("\n" + "=" * 40 + "\n\n")

def main():
    files_to_include = get_files_to_include()
    write_summary_file(files_to_include)

if __name__ == "__main__":
    main()
