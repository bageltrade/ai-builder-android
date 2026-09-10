/* AI Builder — Browser Studio */
const MODELS = [
  { id: "nvidia/nemotron-3-super-120b-a12b", name: "Nemotron 3 Super (120B)", provider: "NVIDIA_NIM", description: "NVIDIA 120B reasoning & code model.", badge: "120B NIM", defaultBaseUrl: "https://integrate.api.nvidia.com/v1" },
  { id: "gemini-2.0-flash", name: "Gemini 2.0 Flash", provider: "GOOGLE_GEMINI", description: "Google fast multimodal model.", badge: "GEMINI", defaultBaseUrl: "https://generativelanguage.googleapis.com/" },
  { id: "meta/llama-3.3-70b-instruct", name: "Llama 3.3 70B", provider: "NVIDIA_NIM", description: "Meta Llama 3.3 70B on NVIDIA NIM.", badge: "70B NIM", defaultBaseUrl: "https://integrate.api.nvidia.com/v1" }
];

const TEMPLATES = [
  {
    id: "habit_tracker",
    title: "Habit Tracker & Streaks",
    description: "Daily habits with streaks and progress.",
    html: "<div class=\"app\"><header><h1>Fire StreakFlow</h1><p>Daily Habit Tracker</p></header><div class=\"progress\"><span id=\"pct\">0%</span><div class=\"bar\"><i id=\"bar\"></i></div></div><div class=\"add\"><input id=\"habit\" placeholder=\"New habit...\"><button id=\"add\">Add</button></div><ul id=\"list\"></ul></div>",
    css: "*{box-sizing:border-box;margin:0;padding:0}body{font-family:system-ui;background:#0d1117;color:#f0f6fc;min-height:100vh;padding:20px}.app{max-width:420px;margin:0 auto}header{margin-bottom:20px}h1{font-size:22px}.progress{background:#161b22;border-radius:12px;padding:14px;margin-bottom:16px}.bar{height:8px;background:#30363d;border-radius:4px;margin-top:8px;overflow:hidden}.bar i{display:block;height:100%;width:0;background:#3fb950;transition:width .3s}.add{display:flex;gap:8px;margin-bottom:16px}input{flex:1;background:#161b22;border:1px solid #30363d;color:#fff;padding:10px;border-radius:8px}button{background:#388bfd;border:none;color:#fff;padding:10px 14px;border-radius:8px;cursor:pointer}ul{list-style:none}li{display:flex;align-items:center;gap:10px;background:#161b22;border:1px solid #30363d;padding:12px;border-radius:10px;margin-bottom:8px}li.done span{text-decoration:line-through;opacity:.5}li button{background:transparent;border:none;color:#f85149;cursor:pointer}",
    js: "const list=document.getElementById('list'),habit=document.getElementById('habit'),pct=document.getElementById('pct'),bar=document.getElementById('bar');let items=JSON.parse(localStorage.getItem('streaks')||'[]');function save(){localStorage.setItem('streaks',JSON.stringify(items));render()}function render(){list.innerHTML='';let done=0;items.forEach((it,i)=>{if(it.done)done++;const li=document.createElement('li');if(it.done)li.classList.add('done');li.innerHTML='<input type=\"checkbox\" '+(it.done?'checked':'')+' data-i=\"'+i+'\"><span>'+it.text+'</span><button data-del=\"'+i+'\">X</button>';list.appendChild(li)});const p=items.length?Math.round(done/items.length*100):0;pct.textContent=p+'%';bar.style.width=p+'%';list.querySelectorAll('input').forEach(cb=>cb.onchange=e=>{items[+e.target.dataset.i].done=e.target.checked;save()});list.querySelectorAll('[data-del]').forEach(b=>b.onclick=e=>{items.splice(+e.target.dataset.del,1);save()})}document.getElementById('add').onclick=()=>{const t=habit.value.trim();if(!t)return;items.push({text:t,done:false});habit.value='';save()};render();",
    thinking: "1. Habit list with localStorage\n2. Progress bar\n3. Dark theme"
  },
  {
    id: "arcade_pong",
    title: "Retro Neon Pong",
    description: "Classic pong with neon glow.",
    html: "<canvas id=\"c\"></canvas><div id=\"ui\"><h1>NEON PONG</h1><p>W/S and arrows · Space to start</p><p id=\"score\">0 — 0</p></div>",
    css: "*{margin:0;padding:0}body{background:#05010a;overflow:hidden;font-family:monospace;color:#0ff}#c{display:block;margin:40px auto;box-shadow:0 0 40px #0ff3}#ui{text-align:center;color:#0ff;text-shadow:0 0 10px #0ff}#score{font-size:28px;margin-top:8px}",
    js: "const c=document.getElementById('c'),x=c.getContext('2d');c.width=640;c.height=360;let p1=120,p2=120,ball={x:320,y:180,vx:4,vy:3},s1=0,s2=0,run=false;const keys={};window.onkeydown=e=>keys[e.key]=true;window.onkeyup=e=>keys[e.key]=false;window.onkeypress=e=>{if(e.code==='Space')run=true};function loop(){if(keys['w']||keys['W'])p1-=5;if(keys['s']||keys['S'])p1+=5;if(keys['ArrowUp'])p2-=5;if(keys['ArrowDown'])p2+=5;p1=Math.max(0,Math.min(300,p1));p2=Math.max(0,Math.min(300,p2));if(run){ball.x+=ball.vx;ball.y+=ball.vy;if(ball.y<0||ball.y>360)ball.vy*=-1;if(ball.x<20&&ball.y>p1&&ball.y<p1+60)ball.vx=Math.abs(ball.vx);if(ball.x>620&&ball.y>p2&&ball.y<p2+60)ball.vx=-Math.abs(ball.vx);if(ball.x<0){s2++;reset()}if(ball.x>640){s1++;reset()}}x.fillStyle='#05010a';x.fillRect(0,0,640,360);x.fillStyle='#0ff';x.shadowBlur=12;x.shadowColor='#0ff';x.fillRect(10,p1,10,60);x.fillRect(620,p2,10,60);x.beginPath();x.arc(ball.x,ball.y,8,0,Math.PI*2);x.fill();document.getElementById('score').textContent=s1+' — '+s2;requestAnimationFrame(loop)}function reset(){ball={x:320,y:180,vx:4*(Math.random()>.5?1:-1),vy:3};run=false}loop();",
    thinking: "1. Canvas pong\n2. Keyboard controls\n3. Neon style"
  },
  {
    id: "markdown_notes",
    title: "Markdown Note Studio",
    description: "Split-pane markdown with live preview.",
    html: "<div class=\"wrap\"><textarea id=\"md\" placeholder=\"# Start writing...\"></textarea><div id=\"prev\"></div></div>",
    css: "*{box-sizing:border-box;margin:0}body{background:#0d1117;color:#e6edf3;font-family:system-ui;height:100vh}.wrap{display:grid;grid-template-columns:1fr 1fr;height:100%}textarea,#prev{padding:20px;height:100%;overflow:auto}textarea{background:#010409;border:none;border-right:1px solid #30363d;color:#e6edf3;font-family:ui-monospace,monospace;font-size:14px;resize:none;outline:none}#prev{background:#0d1117}#prev h1,#prev h2{margin:12px 0 8px}#prev code{background:#161b22;padding:2px 6px;border-radius:4px}",
    js: "const md=document.getElementById('md'),prev=document.getElementById('prev');md.value=localStorage.getItem('md')||'# Hello\\n\\nWrite **markdown** here.';function render(){let t=md.value;t=t.replace(/^### (.*$)/gim,'<h3>$1</h3>').replace(/^## (.*$)/gim,'<h2>$1</h2>').replace(/^# (.*$)/gim,'<h1>$1</h1>').replace(/\\*\\*(.*?)\\*\\*/gim,'<b>$1</b>').replace(/\\*(.*?)\\*/gim,'<i>$1</i>').replace(/`(.*?)`/gim,'<code>$1</code>').replace(/\\n/g,'<br>');prev.innerHTML=t;localStorage.setItem('md',md.value)}md.oninput=render;render();",
    thinking: "1. Split editor/preview\n2. Simple markdown\n3. Autosave"
  },
  {
    id: "pomodoro_timer",
    title: "Pomodoro Timer",
    description: "25/5 focus cycles with session counter.",
    html: "<div class=\"card\"><h1>Pomodoro</h1><div id=\"time\">25:00</div><div class=\"mode\" id=\"mode\">FOCUS</div><div class=\"btns\"><button id=\"start\">Start</button><button id=\"reset\">Reset</button></div><p>Sessions: <span id=\"sess\">0</span></p></div>",
    css: "body{background:#0b0f17;color:#f0f6fc;font-family:system-ui;display:grid;place-items:center;min-height:100vh}.card{background:#131a26;border:1px solid #2b3648;border-radius:16px;padding:32px;text-align:center;width:300px}#time{font-size:56px;font-weight:700;margin:16px 0;font-variant-numeric:tabular-nums}.mode{color:#58a6ff;letter-spacing:2px;font-size:13px;margin-bottom:20px}.btns{display:flex;gap:10px;justify-content:center}button{background:#388bfd;border:none;color:#fff;padding:10px 18px;border-radius:8px;cursor:pointer;font-weight:600}button#reset{background:#21262d}",
    js: "let total=25*60,left=total,timer=null,focus=true,sess=0;const el=id=>document.getElementById(id);function fmt(s){return String(Math.floor(s/60)).padStart(2,'0')+':'+String(s%60).padStart(2,'0')}function tick(){if(left<=0){if(focus){sess++;el('sess').textContent=sess;focus=false;left=5*60;el('mode').textContent='BREAK'}else{focus=true;left=25*60;el('mode').textContent='FOCUS'}}left--;el('time').textContent=fmt(left)}el('start').onclick=()=>{if(timer){clearInterval(timer);timer=null;el('start').textContent='Start'}else{timer=setInterval(tick,1000);el('start').textContent='Pause'}};el('reset').onclick=()=>{clearInterval(timer);timer=null;focus=true;left=25*60;el('time').textContent='25:00';el('mode').textContent='FOCUS';el('start').textContent='Start'};",
    thinking: "1. 25/5 cycles\n2. Start/pause/reset\n3. Session count"
  }
];

const DEFAULT_NVIDIA_KEY = "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";
const DEFAULT_NVIDIA_URL = "https://integrate.api.nvidia.com/v1";

const state = {
  selectedModel: MODELS[0],
  nvidiaKey: localStorage.getItem("nvidiaKey") || DEFAULT_NVIDIA_KEY,
  nvidiaUrl: localStorage.getItem("nvidiaUrl") || DEFAULT_NVIDIA_URL,
  geminiKey: localStorage.getItem("geminiKey") || "",
  html: "",
  css: "",
  js: "",
  thinking: "",
  activeFile: "html",
  versions: [],
  generating: false,
  abort: null
};

const $ = (s) => document.querySelector(s);
const $$ = (s) => document.querySelectorAll(s);

function setStatus(t) { $("#statusText").textContent = t; }

function updateModelChip() {
  $("#modelBadge").textContent = state.selectedModel.badge;
  $("#modelName").textContent = state.selectedModel.name.split(" (")[0];
}

function showPanel(name) {
  $$(".tab").forEach(t => t.classList.toggle("active", t.dataset.tab === name));
  $$(".panel").forEach(p => p.classList.toggle("active", p.id === "panel-" + name));
}

function showCodeFile(file) {
  state.activeFile = file;
  $$(".code-tab").forEach(t => t.classList.toggle("active", t.dataset.file === file));
  $("#codeEditor").value = state[file] || "";
}

function refreshPreview() {
  const body = state.html || "";
  const doc = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style>" + (state.css || "") + "</style></head><body>" + body + "<script>" + (state.js || "") + "<\/script></body></html>";
  $("#previewFrame").srcdoc = doc;
}

function loadTemplate(t) {
  state.html = t.html;
  state.css = t.css;
  state.js = t.js;
  state.thinking = t.thinking || "";
  $("#thinkingLog").textContent = state.thinking;
  showCodeFile(state.activeFile);
  refreshPreview();
  pushVersion("Template: " + t.title);
  setStatus("Loaded " + t.title);
  $("#modalTemplates").classList.add("hidden");
}

function pushVersion(label) {
  state.versions.unshift({
    id: Date.now(),
    label: label,
    html: state.html,
    css: state.css,
    js: state.js,
    thinking: state.thinking,
    time: new Date().toLocaleString()
  });
  renderVersions();
}

function renderVersions() {
  const box = $("#versionsList");
  if (!state.versions.length) {
    box.innerHTML = "<p class=\"muted\">Versions will appear here after you generate.</p>";
    return;
  }
  box.innerHTML = state.versions.map(function(v, i) {
    return "<div class=\"version-item\" data-v=\"" + i + "\"><strong>" + v.label + "</strong><br><span class=\"muted\">" + v.time + "</span></div>";
  }).join("");
  box.querySelectorAll(".version-item").forEach(function(el) {
    el.onclick = function() {
      var v = state.versions[+el.dataset.v];
      state.html = v.html; state.css = v.css; state.js = v.js; state.thinking = v.thinking;
      $("#thinkingLog").textContent = v.thinking || "";
      showCodeFile(state.activeFile);
      refreshPreview();
      setStatus("Restored version");
    };
  });
}

function renderTemplates() {
  $("#templatesGrid").innerHTML = TEMPLATES.map(function(t) {
    return "<div class=\"template-card\" data-id=\"" + t.id + "\"><h3>" + t.title + "</h3><p>" + t.description + "</p></div>";
  }).join("");
  $$(".template-card").forEach(function(card) {
    card.onclick = function() {
      loadTemplate(TEMPLATES.find(function(t) { return t.id === card.dataset.id; }));
    };
  });
}

function renderModels() {
  $("#modelsList").innerHTML = MODELS.map(function(m) {
    var active = m.id === state.selectedModel.id ? " active" : "";
    return "<div class=\"model-card" + active + "\" data-id=\"" + m.id + "\"><h3>" + m.name + " <span class=\"badge\">" + m.badge + "</span></h3><p>" + m.description + "</p></div>";
  }).join("");
  $$(".model-card").forEach(function(card) {
    card.onclick = function() {
      state.selectedModel = MODELS.find(function(m) { return m.id === card.dataset.id; });
      updateModelChip();
      renderModels();
      $("#modalModels").classList.add("hidden");
    };
  });
}

function buildSystemPrompt() {
  return "You are an expert web app builder. Respond with ONLY a JSON object (no markdown) with keys: thinking (string), html (string body markup), css (string), js (string). Make a polished self-contained interactive web app. Vanilla JS only. Dark theme preferred.";
}

async function callNvidia(prompt) {
  var base = (state.nvidiaUrl || DEFAULT_NVIDIA_URL).replace(/\/$/, "");
  var res = await fetch(base + "/chat/completions", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + state.nvidiaKey
    },
    body: JSON.stringify({
      model: state.selectedModel.id,
      messages: [
        { role: "system", content: buildSystemPrompt() },
        { role: "user", content: prompt }
      ],
      temperature: 0.4,
      max_tokens: 4096
    }),
    signal: state.abort ? state.abort.signal : undefined
  });
  if (!res.ok) {
    var t = await res.text();
    throw new Error("NVIDIA API " + res.status + ": " + t.slice(0, 180));
  }
  var data = await res.json();
  return (data.choices && data.choices[0] && data.choices[0].message && data.choices[0].message.content) || "";
}

async function callGemini(prompt) {
  if (!state.geminiKey) throw new Error("Add a Gemini API key in Settings");
  var url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + encodeURIComponent(state.geminiKey);
  var res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      contents: [{ role: "user", parts: [{ text: buildSystemPrompt() + "\n\nUser request: " + prompt }] }],
      generationConfig: { temperature: 0.4, maxOutputTokens: 4096 }
    }),
    signal: state.abort ? state.abort.signal : undefined
  });
  if (!res.ok) {
    var t = await res.text();
    throw new Error("Gemini API " + res.status + ": " + t.slice(0, 180));
  }
  var data = await res.json();
  var parts = data.candidates && data.candidates[0] && data.candidates[0].content && data.candidates[0].content.parts;
  return parts ? parts.map(function(p) { return p.text; }).join("") : "";
}

function parseAIResponse(raw) {
  var text = (raw || "").trim();
  var fence = text.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (fence) text = fence[1].trim();
  try {
    return JSON.parse(text);
  } catch (e) {
    return { thinking: text.slice(0, 800), html: "", css: "", js: "" };
  }
}

async function generate() {
  var prompt = $("#promptInput").value.trim();
  if (!prompt || state.generating) return;
  state.generating = true;
  state.abort = new AbortController();
  $("#btnGenerate").classList.add("hidden");
  $("#btnStop").classList.remove("hidden");
  setStatus("Thinking…");
  $("#thinkingLog").textContent = "Planning architecture…\n";

  try {
    var raw;
    if (state.selectedModel.provider === "GOOGLE_GEMINI") {
      raw = await callGemini(prompt);
    } else {
      raw = await callNvidia(prompt);
    }
    setStatus("Parsing…");
    var parsed = parseAIResponse(raw);
    if (parsed.thinking) {
      state.thinking = parsed.thinking;
      $("#thinkingLog").textContent = parsed.thinking;
    }
    if (parsed.html) state.html = parsed.html;
    if (parsed.css) state.css = parsed.css;
    if (parsed.js) state.js = parsed.js;
    showCodeFile(state.activeFile);
    refreshPreview();
    pushVersion(prompt.slice(0, 48));
    setStatus("Done");
    showPanel("preview");
  } catch (e) {
    if (e.name === "AbortError") setStatus("Stopped");
    else {
      setStatus("Error");
      $("#thinkingLog").textContent = "Error: " + e.message + "\n\nCheck API keys in Settings or try another model.";
      showPanel("thinking");
    }
  } finally {
    state.generating = false;
    state.abort = null;
    $("#btnGenerate").classList.remove("hidden");
    $("#btnStop").classList.add("hidden");
  }
}

function boot() {
  updateModelChip();
  showCodeFile("html");
  loadTemplate(TEMPLATES[0]);
  setStatus("Ready");

  $$(".tab").forEach(function(t) {
    t.onclick = function() { showPanel(t.dataset.tab); };
  });
  $$(".code-tab").forEach(function(t) {
    t.onclick = function() { showCodeFile(t.dataset.file); };
  });
  $$(".vp-btn").forEach(function(b) {
    b.onclick = function() {
      $$(".vp-btn").forEach(function(x) { x.classList.remove("active"); });
      b.classList.add("active");
      $("#previewWrap").className = "preview-frame-wrap " + b.dataset.vp;
    };
  });

  $("#codeEditor").addEventListener("input", function() {
    state[state.activeFile] = $("#codeEditor").value;
    refreshPreview();
  });

  $("#btnGenerate").onclick = generate;
  $("#btnStop").onclick = function() { if (state.abort) state.abort.abort(); };

  $("#btnTemplates").onclick = function() { renderTemplates(); $("#modalTemplates").classList.remove("hidden"); };
  $("#btnModels").onclick = function() { renderModels(); $("#modalModels").classList.remove("hidden"); };
  $("#btnSettings").onclick = function() {
    $("#nvidiaKey").value = state.nvidiaKey;
    $("#nvidiaUrl").value = state.nvidiaUrl;
    $("#geminiKey").value = state.geminiKey;
    $("#modalSettings").classList.remove("hidden");
  };
  $("#btnExport").onclick = function() { $("#modalExport").classList.remove("hidden"); };

  $$(".close-modal").forEach(function(b) {
    b.onclick = function() { $("#" + b.dataset.close).classList.add("hidden"); };
  });
  $$(".modal").forEach(function(m) {
    m.onclick = function(e) { if (e.target === m) m.classList.add("hidden"); };
  });

  $("#btnSaveSettings").onclick = function() {
    state.nvidiaKey = $("#nvidiaKey").value.trim() || DEFAULT_NVIDIA_KEY;
    state.nvidiaUrl = $("#nvidiaUrl").value.trim() || DEFAULT_NVIDIA_URL;
    state.geminiKey = $("#geminiKey").value.trim();
    localStorage.setItem("nvidiaKey", state.nvidiaKey);
    localStorage.setItem("nvidiaUrl", state.nvidiaUrl);
    localStorage.setItem("geminiKey", state.geminiKey);
    $("#modalSettings").classList.add("hidden");
    setStatus("Settings saved");
  };

  $("#btnDownloadHtml").onclick = function() {
    var doc = "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n<style>\n" + state.css + "\n</style>\n</head>\n<body>\n" + state.html + "\n<script>\n" + state.js + "\n<\/script>\n</body>\n</html>";
    var blob = new Blob([doc], { type: "text/html" });
    var a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "ai-builder-export.html";
    a.click();
  };

  $("#btnCopyAll").onclick = async function() {
    var text = "/* HTML */\n" + state.html + "\n\n/* CSS */\n" + state.css + "\n\n/* JS */\n" + state.js;
    await navigator.clipboard.writeText(text);
    setStatus("Copied to clipboard");
  };
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", boot);
} else {
  boot();
}
