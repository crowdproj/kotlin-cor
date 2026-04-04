# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-04-04

### Added
- Full documentation in `docs/` directory
  - Core Concepts (Worker, Chain, Parallel, Loop)
  - DSL Reference with full API documentation
  - Examples from basic to advanced usage
  - Detailed comparison with alternatives
  - Contributing guide
- README with clear niche positioning (BPMS alternative, code-first)
- GitHub Actions workflow for automated testing (`check.yml`)
- `${VERSION}` placeholder in documentation
- Git tag-based versioning (v1.0.1 → 1.0.1, 1.0.1 → 1.0.1)

### Changed
- Version now reads from git tags via `git describe`
- Unified GitHub Actions workflow (publish + release)

### Removed
- `.gitlab-ci.yml` (replaced by GitHub Actions)

### Infrastructure
- Version defaults to "unspecified" when no tag present
- Support for both `v1.0.1` and `1.0.1` tag formats

---

## [1.0.0] - 2026-04-02

### Added
- First release to Maven Central
- Chain of Responsibility pattern implementation
- Worker, Chain, Parallel, Loop handlers
- Kotlin Multiplatform support (JVM, JS, Native, Wasm)
- DSL for business logic composition
- Error handling with `except` blocks
- Settings support for external configuration