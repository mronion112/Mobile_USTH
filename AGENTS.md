# AI Agent Guidelines

- Read `.cursor` or `.claude` folders if present.
- Reference `CONTEXT.md` and `README.md` for project domain knowledge and architectural context.
- Strictly adhere to rules in `.agents/rules/`:
  - `code-style.md` for naming conventions, Kotlin formatting, and Compose styles.
  - `architecture-rules.md` for layer boundaries, on-device feed fetching, and local storage rules.
  - `karpathy-rules.md` for pragmatic, disciplined engineering guidelines.
- Design tokens must align with `DESIGN2.md` and `src/DESIGN2_IMPLEMENTATION.md` (Editorial Pulse theme).
- Screens must match contracts in `src/00-app-shell/Guide.md` and individual screen guides.
