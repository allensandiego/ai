# Repository Instructions

## Project boundary

- Before working, resolve the active Git root with `git rev-parse --show-toplevel`
  from the workspace. Treat that absolute path as the sole project root.
- Read and write project files only under that root. Do not inspect or modify
  parent directories, sibling projects, unrelated workspaces, or user-home
  files. If a task appears to require an out-of-root file, stop and ask first.
- Do not hard-code this machine's absolute path in project files. Use
  repository-relative paths in documentation; derive absolute paths from the
  resolved root only when a tool requires them.

## Work within the existing layout

- Inspect the repository tree, `pom.xml`, relevant source/configuration, tests,
  and `src/main/resources/schema.sql` before making changes.
- Preserve the existing Maven and Java package layout rooted at
  `src/main/java/com/allensandiego/ai/` and
  `src/test/java/com/allensandiego/ai/`. Follow existing conventions and add
  directories only when the task requires them.
- Do not assume that a suggested or documented future layer already exists.
  Keep application source, tests, documentation, and generated artifacts inside
  the repository. Store generated build and Playwright screenshot output under
  `target/`.
