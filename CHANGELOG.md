# Changelog

## 1.0.1 - Documentation, CI/CD and versioning improvements
**2026-04-04**

### Added
- Full documentation in `docs/` directory (Core Concepts, DSL Reference, Examples, Comparison, Contributing)
- README with clear niche positioning (BPMS alternative, code-first)
- GitHub Actions workflow for publishing
- `${VERSION}` placeholder in documentation
- Git tag-based versioning

### Changed
- Version now reads from git tags via `git describe`
- Unified GitHub Actions workflow (publish + release)

### Removed
- `.gitlab-ci.yml` (replaced by GitHub Actions)

### Infrastructure
- Version defaults to "unspecified" when no tag present
- Support for both `v1.0.1` and `1.0.1` tag formats

---

## 1.0.0 - First Maven Central release
**2026-04-02**

### Added
- First release to Maven Central
- Chain of Responsibility pattern implementation
- Worker, Chain, Parallel, Loop handlers
- Kotlin Multiplatform support
- DSL for business logic composition
- Error handling with `except` blocks
- Settings support