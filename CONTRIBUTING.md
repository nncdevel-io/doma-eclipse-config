# Contributing to doma-eclipse-config

Thank you for your interest in contributing to this Eclipse plugin! This guide will help you get started.

## Quick Setup

### Prerequisites
- Eclipse IDE for Eclipse Committers (includes PDE)
- Java 8+
- Git

### Development Environment
1. **Clone the repository**
   ```bash
   git clone https://github.com/nncdevel-io/doma-eclipse-config.git
   cd doma-eclipse-config
   ```

2. **Import in Eclipse**
   - File → Import... → Existing Projects into Workspace
   - Select the project root directory
   - Ensure PDE (Plug-in Development Environment) is available

3. **Debug/Test**
   - Right-click project → Run As → Eclipse Application
   - This launches a new Eclipse instance with your plugin loaded

## How to Contribute

### Reporting Bugs
1. Check [existing issues](https://github.com/nncdevel-io/doma-eclipse-config/issues)
2. Create new issue with:
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment (Eclipse version, OS)
   - Error logs/screenshots

### Submitting Changes
1. **Fork & Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Follow existing code style
   - Add Javadoc for public methods
   - Include tests when possible

3. **Test Your Changes**
   - Run existing tests: Right-click test class → Run As → JUnit Test
   - Test manually with sample project in `examples/`
   - Verify on different platforms if possible

4. **Submit Pull Request**
   - Clear title and description
   - Reference related issues
   - Include screenshots for UI changes

### Code Guidelines

#### Java Style
- Follow standard Java conventions
- Use descriptive variable names
- Add comprehensive Javadoc

#### Testing
- Eclipse-independent logic → `DomaConfigUtilsTest`
- Eclipse-dependent code → `ConfigHandlerTest`  
- Add tests for new features
- Ensure existing tests pass

#### Key Files
- `ConfigHandler.java`: Main plugin logic (Eclipse APIs)
- `DomaConfigUtils.java`: Testable utilities (no Eclipse deps)
- `plugin.xml`: UI configuration
- `META-INF/MANIFEST.MF`: Plugin metadata

## Building & Testing

### Building Plugin
1. Right-click project → Export...
2. Plug-in Development → Deployable plug-ins and fragments
3. Select destination directory
4. JAR will be created in `plugins/` folder

### Testing
- **Unit tests**: `test/` directory
- **Manual testing**: Use `examples/doma-sample-project/`
- **Integration**: Install plugin in clean Eclipse

## Common Development Tasks

### Adding New Features
1. Update logic in `DomaConfigUtils` (testable)
2. Update `ConfigHandler` for Eclipse integration
3. Add tests for both classes
4. Update documentation

### Fixing Bugs
1. Write test that reproduces the bug
2. Fix the bug (make test pass)
3. Ensure no regression in existing tests

### OS-Specific Issues
The plugin handles different path separators:
- Windows: `repository\\org\\seasar\\doma`
- Linux/Mac: `repository/org/seasar/doma`

Test on multiple platforms when changing path-related code.

## Release Process

1. Update version in `META-INF/MANIFEST.MF`
2. Run all tests
3. Build plugin JAR
4. Create GitHub release with JAR attachment
5. Update documentation

## Community

- **Language**: English or Japanese welcome
- **Be respectful**: Follow standard open source etiquette
- **Ask questions**: Use GitHub Issues for development questions

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

For detailed documentation (Japanese), see [README.md](README.md).