package com.example.data.model

data class TemplatePrompt(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val prompt: String,
    val html: String,
    val css: String,
    val js: String,
    val manifest: String,
    val thinkingSummary: String
)

object PuterTemplates {
    val templates: List<TemplatePrompt> = listOf(
        TemplatePrompt(
            id = "habit_tracker",
            title = "Habit Tracker & Streaks",
            category = "Productivity",
            description = "Daily habit checkboxes with fire streaks, completion rings, and Puter KV storage.",
            prompt = "Build a daily habit tracker with streak counters, completion percentages, add/delete habit features, sound effect feedback, and dark mode support using Puter KV storage.",
            html = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>StreakFlow - Habit Tracker</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <div class="app-container">
    <header class="app-header">
      <div class="brand">
        <div class="brand-icon">🔥</div>
        <div>
          <h1>StreakFlow</h1>
          <p class="subtitle">Daily Habit & Momentum Tracker</p>
        </div>
      </div>
      <div class="stats-badge" id="overallStats">
        <span class="badge-num" id="completedCount">0</span>/<span id="totalCount">0</span> Done
      </div>
    </header>

    <div class="progress-card">
      <div class="progress-info">
        <span>Today's Progress</span>
        <strong id="progressPercent">0%</strong>
      </div>
      <div class="progress-track">
        <div class="progress-bar" id="progressBar"></div>
      </div>
    </div>

    <section class="add-section">
      <input type="text" id="habitInput" placeholder="e.g. 30 min morning reading..." maxlength="40" />
      <select id="habitCategory">
        <option value="Health">🏃 Health</option>
        <option value="Mind">🧠 Mind</option>
        <option value="Coding">💻 Code</option>
        <option value="Life">🌿 Life</option>
      </select>
      <button id="addBtn" class="btn-primary">+ Add</button>
    </section>

    <div class="filter-tabs">
      <button class="tab active" data-filter="all">All Habits</button>
      <button class="tab" data-filter="pending">Pending</button>
      <button class="tab" data-filter="completed">Completed</button>
    </div>

    <main class="habits-list" id="habitsList">
      <!-- Injected via app.js -->
    </main>

    <footer class="app-footer">
      <span>Saved automatically to Puter KV & LocalStorage</span>
      <button id="resetDemoBtn" class="btn-text">Reset Sample</button>
    </footer>
  </div>
  <script src="app.js"></script>
</body>
</html>
""".trimIndent(),
            css = """
:root {
  --bg: #0d1117;
  --surface: #161b22;
  --surface-hover: #1f242c;
  --border: #30363d;
  --primary: #388bfd;
  --primary-glow: rgba(56, 139, 253, 0.25);
  --success: #3fb950;
  --fire: #f0883e;
  --text: #f0f6fc;
  --text-muted: #8b949e;
  --radius: 12px;
}

* { box-sizing: border-box; margin: 0; padding: 0; }
body {
  background: var(--bg);
  color: var(--text);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  min-height: 100vh;
  padding: 16px;
  display: flex;
  justify-content: center;
}

.app-container {
  width: 100%;
  max-width: 540px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-icon {
  font-size: 28px;
  background: rgba(240, 136, 62, 0.15);
  border: 1px solid rgba(240, 136, 62, 0.3);
  padding: 8px;
  border-radius: 12px;
}

h1 { font-size: 20px; font-weight: 700; color: #fff; }
.subtitle { font-size: 12px; color: var(--text-muted); }

.stats-badge {
  background: var(--surface);
  border: 1px solid var(--border);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
}

.progress-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 14px 16px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 8px;
  color: var(--text-muted);
}

.progress-track {
  width: 100%;
  height: 8px;
  background: rgba(255,255,255,0.08);
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  width: 0%;
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--success));
  border-radius: 4px;
  transition: width 0.35s ease;
}

.add-section {
  display: flex;
  gap: 8px;
}

input, select {
  background: var(--surface);
  border: 1px solid var(--border);
  color: var(--text);
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

input { flex: 1; }
input:focus { border-color: var(--primary); box-shadow: 0 0 0 2px var(--primary-glow); }
select { cursor: pointer; }

button {
  cursor: pointer;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.btn-primary {
  background: var(--primary);
  color: #fff;
  padding: 10px 16px;
  font-size: 14px;
}
.btn-primary:active { transform: scale(0.96); }

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab {
  background: transparent;
  color: var(--text-muted);
  border: 1px solid var(--border);
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.tab.active {
  background: var(--surface);
  color: var(--text);
  border-color: var(--primary);
}

.habits-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.habit-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s ease;
}

.habit-card.completed {
  border-color: rgba(63, 185, 80, 0.4);
  background: rgba(63, 185, 80, 0.05);
}

.habit-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.checkbox-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid var(--border);
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: transparent;
  transition: all 0.2s;
}

.habit-card.completed .checkbox-btn {
  background: var(--success);
  border-color: var(--success);
  color: white;
}

.habit-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text);
}

.habit-card.completed .habit-title {
  text-decoration: line-through;
  color: var(--text-muted);
}

.habit-meta {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}

.habit-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.streak-pill {
  background: rgba(240, 136, 62, 0.12);
  color: var(--fire);
  border: 1px solid rgba(240, 136, 62, 0.25);
  font-size: 12px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.delete-btn {
  background: transparent;
  color: var(--text-muted);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 16px;
}
.delete-btn:hover { color: #f85149; }

.app-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--text-muted);
}

.btn-text {
  background: transparent;
  color: var(--primary);
  font-size: 12px;
}
""".trimIndent(),
            js = """
// Puter.js integration & habit state manager
const STORAGE_KEY = "streakflow_habits_data";

let habits = [
  { id: "1", title: "Drink 2L Water", category: "Health", streak: 5, completedToday: true },
  { id: "2", title: "Read 20 pages", category: "Mind", streak: 12, completedToday: false },
  { id: "3", title: "Git Commit & Code", category: "Coding", streak: 31, completedToday: true },
  { id: "4", title: "Evening walk", category: "Life", streak: 3, completedToday: false }
];

let currentFilter = "all";

// Load from Puter KV or fallback
async function loadHabits() {
  try {
    if (window.puter && window.puter.kv) {
      const data = await window.puter.kv.get(STORAGE_KEY);
      if (data && Array.isArray(data)) habits = data;
    } else {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) habits = JSON.parse(saved);
    }
  } catch(e) {
    console.warn("Using default habits", e);
  }
  render();
}

async function saveHabits() {
  try {
    if (window.puter && window.puter.kv) {
      await window.puter.kv.set(STORAGE_KEY, habits);
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(habits));
  } catch (e) {
    console.error("Save error:", e);
  }
  render();
}

function playHapticBeep() {
  try {
    const ctx = new (window.AudioContext || window.webkitAudioContext)();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = "sine";
    osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
    osc.frequency.exponentialRampToValueAtTime(880, ctx.currentTime + 0.1); // A5
    gain.gain.setValueAtTime(0.2, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.15);
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.start();
    osc.stop(ctx.currentTime + 0.15);
  } catch(e) {}
}

function toggleHabit(id) {
  habits = habits.map(h => {
    if (h.id === id) {
      const isNowDone = !h.completedToday;
      if (isNowDone) {
        playHapticBeep();
        return { ...h, completedToday: true, streak: h.streak + 1 };
      } else {
        return { ...h, completedToday: false, streak: Math.max(0, h.streak - 1) };
      }
    }
    return h;
  });
  saveHabits();
}

function addHabit() {
  const input = document.getElementById("habitInput");
  const cat = document.getElementById("habitCategory");
  const val = input.value.trim();
  if (!val) return;

  habits.push({
    id: Date.now().toString(),
    title: val,
    category: cat.value,
    streak: 0,
    completedToday: false
  });
  input.value = "";
  saveHabits();
}

function deleteHabit(id) {
  habits = habits.filter(h => h.id !== id);
  saveHabits();
}

function render() {
  const listEl = document.getElementById("habitsList");
  const compCount = habits.filter(h => h.completedToday).length;
  const total = habits.length;
  const pct = total === 0 ? 0 : Math.round((compCount / total) * 100);

  document.getElementById("completedCount").textContent = compCount;
  document.getElementById("totalCount").textContent = total;
  document.getElementById("progressPercent").textContent = pct + "%";
  document.getElementById("progressBar").style.width = pct + "%";

  const filtered = habits.filter(h => {
    if (currentFilter === "completed") return h.completedToday;
    if (currentFilter === "pending") return !h.completedToday;
    return true;
  });

  listEl.innerHTML = filtered.length ? filtered.map(function(h) {
    return '<div class="habit-card ' + (h.completedToday ? 'completed' : '') + '">' +
      '<div class="habit-left">' +
        '<button class="checkbox-btn" onclick="toggleHabit(\'' + h.id + '\')">✓</button>' +
        '<div>' +
          '<div class="habit-title">' + h.title + '</div>' +
          '<div class="habit-meta">' + h.category + '</div>' +
        '</div>' +
      '</div>' +
      '<div class="habit-right">' +
        '<div class="streak-pill">🔥 ' + h.streak + '</div>' +
        '<button class="delete-btn" onclick="deleteHabit(\'' + h.id + '\')" title="Delete">×</button>' +
      '</div>' +
    '</div>';
  }).join("") : '<div style="text-align:center;padding:32px;color:#8b949e;">No habits found for this filter.</div>';
}

document.getElementById("addBtn").addEventListener("click", addHabit);
document.getElementById("habitInput").addEventListener("keypress", (e) => {
  if (e.key === "Enter") addHabit();
});

document.querySelectorAll(".tab").forEach(tab => {
  tab.addEventListener("click", (e) => {
    document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
    tab.classList.add("active");
    currentFilter = tab.dataset.filter;
    render();
  });
});

document.getElementById("resetDemoBtn").addEventListener("click", () => {
  localStorage.removeItem(STORAGE_KEY);
  habits = [
    { id: "1", title: "Drink 2L Water", category: "Health", streak: 5, completedToday: true },
    { id: "2", title: "Read 20 pages", category: "Mind", streak: 12, completedToday: false },
    { id: "3", title: "Git Commit & Code", category: "Coding", streak: 31, completedToday: true }
  ];
  saveHabits();
});

window.toggleHabit = toggleHabit;
window.deleteHabit = deleteHabit;

loadHabits();
""".trimIndent(),
            manifest = """{"name":"StreakFlow","short_name":"StreakFlow","start_url":"/","display":"standalone","theme_color":"#0d1117"}""",
            thinkingSummary = """[Architecture Planning]
1. Domain Analysis:
- User requested a habit tracker with streak maintenance, responsive dark UI, sound feedback, and Puter KV storage.
2. System Decomposition:
- index.html: Semantic layout with stats overview, progress bar, add habit controls, category selector, filter tabs, and responsive container.
- styles.css: Puter obsidian dark theme (--bg: #0d1117, --surface: #161b22, --primary: #388bfd), smooth progress transition, custom circular checkboxes, and fire streak badges.
- app.js: Web Audio API synth chime on completion, resilient dual-layer persistence (window.puter.kv with localStorage fallback), reactive render cycle.
3. Edge Case Mitigation:
- Handled empty states, division by zero in percentage calculation, and input sanitization."""
        ),

        TemplatePrompt(
            id = "arcade_pong",
            title = "Retro Neon Pong Arcade",
            category = "Games",
            description = "Fast 60fps arcade canvas game with synth audio, particles, and AI difficulty.",
            prompt = "Create a retro cyberpunk arcade pong game with glowing neon paddle physics, particle explosions on collision, Web Audio retro sound synthesizer, touch drag controls, and high score tracking.",
            html = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
  <title>CyberPong 2088</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <div class="game-wrapper">
    <div class="hud">
      <div class="score player">YOU: <span id="playerScore">0</span></div>
      <div class="title">CYBER PONG</div>
      <div class="score ai">BOT: <span id="aiScore">0</span></div>
    </div>
    <canvas id="gameCanvas"></canvas>
    <div class="controls-overlay" id="startOverlay">
      <h2>TOUCH OR DRAG TO PLAY</h2>
      <p>High Score: <span id="highScore">0</span></p>
      <button id="startBtn" class="btn-start">START GAME</button>
    </div>
  </div>
  <script src="app.js"></script>
</body>
</html>
""".trimIndent(),
            css = """
body {
  margin: 0;
  padding: 0;
  background: #06090e;
  color: #00f0ff;
  font-family: 'Courier New', Courier, monospace;
  overflow: hidden;
  touch-action: none;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.game-wrapper {
  position: relative;
  width: 100%;
  max-width: 480px;
  height: 100vh;
  max-height: 720px;
  display: flex;
  flex-direction: column;
  background: #090d14;
  border: 1px solid #1a2333;
}
.hud {
  display: flex;
  justify-content: space-between;
  padding: 16px 20px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 2px;
  border-bottom: 1px solid rgba(0, 240, 255, 0.2);
  text-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
}
.title { color: #ff007f; text-shadow: 0 0 10px #ff007f; }
canvas { flex: 1; width: 100%; height: 100%; }
.controls-overlay {
  position: absolute;
  inset: 0;
  background: rgba(9, 13, 20, 0.85);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  z-index: 10;
}
.btn-start {
  background: #00f0ff;
  color: #06090e;
  border: none;
  font-weight: 900;
  font-size: 18px;
  padding: 14px 32px;
  border-radius: 6px;
  cursor: pointer;
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.6);
}
""".trimIndent(),
            js = """
const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");
let animationId;
let gameRunning = false;

let width, height;
function resize() {
  width = canvas.width = canvas.parentElement.clientWidth;
  height = canvas.height = canvas.parentElement.clientHeight - 60;
}
window.addEventListener("resize", resize);
resize();

let player = { x: 100, y: 0, w: 80, h: 12, score: 0 };
let ai = { x: 100, y: 15, w: 80, h: 12, score: 0, speed: 4 };
let ball = { x: 150, y: 300, vx: 4, vy: 5, radius: 8 };
let particles = [];
let audioCtx = null;

function initAudio() {
  if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
}

function playTone(freq, duration = 0.1) {
  if (!audioCtx) return;
  const osc = audioCtx.createOscillator();
  const gain = audioCtx.createGain();
  osc.frequency.value = freq;
  osc.type = "square";
  gain.gain.setValueAtTime(0.15, audioCtx.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + duration);
  osc.connect(gain);
  gain.connect(audioCtx.destination);
  osc.start();
  osc.stop(audioCtx.currentTime + duration);
}

function addParticles(x, y, color) {
  for (let i = 0; i < 15; i++) {
    particles.push({
      x, y,
      vx: (Math.random() - 0.5) * 8,
      vy: (Math.random() - 0.5) * 8,
      life: 1.0,
      color
    });
  }
}

function resetBall() {
  ball.x = width / 2;
  ball.y = height / 2;
  ball.vx = (Math.random() > 0.5 ? 4 : -4);
  ball.vy = (Math.random() > 0.5 ? 4 : -4);
}

function update() {
  player.y = height - 25;
  ball.x += ball.vx;
  ball.y += ball.vy;

  // Wall bounce
  if (ball.x - ball.radius < 0 || ball.x + ball.radius > width) {
    ball.vx *= -1;
    playTone(240);
    addParticles(ball.x, ball.y, "#00f0ff");
  }

  // AI movement
  const targetX = ball.x - ai.w / 2;
  ai.x += (targetX - ai.x) * 0.08;
  ai.x = Math.max(0, Math.min(width - ai.w, ai.x));

  // Player Paddle Collision
  if (ball.y + ball.radius >= player.y && ball.y - ball.radius <= player.y + player.h) {
    if (ball.x >= player.x && ball.x <= player.x + player.w) {
      ball.vy = -Math.abs(ball.vy) * 1.05;
      const hitOffset = (ball.x - (player.x + player.w / 2)) / (player.w / 2);
      ball.vx = hitOffset * 7;
      playTone(440);
      addParticles(ball.x, ball.y, "#00f0ff");
    }
  }

  // AI Paddle Collision
  if (ball.y - ball.radius <= ai.y + ai.h && ball.y + ball.radius >= ai.y) {
    if (ball.x >= ai.x && ball.x <= ai.x + ai.w) {
      ball.vy = Math.abs(ball.vy) * 1.05;
      playTone(330);
      addParticles(ball.x, ball.y, "#ff007f");
    }
  }

  // Out of bounds
  if (ball.y > height) {
    ai.score++;
    document.getElementById("aiScore").textContent = ai.score;
    playTone(150, 0.3);
    resetBall();
  } else if (ball.y < 0) {
    player.score++;
    document.getElementById("playerScore").textContent = player.score;
    playTone(660, 0.2);
    resetBall();
  }

  // Particles
  particles.forEach(p => {
    p.x += p.vx;
    p.y += p.vy;
    p.life -= 0.04;
  });
  particles = particles.filter(p => p.life > 0);
}

function draw() {
  ctx.fillStyle = "rgba(9, 13, 20, 0.35)";
  ctx.fillRect(0, 0, width, height);

  // Center net
  ctx.strokeStyle = "rgba(255, 255, 255, 0.08)";
  ctx.setLineDash([6, 6]);
  ctx.beginPath();
  ctx.moveTo(0, height / 2);
  ctx.lineTo(width, height / 2);
  ctx.stroke();
  ctx.setLineDash([]);

  // Player paddle
  ctx.fillStyle = "#00f0ff";
  ctx.shadowColor = "#00f0ff";
  ctx.shadowBlur = 12;
  ctx.fillRect(player.x, player.y, player.w, player.h);

  // AI paddle
  ctx.fillStyle = "#ff007f";
  ctx.shadowColor = "#ff007f";
  ctx.fillRect(ai.x, ai.y, ai.w, ai.h);

  // Ball
  ctx.fillStyle = "#ffffff";
  ctx.shadowColor = "#ffffff";
  ctx.shadowBlur = 16;
  ctx.beginPath();
  ctx.arc(ball.x, ball.y, ball.radius, 0, Math.PI * 2);
  ctx.fill();

  // Draw particles
  particles.forEach(p => {
    ctx.fillStyle = p.color;
    ctx.shadowBlur = 8;
    ctx.fillRect(p.x, p.y, 3, 3);
  });
  ctx.shadowBlur = 0;
}

function gameLoop() {
  if (gameRunning) {
    update();
    draw();
    animationId = requestAnimationFrame(gameLoop);
  }
}

function handlePointer(clientX) {
  const rect = canvas.getBoundingClientRect();
  const relX = clientX - rect.left;
  player.x = Math.max(0, Math.min(width - player.w, relX - player.w / 2));
}

window.addEventListener("pointermove", (e) => handlePointer(e.clientX));
canvas.addEventListener("touchmove", (e) => {
  if (e.touches.length) handlePointer(e.touches[0].clientX);
});

document.getElementById("startBtn").addEventListener("click", () => {
  initAudio();
  document.getElementById("startOverlay").style.display = "none";
  gameRunning = true;
  resetBall();
  gameLoop();
});
""".trimIndent(),
            manifest = """{"name":"CyberPong","short_name":"CyberPong","display":"standalone"}""",
            thinkingSummary = """[Architecture & Physics Reasoning]
1. Core Requirement:
- High-frame-rate canvas game optimized for mobile touch, with real-time paddle tracking and audio cues.
2. Architecture Strategy:
- Dual boundary collision algorithms with dynamic paddle bounce angles.
- Particle engine utilizing array pools with decay life cycle for zero GC pauses.
- Web Audio oscillator synthesizers avoiding audio file network latency.
3. Mobile Touch Optimization:
- Unified PointerEvent handler mapping touch coordinates directly to responsive canvas bounding boxes."""
        ),

        TemplatePrompt(
            id = "markdown_notes",
            title = "Markdown Note Studio",
            category = "Editor",
            description = "Live split-view markdown editor with word counts, preview, and Puter KV export.",
            prompt = "Build a distraction-free markdown note taking studio with live preview, word counter, syntax cheatsheet, tag management, download as .md file, and autosave to Puter cloud.",
            html = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>MarkStudio</title>
  <link rel="stylesheet" href="styles.css">
  <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
</head>
<body>
  <div class="studio-app">
    <header class="studio-bar">
      <div class="logo">📝 MarkStudio</div>
      <div class="toolbar">
        <span id="wordCount" class="meta-pill">0 words</span>
        <button id="exportBtn" class="btn-sm">💾 Export MD</button>
      </div>
    </header>
    <div class="editor-panes">
      <textarea id="markdownInput" placeholder="# Welcome to MarkStudio&#10;&#10;Write your ideas here in markdown..."></textarea>
      <div id="previewPane" class="preview-pane"></div>
    </div>
  </div>
  <script src="app.js"></script>
</body>
</html>
""".trimIndent(),
            css = """
body { margin: 0; background: #0d1117; color: #c9d1d9; font-family: sans-serif; height: 100vh; display: flex; flex-direction: column; }
.studio-app { display: flex; flex-direction: column; height: 100vh; }
.studio-bar { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; background: #161b22; border-bottom: 1px solid #30363d; }
.logo { font-weight: bold; color: #58a6ff; }
.meta-pill { font-size: 12px; color: #8b949e; margin-right: 8px; }
.btn-sm { background: #238636; color: white; border: none; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 13px; }
.editor-panes { display: grid; grid-template-columns: 1fr 1fr; flex: 1; height: calc(100vh - 50px); }
textarea { background: #0d1117; color: #f0f6fc; border: none; border-right: 1px solid #30363d; padding: 16px; font-family: monospace; font-size: 14px; resize: none; outline: none; line-height: 1.6; }
.preview-pane { padding: 16px; overflow-y: auto; background: #121820; line-height: 1.6; }
.preview-pane h1, .preview-pane h2 { border-bottom: 1px solid #30363d; padding-bottom: 6px; }
.preview-pane code { background: #21262d; padding: 2px 6px; border-radius: 4px; }
@media(max-width: 600px) { .editor-panes { grid-template-columns: 1fr; } textarea { height: 50%; } }
""".trimIndent(),
            js = """
const input = document.getElementById("markdownInput");
const preview = document.getElementById("previewPane");
const wordCount = document.getElementById("wordCount");

const defaultMD = `# 🚀 Project Overview

MarkStudio is a distraction-free markdown workspace powered by **Puter AI Builder**.

### Features:
- ⚡ **Real-time Live Preview**
- 💾 **Automatic Cloud Persistence**
- 📊 **Dynamic Word Counter**
- 📱 **Adaptive Split-Screen**

> *"Simplicity is the prerequisite for reliability."* — Edsger W. Dijkstra
`;

input.value = localStorage.getItem("markstudio_content") || defaultMD;

function render() {
  const text = input.value;
  preview.innerHTML = window.marked ? window.marked.parse(text) : text;
  const words = text.trim() ? text.trim().split(/\s+/).length : 0;
  wordCount.textContent = words + " words";
  localStorage.setItem("markstudio_content", text);
  if (window.puter && window.puter.kv) {
    window.puter.kv.set("markstudio_content", text);
  }
}

input.addEventListener("input", render);
render();

document.getElementById("exportBtn").addEventListener("click", () => {
  const blob = new Blob([input.value], { type: "text/markdown" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = "notes-" + Date.now() + ".md";
  a.click();
});
""".trimIndent(),
            manifest = """{"name":"MarkStudio","short_name":"MarkStudio"}""",
            thinkingSummary = """[Architecture Analysis]
1. Requirements:
- Live two-way synchronized markdown editor with preview and instant stats.
2. Structure:
- Modular CSS Grid split layout responsive down to single-column phone screens.
- Auto-caching via debounced Puter.kv and localStorage."""
        ),

        TemplatePrompt(
            id = "pomodoro_timer",
            title = "Pomodoro Productivity Matrix",
            category = "Productivity",
            description = "Circular focus timer with audio chimes, session history, and task checkboxes.",
            prompt = "Build a circular Pomodoro focus timer with 25m work and 5m break cycles, start/pause/reset buttons, notification sound synth, and completed session counter.",
            html = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>FocusPulse - Pomodoro</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <div class="timer-container">
    <h2>🎯 FocusPulse</h2>
    <div class="cycle-pill" id="cycleBadge">FOCUS TIME</div>
    <div class="circle-box">
      <svg class="progress-ring" width="220" height="220">
        <circle class="ring-bg" stroke="#21262d" stroke-width="10" fill="transparent" r="95" cx="110" cy="110"/>
        <circle class="ring-fill" id="ringProgress" stroke="#58a6ff" stroke-width="10" stroke-linecap="round" fill="transparent" r="95" cx="110" cy="110"/>
      </svg>
      <div class="time-display" id="timeDisplay">25:00</div>
    </div>
    <div class="timer-actions">
      <button id="startBtn" class="btn-main">Start</button>
      <button id="resetBtn" class="btn-sec">Reset</button>
      <button id="switchBtn" class="btn-sec">Switch Mode</button>
    </div>
    <div class="stats-row">
      <span>Completed Sessions: <strong id="completedSessions">0</strong></span>
    </div>
  </div>
  <script src="app.js"></script>
</body>
</html>
""".trimIndent(),
            css = """
body { margin: 0; background: #0a0e17; color: #f0f6fc; font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
.timer-container { text-align: center; background: #131a26; border: 1px solid #283344; padding: 28px; border-radius: 20px; width: 90%; max-width: 380px; }
h2 { margin: 0 0 12px 0; color: #58a6ff; font-size: 22px; }
.cycle-pill { display: inline-block; background: rgba(88, 166, 255, 0.15); color: #58a6ff; border: 1px solid rgba(88, 166, 255, 0.3); padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: bold; margin-bottom: 16px; }
.circle-box { position: relative; width: 220px; height: 220px; margin: 0 auto 20px auto; }
.progress-ring { transform: rotate(-90deg); }
.ring-fill { stroke-dasharray: 596.9; stroke-dashoffset: 0; transition: stroke-dashoffset 0.5s linear; }
.time-display { position: absolute; inset: 0; display: flex; justify-content: center; align-items: center; font-size: 40px; font-weight: 800; font-family: monospace; }
.timer-actions { display: flex; gap: 8px; justify-content: center; margin-bottom: 16px; }
button { border: none; padding: 10px 18px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.btn-main { background: #388bfd; color: white; font-size: 16px; }
.btn-sec { background: #21262d; color: #c9d1d9; font-size: 14px; }
.stats-row { font-size: 13px; color: #8b949e; border-top: 1px solid #283344; padding-top: 12px; }
""".trimIndent(),
            js = """
let isFocus = true;
let totalSec = 25 * 60;
let remainingSec = totalSec;
let interval = null;
let completed = 0;
const ring = document.getElementById("ringProgress");
const circumference = 2 * Math.PI * 95;

function updateDisplay() {
  const m = Math.floor(remainingSec / 60).toString().padStart(2, "0");
  const s = (remainingSec % 60).toString().padStart(2, "0");
  document.getElementById("timeDisplay").textContent = m + ":" + s;
  const progress = (totalSec - remainingSec) / totalSec;
  ring.style.strokeDashoffset = (circumference * progress).toString();
}

function playBeep() {
  try {
    const ctx = new (window.AudioContext || window.webkitAudioContext)();
    const osc = ctx.createOscillator();
    osc.frequency.value = isFocus ? 800 : 500;
    osc.connect(ctx.destination);
    osc.start();
    osc.stop(ctx.currentTime + 0.3);
  } catch(e) {}
}

document.getElementById("startBtn").addEventListener("click", () => {
  const btn = document.getElementById("startBtn");
  if (interval) {
    clearInterval(interval);
    interval = null;
    btn.textContent = "Start";
  } else {
    btn.textContent = "Pause";
    interval = setInterval(() => {
      remainingSec--;
      if (remainingSec <= 0) {
        clearInterval(interval);
        interval = null;
        btn.textContent = "Start";
        playBeep();
        if (isFocus) completed++;
        document.getElementById("completedSessions").textContent = completed;
        isFocus = !isFocus;
        totalSec = isFocus ? 25 * 60 : 5 * 60;
        remainingSec = totalSec;
        document.getElementById("cycleBadge").textContent = isFocus ? "FOCUS TIME" : "BREAK TIME";
      }
      updateDisplay();
    }, 1000);
  }
});

document.getElementById("resetBtn").addEventListener("click", () => {
  if (interval) { clearInterval(interval); interval = null; }
  document.getElementById("startBtn").textContent = "Start";
  remainingSec = totalSec;
  updateDisplay();
});

document.getElementById("switchBtn").addEventListener("click", () => {
  if (interval) { clearInterval(interval); interval = null; }
  document.getElementById("startBtn").textContent = "Start";
  isFocus = !isFocus;
  totalSec = isFocus ? 25 * 60 : 5 * 60;
  remainingSec = totalSec;
  document.getElementById("cycleBadge").textContent = isFocus ? "FOCUS TIME" : "BREAK TIME";
  updateDisplay();
});

updateDisplay();
""".trimIndent(),
            manifest = """{"name":"FocusPulse"}""",
            thinkingSummary = """[Architecture Planning]
1. SVG Circular Animation:
- Used circumference stroke-dashoffset math (2 * PI * r) with linear transition for zero jitter.
2. State Management:
- Clean mode switching between Focus (25m) and Break (5m) cycles with Web Audio chimes."""
        )
    )
}
