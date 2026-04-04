---
name: /publish
description: Create and publish a new release
input: version
---

# Publish Release

## Steps

1. **Ask user for:**
   - Version number (e.g., 1.0.3)

2. **Update CHANGELOG.md:**
   - Add new header at top:
   ```
   ## {VERSION} - {SHORT_DESCRIPTION}
   **{CURRENT DATE}**

   ### Added
   - ...

   ---

   ```
   - Use summary of all changes since latest release

3. **Commit and push:**
   ```bash
   git add -A && git commit -m "Prepare release {VERSION}" && git push
   ```

4. **Create and push tag:**
   ```bash
   git tag {VERSION} && git push origin {VERSION}
   ```

## Notes

- Version format: X.Y.Z (no v prefix, no brackets)
- Tag triggers GitHub Actions workflow automatically
- Workflow publishes to Maven Central and creates GitHub Release
- Release title is extracted from CHANGELOG (format: "X.Y.Z - Description")

## Changelog Format (Reference)

```
## {VERSION} - {SHORT_DESCRIPTION}
**{RELEASE_DATE}**

### Added
- ...

### Changed
- ...

### Removed
- ...

---

## {PREV_VERSION} - {PREV_DESCRIPTION}
**{PREV_DATE}**
...
```

Rules:
- VERSION: only number (e.g., `1.0.2`, NOT `v1.0.2` or `[1.0.2]`)
- DATE: bold format `**YYYY-MM-DD**`
- Separate releases with `---`