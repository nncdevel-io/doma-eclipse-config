# doma-eclipse-config

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-0.0.3-blue.svg)]()

An Eclipse IDE plugin for Doma2 projects that automatically configures build paths for Maven-based Doma2 projects, ensuring SQL and script files are properly recognized within Eclipse.

## Overview

**Doma2** is a lightweight O/R mapping framework for Java that uses external SQL files and annotation processors to generate DAO implementations at compile time.

**This plugin** solves a common Eclipse IDE issue where SQL files (`.sql`) and script files (`.script`) in `src/main/resources` are excluded from the build path, hampering the development experience.

### Key Features
- **Auto-detection**: Identifies Doma2 Maven projects via `pom.xml` dependencies or `.factorypath` files
- **Classpath modification**: Changes `excluding="**"` to `including="**/*.script|**/*.sql"`
- **Cross-platform**: Works on Windows, Linux, and macOS
- **Non-destructive**: Safely updates existing project settings

## Quick Start

### Prerequisites
- Eclipse IDE for Java Developers (4.6+)
- Java 8+
- Maven 3.6+
- A Doma2 project

### Installation
1. Download or build the plugin JAR: `io.nncdevel.doma-eclipse-config_0.0.3.jar`
2. Copy to Eclipse's `dropins` folder
3. Restart Eclipse
4. Look for the Doma icon ![](icons/doma.png) in the main toolbar

### Usage
1. Click the Doma icon in Eclipse toolbar
2. Plugin automatically detects Doma2 Maven projects in workspace
3. Modifies `.classpath` files to include SQL/script files
4. Success dialog shows which projects were updated

## Development

### Building from Source
1. Import project in Eclipse with PDE
2. Right-click project → Export... → Deployable plug-ins and fragments
3. Output JAR will be in `plugins/` directory

### Contributing
We welcome contributions! Please:
1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

See the main [README.md](README.md) (Japanese) for detailed contribution guidelines.

### Project Structure
```
src/io/nncdevel/domaeclipseconfig/
├── Activator.java           # Plugin lifecycle
└── handlers/
    ├── ConfigHandler.java   # Main logic (Eclipse-dependent)
    └── DomaConfigUtils.java # Utilities (Eclipse-independent)
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/nncdevel-io/doma-eclipse-config/issues)
- **Documentation**: [README.md](README.md) (Japanese, comprehensive)
- **Testing**: [TESTING.md](TESTING.md) (Japanese)

---

For comprehensive documentation in Japanese, please see [README.md](README.md).