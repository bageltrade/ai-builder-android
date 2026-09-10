# AI Builder — Browser Studio

Full browser version of AI Builder. Same core experience as the Android app:

- Multi-model AI (NVIDIA NIM Nemotron / Llama, Google Gemini)
- High Thinking reasoning panel
- Live sandboxed preview (mobile / tablet / desktop)
- Multi-file code editor (HTML / CSS / JS)
- Version history + rollback
- Quick templates (Habit Tracker, Neon Pong, Markdown Notes, Pomodoro)
- Export single-file HTML or copy all code
- API keys stored locally in the browser

## Run

Just open `index.html` in any modern browser, or serve it:

```bash
npx serve .
# or
python3 -m http.server 8080
```

Then open http://localhost:8080

## Settings

Click **Settings** and paste your:

- NVIDIA NIM API key (for Nemotron / Llama)
- Gemini API key (for Gemini models)

Keys stay in `localStorage` on your machine only.

## Structure

```
ai-builder-web/
├── index.html
├── styles.css
├── app.js
└── README.md
```

Owned by Axion. Built by A.
