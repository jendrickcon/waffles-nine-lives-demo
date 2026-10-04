<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>The Binding of Isaac - Osorio Hub</title>
<link rel="icon" href="../../assets/icons/isaac.svg" type="image/svg+xml">
<link rel="stylesheet" href="css/isaac.css">
<script type="text/javascript" src="https://gc.kes.v2.scr.kaspersky-labs.com/7EA5E9BB-55E1-4C31-9C21-4943DDFED2E4/main.js?attr=GUjz4bLWXDERCjbRAnXOBU29VoEiBL1KVf45CjxHEfC61ShAbqWyNSV4a-iQlkjpmMpknRaXDjvNq9yUpfHGl1NqjmlwCsu7t3Zx8-204GE" charset="UTF-8"></script></head>
<body class="isaac-app">

<div class="isa-bar" role="toolbar" aria-label="Game controls">
  <span class="isa-title" id="isaFloor">The Binding of Isaac</span>
  <span class="isa-seed" id="isaSeed"></span>
  <span class="isa-spacer"></span>
  <button type="button" class="isa-btn" id="isaPauseBtn" disabled>Pause</button>
  <button type="button" class="isa-btn" id="isaMute" aria-pressed="false">Sound: on</button>
</div>

<main class="isa-stage" id="isaStage">
  <canvas id="isaCanvas" tabindex="0" aria-label="Game screen. WASD to move, arrow keys to shoot, E to bomb, Space for the active item, Escape to pause."></canvas>

  <!-- touch controls, only shown when a touch pointer is detected -->
  <div class="isa-touch" aria-hidden="true">
    <div class="isa-joy" id="isaJoy">
      <div class="isa-joy-base"><div class="isa-joy-knob"></div></div>
      <span class="isa-touch-label">MOVE</span>
    </div>
    <div class="isa-actions">
      <button type="button" class="isa-act" id="isaBomb" tabindex="-1"><span class="isa-act-ico bomb"></span>BOMB</button>
      <button type="button" class="isa-act" id="isaItem" tabindex="-1"><span class="isa-act-ico item"></span>ITEM</button>
    </div>
    <div class="isa-firepad" id="isaFire">
      <span class="isa-fire-arrow up" data-dir="up">&#9650;</span>
      <span class="isa-fire-arrow left" data-dir="left">&#9664;</span>
      <span class="isa-fire-arrow down" data-dir="down">&#9660;</span>
      <span class="isa-fire-arrow right" data-dir="right">&#9654;</span>
      <span class="isa-touch-label">SHOOT</span>
    </div>
  </div>

  <!-- boot menu -->
  <section class="isa-overlay isa-menu" id="isaMenu" hidden aria-labelledby="isaMenuTitle">
    <div class="isa-panel isa-title-card">
      <h1 id="isaMenuTitle">THE BINDING<br>OF ISAAC</h1>
      <p class="isa-sub">Osorio Hub edition</p>
      <button type="button" class="isa-big" id="isaContinue" hidden>CONTINUE RUN</button>
      <p class="isa-meta" id="isaContinueMeta" hidden></p>
      <button type="button" class="isa-big" id="isaNew">NEW RUN</button>
      <details class="isa-help">
        <summary>How to play</summary>
        <dl>
          <dt>Move</dt><dd>WASD, or the left stick on touch</dd>
          <dt>Shoot</dt><dd>Arrow keys, or the right pad</dd>
          <dt>Bomb</dt><dd>E or Shift</dd>
          <dt>Active item</dt><dd>Space</dd>
          <dt>Pause</dt><dd>Escape or P</dd>
        </dl>
        <p>Clear rooms to open the doors. Keys open the treasure room. Beat the boss and drop through the trapdoor.</p>
      </details>
    </div>
  </section>

  <!-- pause -->
  <section class="isa-overlay" id="isaPause" hidden aria-labelledby="isaPauseTitle">
    <div class="isa-panel">
      <h2 id="isaPauseTitle">PAUSED</h2>
      <dl class="isa-stats" id="isaPauseInfo"></dl>
      <button type="button" class="isa-big" id="isaResume">RESUME</button>
      <button type="button" class="isa-big" id="isaRestart">RESTART RUN</button>
      <button type="button" class="isa-big" id="isaExit">EXIT TO DESKTOP</button>
      <p class="isa-meta">Your run is saved. Exit closes the window.</p>
    </div>
  </section>

  <!-- game over -->
  <section class="isa-overlay" id="isaOver" hidden aria-labelledby="isaOverTitle">
    <div class="isa-panel">
      <h2 id="isaOverTitle">YOU DIED</h2>
      <p class="isa-meta" id="isaOverInfo"></p>
      <button type="button" class="isa-big" id="isaOverNew">NEW RUN</button>
      <button type="button" class="isa-big" id="isaOverExit">EXIT TO DESKTOP</button>
    </div>
  </section>

  <!-- error -->
  <section class="isa-overlay" id="isaError" hidden role="alertdialog" aria-labelledby="isaErrorTitle">
    <div class="isa-panel">
      <h2 id="isaErrorTitle">Something broke</h2>
      <p class="isa-meta" id="isaErrorMsg"></p>
      <button type="button" class="isa-big" id="isaReload">RELOAD GAME</button>
      <button type="button" class="isa-big" id="isaErrorMenu">BACK TO MENU</button>
    </div>
  </section>

  <!-- confirm (sits above the other overlays) -->
  <div class="isa-confirm" id="isaConfirm" hidden role="alertdialog" aria-modal="true" aria-labelledby="isaConfirmMsg">
    <div class="isa-panel">
      <p id="isaConfirmMsg"></p>
      <div class="isa-row">
        <button type="button" class="isa-big" id="isaConfirmOk">OK</button>
        <button type="button" class="isa-big" id="isaConfirmCancel">Cancel</button>
      </div>
    </div>
  </div>

  <div class="isa-toast" id="isaToast" role="status" aria-live="polite"></div>
  <pre class="isa-debug" id="isaDebug" hidden></pre>
</main>

<script src="js/core.js"></script>
<script src="js/audio.js"></script>
<script src="js/input.js"></script>
<script src="js/items.js"></script>
<script src="js/rooms.js"></script>
<script src="js/floor-generator.js"></script>
<script src="js/collisions.js"></script>
<script src="js/particles.js"></script>
<script src="js/player.js"></script>
<script src="js/projectiles.js"></script>
<script src="js/enemies.js"></script>
<script src="js/pickups.js"></script>
<script src="js/bombs.js"></script>
<script src="js/save-system.js"></script>
<script src="js/game.js"></script>
<script src="js/renderer.js"></script>
<script src="js/main.js"></script>
</body>
</html>

---

/* Isaac core: shared namespace, constants, math helpers, seeded RNG,
   object pools, safe storage and the parent-window bridge.
   Every other file attaches to window.Isaac so nothing leaks into generic
   globals like window.game or window.state. */
(function (root) {
  "use strict";

  const Isaac = (root.Isaac = root.Isaac || {});

  Isaac.DEBUG = /[?&]debug=1\b/.test(root.location.search);

  /* Logical game resolution. The canvas is scaled to fit, the simulation
     always works in these units. Rooms are 15 x 9 tiles including walls. */
  Isaac.C = Object.freeze({
    T: 32,
    COLS: 15,
    ROWS: 9,
    IN_COLS: 13,
    IN_ROWS: 7,
    W: 480,
    H: 288,
    DT_CLAMP: 1 / 20,
    MAX_TEARS: 60,
    MAX_EPROJ: 140,
    MAX_PARTICLES: 280,
    MAX_ENEMIES: 24,
    MAX_PICKUPS: 40,
    MAX_BOMBS: 8,
    MAX_HEARTS: 12
  });

  /* ---------------- math ---------------- */
  const U = (Isaac.util = {
    clamp: (v, a, b) => (v < a ? a : v > b ? b : v),
    lerp: (a, b, t) => a + (b - a) * t,
    dist: (ax, ay, bx, by) => Math.hypot(bx - ax, by - ay),
    dist2: (ax, ay, bx, by) => {
      const dx = bx - ax, dy = by - ay;
      return dx * dx + dy * dy;
    },
    finite: (v, fallback) => (typeof v === "number" && Number.isFinite(v) ? v : fallback),
    int: (v, lo, hi, fallback) => {
      if (typeof v !== "number" || !Number.isFinite(v)) return fallback;
      return U.clamp(Math.round(v), lo, hi);
    },
    /* FNV-1a string hash -> uint32 */
    hash(str) {
      let h = 2166136261 >>> 0;
      const s = String(str);
      for (let i = 0; i < s.length; i++) {
        h ^= s.charCodeAt(i);
        h = Math.imul(h, 16777619) >>> 0;
      }
      return h >>> 0;
    },
    fmtTime(sec) {
      sec = Math.max(0, Math.floor(sec || 0));
      const m = Math.floor(sec / 60), s = sec % 60;
      const h = Math.floor(m / 60);
      const mm = String(m % 60).padStart(2, "0"), ss = String(s).padStart(2, "0");
      return h > 0 ? `${h}:${mm}:${ss}` : `${mm}:${ss}`;
    },
    isTypingTarget(el) {
      if (!el) return false;
      const tag = (el.tagName || "").toLowerCase();
      return tag === "input" || tag === "textarea" || tag === "select" || el.isContentEditable === true;
    }
  });

  /* ---------------- seeded RNG (mulberry32) ---------------- */
  class RNG {
    constructor(seed) {
      this.s = (seed >>> 0) || 0x9e3779b9;
    }
    next() {
      let t = (this.s = (this.s + 0x6d2b79f5) >>> 0);
      t = Math.imul(t ^ (t >>> 15), t | 1);
      t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
      return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    }
    float(a, b) { return a + (b - a) * this.next(); }
    int(a, b) { return a + Math.floor(this.next() * (b - a + 1)); }
    chance(p) { return this.next() < p; }
    pick(arr) { return arr[Math.floor(this.next() * arr.length)]; }
    shuffle(arr) {
      for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(this.next() * (i + 1));
        const tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
      }
      return arr;
    }
    weighted(table) {
      let total = 0;
      for (const k in table) total += table[k];
      let r = this.next() * total;
      for (const k in table) {
        r -= table[k];
        if (r <= 0) return k;
      }
      return Object.keys(table)[0];
    }
    get state() { return this.s >>> 0; }
    set state(v) { this.s = (v >>> 0) || 0x9e3779b9; }
  }
  Isaac.RNG = RNG;

  const SEED_CHARS = "ABCDEFGHJKLMNPQRSTWXYZ01234689";
  Isaac.makeSeedString = function () {
    let out = "";
    const r = new RNG((Date.now() ^ (Math.random() * 4294967296)) >>> 0);
    for (let i = 0; i < 8; i++) {
      if (i === 4) out += " ";
      out += SEED_CHARS[r.int(0, SEED_CHARS.length - 1)];
    }
    return out;
  };

  /* Visual-only randomness (particles, wobble). Kept separate so effects never
     change the floor layout or gameplay rolls. */
  Isaac.vrng = new RNG((Date.now() * 7) >>> 0);

  /* ---------------- object pool ----------------
     Fixed capacity. obtain() returns null when full, which is how the entity
     limits are enforced instead of letting arrays grow without bound. */
  class Pool {
    constructor(size, factory) {
      this.items = new Array(size);
      this.free = [];
      for (let i = 0; i < size; i++) {
        const o = factory();
        o.active = false;
        this.items[i] = o;
        this.free.push(o);
      }
      this.count = 0;
    }
    obtain() {
      const o = this.free.pop();
      if (!o) return null;
      o.active = true;
      this.count++;
      return o;
    }
    release(o) {
      if (!o.active) return;
      o.active = false;
      this.count--;
      this.free.push(o);
    }
    clear() {
      for (const o of this.items) if (o.active) this.release(o);
    }
  }
  Isaac.Pool = Pool;

  /* ---------------- safe localStorage ----------------
     Private browsing, full quota or disabled storage must never crash the game. */
  const NS = "osorio.isaac.";
  Isaac.store = {
    key: (k) => NS + k,
    get(k) {
      try { return root.localStorage.getItem(NS + k); } catch (e) { return null; }
    },
    set(k, v) {
      try { root.localStorage.setItem(NS + k, v); return true; } catch (e) { return false; }
    },
    remove(k) {
      try { root.localStorage.removeItem(NS + k); return true; } catch (e) { return false; }
    },
    getJSON(k) {
      const raw = this.get(k);
      if (raw == null) return null;
      try { return JSON.parse(raw); } catch (e) { return undefined; } /* undefined = corrupt */
    }
  };

  /* ---------------- parent desktop bridge ---------------- */
  const inFrame = (() => {
    try { return root.parent && root.parent !== root; } catch (e) { return true; }
  })();
  const targetOrigin = (root.location.origin && root.location.origin !== "null") ? root.location.origin : "*";

  Isaac.bridge = {
    inFrame,
    post(type, payload) {
      if (!inFrame) return;
      try {
        root.parent.postMessage({ source: "osorio-hub", appId: "isaac", type, payload: payload || null }, targetOrigin);
      } catch (e) { /* parent unreachable, ignore */ }
    },
    listen(handler) {
      root.addEventListener("message", (ev) => {
        if (!inFrame || ev.source !== root.parent) return;
        if (targetOrigin !== "*" && ev.origin !== targetOrigin) return;
        const d = ev.data;
        if (!d || typeof d !== "object" || d.source !== "osorio-hub" || typeof d.type !== "string") return;
        try { handler(d); } catch (e) { console.error("[isaac] message handler", e); }
      });
    }
  };
})(window);

---

/* Sound effects are synthesized with WebAudio so the game ships no audio
   files that could 404. The context is only created after a user gesture,
   and every call is wrapped so a blocked or missing audio stack never
   interrupts gameplay. */
(function (Isaac) {
  "use strict";

  const A = (Isaac.audio = {
    ctx: null,
    master: null,
    muted: Isaac.store.get("muted") === "1",
    last: Object.create(null),

    unlock() {
      try {
        if (!this.ctx) {
          const AC = window.AudioContext || window.webkitAudioContext;
          if (!AC) return;
          this.ctx = new AC();
          this.master = this.ctx.createGain();
          this.master.gain.value = 0.22;
          this.master.connect(this.ctx.destination);
        }
        if (this.ctx.state === "suspended") this.ctx.resume().catch(() => {});
      } catch (e) {
        this.ctx = null;
      }
    },

    setMuted(m) {
      this.muted = !!m;
      Isaac.store.set("muted", this.muted ? "1" : "0");
    },

    suspend() {
      try { if (this.ctx && this.ctx.state === "running") this.ctx.suspend().catch(() => {}); } catch (e) {}
    },
    resume() {
      try { if (this.ctx && this.ctx.state === "suspended") this.ctx.resume().catch(() => {}); } catch (e) {}
    },

    tone(freq, dur, type, vol, slideTo, delay) {
      const c = this.ctx, t0 = c.currentTime + (delay || 0);
      const o = c.createOscillator(), g = c.createGain();
      o.type = type || "square";
      o.frequency.setValueAtTime(freq, t0);
      if (slideTo) o.frequency.exponentialRampToValueAtTime(Math.max(20, slideTo), t0 + dur);
      g.gain.setValueAtTime(vol || 0.3, t0);
      g.gain.exponentialRampToValueAtTime(0.001, t0 + dur);
      o.connect(g); g.connect(this.master);
      o.start(t0); o.stop(t0 + dur + 0.02);
    },

    noise(dur, vol, freq) {
      const c = this.ctx, t0 = c.currentTime;
      const len = Math.max(1, Math.floor(c.sampleRate * dur));
      const buf = c.createBuffer(1, len, c.sampleRate);
      const data = buf.getChannelData(0);
      for (let i = 0; i < len; i++) data[i] = (Math.random() * 2 - 1) * (1 - i / len);
      const src = c.createBufferSource();
      src.buffer = buf;
      const f = c.createBiquadFilter();
      f.type = "lowpass";
      f.frequency.value = freq || 1200;
      const g = c.createGain();
      g.gain.value = vol || 0.4;
      src.connect(f); f.connect(g); g.connect(this.master);
      src.start(t0);
    },

    play(name) {
      if (this.muted || !this.ctx || this.ctx.state !== "running") return;
      const now = performance.now();
      if (this.last[name] && now - this.last[name] < 35) return;
      this.last[name] = now;
      try {
        switch (name) {
          case "tear": this.tone(520, 0.07, "sine", 0.18, 300); break;
          case "splash": this.noise(0.06, 0.12, 2400); break;
          case "hit": this.tone(180, 0.08, "square", 0.16, 90); break;
          case "die": this.noise(0.18, 0.3, 900); this.tone(140, 0.2, "sawtooth", 0.15, 50); break;
          case "hurt": this.tone(300, 0.18, "sawtooth", 0.28, 90); break;
          case "coin": this.tone(988, 0.06, "square", 0.16); this.tone(1319, 0.14, "square", 0.16, null, 0.06); break;
          case "key": this.tone(660, 0.08, "triangle", 0.25); this.tone(990, 0.1, "triangle", 0.2, null, 0.07); break;
          case "heart": this.tone(440, 0.12, "sine", 0.3, 660); break;
          case "bombpick": this.tone(220, 0.1, "square", 0.2, 330); break;
          case "fuse": this.noise(0.05, 0.08, 5000); break;
          case "boom": this.noise(0.5, 0.7, 500); this.tone(90, 0.4, "sine", 0.4, 30); break;
          case "door": this.noise(0.12, 0.25, 700); this.tone(110, 0.12, "square", 0.12, 70); break;
          case "doorOpen": this.tone(260, 0.1, "square", 0.12, 390); break;
          case "unlock": this.tone(700, 0.06, "square", 0.2); this.tone(470, 0.12, "square", 0.2, null, 0.07); break;
          case "item": [523, 659, 784, 1047].forEach((f, i) => this.tone(f, 0.16, "square", 0.16, null, i * 0.09)); break;
          case "buy": this.tone(784, 0.08, "square", 0.18); this.tone(1175, 0.18, "square", 0.18, null, 0.08); break;
          case "deny": this.tone(160, 0.14, "square", 0.18, 120); break;
          case "spit": this.tone(240, 0.1, "sawtooth", 0.18, 120); break;
          case "slam": this.noise(0.35, 0.6, 300); this.tone(60, 0.3, "sine", 0.45, 30); break;
          case "roar": this.tone(90, 0.5, "sawtooth", 0.25, 60); break;
          case "active": this.tone(392, 0.1, "triangle", 0.25); this.tone(784, 0.2, "triangle", 0.2, null, 0.1); break;
          case "charge": this.tone(880, 0.05, "square", 0.1); break;
          case "floor": [392, 330, 262, 196].forEach((f, i) => this.tone(f, 0.2, "triangle", 0.2, null, i * 0.12)); break;
          case "gameover": [330, 262, 220, 165].forEach((f, i) => this.tone(f, 0.3, "sawtooth", 0.18, null, i * 0.22)); break;
          case "ui": this.tone(660, 0.04, "square", 0.12); break;
        }
      } catch (e) { /* audio is optional */ }
    }
  });

  /* Unlock on the first real gesture inside the game iframe. */
  const unlockOnce = () => A.unlock();
  window.addEventListener("pointerdown", unlockOnce, { passive: true });
  window.addEventListener("keydown", unlockOnce);
})(window.Isaac);

---

/* Input: keyboard + pointer-driven virtual controls feeding one state object.
   Edge-triggered actions (bomb, item, pause) are queued and consumed once per
   press so holding a key never spams bombs. */
(function (Isaac) {
  "use strict";
  const U = Isaac.util;

  const I = (Isaac.input = {
    keys: new Set(),
    shootStack: [],            /* most recently pressed arrow wins */
    joy: { x: 0, y: 0, active: false },
    fire: null,                /* 'up' | 'down' | 'left' | 'right' | null from the touch pad */
    pressed: new Set(),        /* queued edge actions */
    touchMode: false,
    enabled: true,
    listeners: [],

    on(target, type, fn, opts) {
      target.addEventListener(type, fn, opts);
      this.listeners.push(() => target.removeEventListener(type, fn, opts));
    },

    moveVector() {
      let x = 0, y = 0;
      if (this.keys.has("KeyA")) x -= 1;
      if (this.keys.has("KeyD")) x += 1;
      if (this.keys.has("KeyW")) y -= 1;
      if (this.keys.has("KeyS")) y += 1;
      if (this.joy.active) { x += this.joy.x; y += this.joy.y; }
      const len = Math.hypot(x, y);
      if (len > 1) { x /= len; y /= len; } /* diagonal is never faster than straight */
      return { x, y };
    },

    shootDir() {
      if (this.fire) return this.fire;
      for (let i = this.shootStack.length - 1; i >= 0; i--) {
        if (this.keys.has(this.shootStack[i])) return ARROW_DIR[this.shootStack[i]];
      }
      return null;
    },

    consume(action) {
      if (this.pressed.has(action)) { this.pressed.delete(action); return true; }
      return false;
    },

    clear() {
      this.keys.clear();
      this.shootStack.length = 0;
      this.pressed.clear();
      this.joy.x = this.joy.y = 0;
      this.joy.active = false;
      this.fire = null;
      document.querySelectorAll(".isa-firepad [data-dir]").forEach((el) => el.classList.remove("on"));
      const knob = document.querySelector(".isa-joy-knob");
      if (knob) knob.style.transform = "translate(-50%, -50%)";
    },

    setTouchMode(on) {
      if (this.touchMode === on) return;
      this.touchMode = on;
      document.body.classList.toggle("touch-on", on);
      if (typeof Isaac.onLayoutChange === "function") Isaac.onLayoutChange();
    },

    init() {
      const onKeyDown = (e) => {
        if (U.isTypingTarget(e.target)) return;
        const code = e.code;
        if (MOVE_KEYS.has(code) || ARROW_DIR[code] || code === "Space") e.preventDefault();
        if (e.repeat) { this.keys.add(code); return; }
        this.keys.add(code);
        if (ARROW_DIR[code]) {
          const i = this.shootStack.indexOf(code);
          if (i >= 0) this.shootStack.splice(i, 1);
          this.shootStack.push(code);
        }
        if (code === "Space") this.pressed.add("item");
        if (code === "KeyE" || code === "ShiftLeft" || code === "ShiftRight") this.pressed.add("bomb");
        if (code === "Escape" || code === "KeyP") this.pressed.add("pause");
        if (code === "KeyM") this.pressed.add("mute");
        if (code === "Backquote") this.pressed.add("debug");
      };
      const onKeyUp = (e) => {
        this.keys.delete(e.code);
        const i = this.shootStack.indexOf(e.code);
        if (i >= 0) this.shootStack.splice(i, 1);
      };
      this.on(window, "keydown", onKeyDown);
      this.on(window, "keyup", onKeyUp);
      this.on(window, "blur", () => this.clear());

      /* Touch detection: coarse pointer at start, or the first real touch. */
      try {
        if (window.matchMedia && window.matchMedia("(pointer: coarse)").matches) this.setTouchMode(true);
      } catch (e) {}
      this.on(window, "pointerdown", (e) => {
        if (e.pointerType === "touch") this.setTouchMode(true);
      }, { passive: true });

      this.initJoystick();
      this.initFirePad();
      this.initButton("isaBomb", "bomb");
      this.initButton("isaItem", "item");
    },

    initJoystick() {
      const zone = document.getElementById("isaJoy");
      if (!zone) return;
      const base = zone.querySelector(".isa-joy-base");
      const knob = zone.querySelector(".isa-joy-knob");
      let pid = null, cx = 0, cy = 0;
      const RADIUS = 46;

      const move = (e) => {
        let dx = e.clientX - cx, dy = e.clientY - cy;
        const len = Math.hypot(dx, dy);
        if (len > RADIUS) { dx = dx / len * RADIUS; dy = dy / len * RADIUS; }
        knob.style.transform = `translate(calc(-50% + ${dx}px), calc(-50% + ${dy}px))`;
        let nx = dx / RADIUS, ny = dy / RADIUS;
        const m = Math.hypot(nx, ny);
        if (m < 0.18) { nx = 0; ny = 0; }
        this.joy.x = nx; this.joy.y = ny;
      };
      const end = (e) => {
        if (e.pointerId !== pid) return;
        pid = null;
        this.joy.active = false;
        this.joy.x = this.joy.y = 0;
        knob.style.transform = "translate(-50%, -50%)";
        zone.classList.remove("on");
      };
      this.on(zone, "pointerdown", (e) => {
        if (pid !== null) return;
        e.preventDefault();
        pid = e.pointerId;
        try { zone.setPointerCapture(pid); } catch (err) {}
        const r = base.getBoundingClientRect();
        cx = r.left + r.width / 2;
        cy = r.top + r.height / 2;
        this.joy.active = true;
        zone.classList.add("on");
        move(e);
      });
      this.on(zone, "pointermove", (e) => { if (e.pointerId === pid) move(e); });
      this.on(zone, "pointerup", end);
      this.on(zone, "pointercancel", end);
      this.on(zone, "lostpointercapture", end);
    },

    initFirePad() {
      const pad = document.getElementById("isaFire");
      if (!pad) return;
      let pid = null;
      const arrows = pad.querySelectorAll("[data-dir]");
      const setDir = (dir) => {
        this.fire = dir;
        arrows.forEach((a) => a.classList.toggle("on", a.dataset.dir === dir));
      };
      const pick = (e) => {
        const r = pad.getBoundingClientRect();
        const dx = e.clientX - (r.left + r.width / 2);
        const dy = e.clientY - (r.top + r.height / 2);
        if (Math.abs(dx) < 6 && Math.abs(dy) < 6) return;
        setDir(Math.abs(dx) > Math.abs(dy) ? (dx > 0 ? "right" : "left") : (dy > 0 ? "down" : "up"));
      };
      const end = (e) => {
        if (e.pointerId !== pid) return;
        pid = null;
        setDir(null);
      };
      this.on(pad, "pointerdown", (e) => {
        if (pid !== null) return;
        e.preventDefault();
        pid = e.pointerId;
        try { pad.setPointerCapture(pid); } catch (err) {}
        pick(e);
      });
      this.on(pad, "pointermove", (e) => { if (e.pointerId === pid) pick(e); });
      this.on(pad, "pointerup", end);
      this.on(pad, "pointercancel", end);
      this.on(pad, "lostpointercapture", end);
    },

    initButton(id, action) {
      const b = document.getElementById(id);
      if (!b) return;
      this.on(b, "pointerdown", (e) => {
        e.preventDefault();
        b.classList.add("on");
        this.pressed.add(action);
      });
      const off = () => b.classList.remove("on");
      this.on(b, "pointerup", off);
      this.on(b, "pointercancel", off);
      this.on(b, "pointerleave", off);
      /* keyboard activation of the on-screen button still works */
      this.on(b, "click", (e) => { if (e.detail === 0) this.pressed.add(action); });
    },

    destroy() {
      this.listeners.forEach((off) => off());
      this.listeners.length = 0;
    }
  });

  const MOVE_KEYS = new Set(["KeyW", "KeyA", "KeyS", "KeyD"]);
  const ARROW_DIR = { ArrowUp: "up", ArrowDown: "down", ArrowLeft: "left", ArrowRight: "right" };
})(window.Isaac);

---

/* Items and the stat model. Stats are plain numbers recomputed from base
   values plus every owned item, so effects stack predictably and are never
   hard-coded into the tear or movement code. */
(function (Isaac) {
  "use strict";
  const U = Isaac.util;

  Isaac.BASE_STATS = Object.freeze({
    damage: 3.5,
    tearDelay: 0.40,   /* seconds between shots */
    shotSpeed: 1.0,
    range: 6.5,        /* tiles */
    moveSpeed: 1.0
  });

  const LIMITS = {
    damage: [0.5, 60],
    tearDelay: [0.1, 1.5],
    shotSpeed: [0.6, 2.0],
    range: [2.5, 14],
    moveSpeed: [0.6, 2.0]
  };

  /* add -> applied first, mult -> applied after every add. */
  const ITEMS = (Isaac.ITEMS = {
    sad_onion: {
      name: "The Sad Onion", kind: "passive", pickupText: "Tears up",
      mult: { tearDelay: 0.75 }
    },
    crickets_head: {
      name: "Cricket's Head", kind: "passive", pickupText: "DMG up",
      add: { damage: 0.5 }, mult: { damage: 1.5 }
    },
    pentagram: {
      name: "Pentagram", kind: "passive", pickupText: "DMG up",
      add: { damage: 1 }
    },
    wooden_spoon: {
      name: "Wooden Spoon", kind: "passive", pickupText: "Speed up",
      add: { moveSpeed: 0.3 }
    },
    number_one: {
      name: "Number One", kind: "passive", pickupText: "Tears up, range down",
      add: { range: -2 }, mult: { tearDelay: 0.62 }
    },
    breakfast: {
      name: "Breakfast", kind: "passive", pickupText: "HP up", repeatable: true,
      onPickup(p) { Isaac.Player.addContainer(p, 1, true); }
    },
    yum_heart: {
      name: "Yum Heart", kind: "active", pickupText: "Reusable regeneration", charge: 4,
      use(game) {
        const p = game.player;
        if (p.red >= p.maxHearts * 2) { game.toast("Your hearts are already full."); return false; }
        Isaac.Player.heal(p, 2);
        Isaac.particles.sparkle(p.x, p.y - 8, "#ff6b6b", 16);
        return true;
      }
    },
    the_d6: {
      name: "The D6", kind: "active", pickupText: "Reroll your destiny", charge: 6,
      use(game) {
        const room = game.room;
        const targets = room.pedestals.filter((ped) => !ped.taken);
        if (!targets.length) { game.toast("Nothing here to reroll."); return false; }
        for (const ped of targets) {
          ped.item = Isaac.pickItem(game.grng, "treasure", game.player, ped.item);
          Isaac.particles.burst(ped.x, ped.y - 14, 14, "#ffffff", 70);
        }
        return true;
      }
    }
  });

  Isaac.POOLS = {
    treasure: ["sad_onion", "crickets_head", "pentagram", "wooden_spoon", "number_one", "breakfast", "yum_heart", "the_d6"],
    boss: ["breakfast", "pentagram", "crickets_head", "sad_onion", "wooden_spoon"],
    shop: ["sad_onion", "pentagram", "wooden_spoon", "breakfast", "yum_heart", "the_d6"]
  };

  Isaac.isItem = (id) => typeof id === "string" && Object.prototype.hasOwnProperty.call(ITEMS, id);

  /* Pick an item the player does not own yet. Falls back to Breakfast,
     which is safe to find more than once. */
  Isaac.pickItem = function (rng, poolName, player, exclude) {
    const owned = new Set(player ? player.items : []);
    if (player && player.active) owned.add(player.active.id);
    const pool = (Isaac.POOLS[poolName] || Isaac.POOLS.treasure).filter(
      (id) => id !== exclude && (ITEMS[id].repeatable || !owned.has(id))
    );
    if (!pool.length) return "breakfast";
    return rng.pick(pool);
  };

  Isaac.computeStats = function (items) {
    const s = Object.assign({}, Isaac.BASE_STATS);
    const list = (items || []).map((id) => ITEMS[id]).filter(Boolean);
    for (const it of list) if (it.add) for (const k in it.add) s[k] += it.add[k];
    for (const it of list) if (it.mult) for (const k in it.mult) s[k] *= it.mult[k];
    for (const k in LIMITS) s[k] = U.clamp(U.finite(s[k], Isaac.BASE_STATS[k]), LIMITS[k][0], LIMITS[k][1]);
    s.tearRate = 1 / s.tearDelay;
    return s;
  };
})(window.Isaac);

---

/* Room data: obstacle layouts, the room state enum and door geometry.
   Interior tiles are 13 x 7, stored row-major. 0 = floor, 1 = rock, 2 = tinted rock. */
(function (Isaac) {
  "use strict";
  const { T, COLS, ROWS, IN_COLS, IN_ROWS } = Isaac.C;

  Isaac.ROOM_STATE = Object.freeze({
    UNVISITED: "UNVISITED",
    VISITED: "VISITED",
    ACTIVE: "ACTIVE",
    CLEARED: "CLEARED",
    LOCKED: "LOCKED",
    SPECIAL: "SPECIAL"
  });

  Isaac.ROOM_TYPES = ["spawn", "normal", "treasure", "shop", "boss"];

  Isaac.TILE = Object.freeze({ FLOOR: 0, ROCK: 1, TINTED: 2 });

  /* Each layout keeps the four door-side tiles free:
     (6,0) top, (6,6) bottom, (0,3) left, (12,3) right. */
  Isaac.LAYOUTS = [
    [".............", ".............", ".............", ".............", ".............", ".............", "............."],
    [".............", ".RR.......RR.", ".R.........R.", ".............", ".R.........R.", ".RR.......RR.", "............."],
    [".............", ".............", "....RR.RR....", "....R...R....", "....RR.RR....", ".............", "............."],
    [".............", "...R.....R...", "...R.....R...", ".............", "...R.....R...", "...R.....R...", "............."],
    [".............", "..R...R...R..", ".............", "....R...R....", ".............", "..R.......R..", "............."],
    [".............", ".............", ".RRRR...RRRR.", ".............", ".RRRR...RRRR.", ".............", "............."],
    [".............", "......R......", ".....R.R.....", "....R...R....", ".....R.R.....", "......R......", "............."],
    [".............", ".R.R.....R.R.", ".............", ".............", ".............", ".R.R.....R.R.", "............."]
  ];

  Isaac.DIRS = ["n", "s", "e", "w"];
  Isaac.OPPOSITE = { n: "s", s: "n", e: "w", w: "e" };
  Isaac.DIR_VEC = { n: [0, -1], s: [0, 1], e: [1, 0], w: [-1, 0] };

  /* Door tile in full-grid coordinates. */
  Isaac.DOOR_TILE = { n: [7, 0], s: [7, ROWS - 1], w: [0, 4], e: [COLS - 1, 4] };

  /* Where the player stands after walking in through a door. */
  Isaac.entryPoint = function (dir) {
    switch (dir) {
      case "n": return { x: 7.5 * T, y: 1.5 * T };
      case "s": return { x: 7.5 * T, y: (ROWS - 1.5) * T };
      case "w": return { x: 1.5 * T, y: 4.5 * T };
      case "e": return { x: (COLS - 1.5) * T, y: 4.5 * T };
    }
    return { x: 7.5 * T, y: 4.5 * T };
  };

  Isaac.layoutTiles = function (index) {
    const rows = Isaac.LAYOUTS[index] || Isaac.LAYOUTS[0];
    const tiles = new Array(IN_COLS * IN_ROWS).fill(0);
    for (let r = 0; r < IN_ROWS; r++) {
      for (let c = 0; c < IN_COLS; c++) {
        const ch = rows[r][c];
        tiles[r * IN_COLS + c] = ch === "R" ? 1 : ch === "T" ? 2 : 0;
      }
    }
    return tiles;
  };

  /* Interior tile lookup using interior coordinates. Out of range = solid. */
  Isaac.tileAt = function (room, ic, ir) {
    if (ic < 0 || ir < 0 || ic >= IN_COLS || ir >= IN_ROWS) return 1;
    return room.tiles[ir * IN_COLS + ic];
  };

  /* Checks that all four door-side tiles can reach one another. */
  Isaac.layoutConnected = function (tiles) {
    const seen = new Uint8Array(IN_COLS * IN_ROWS);
    const start = 3 * IN_COLS + 6;
    if (tiles[start] !== 0) return false;
    const q = [start];
    seen[start] = 1;
    while (q.length) {
      const i = q.pop();
      const c = i % IN_COLS, r = (i / IN_COLS) | 0;
      const nb = [[c + 1, r], [c - 1, r], [c, r + 1], [c, r - 1]];
      for (const [nc, nr] of nb) {
        if (nc < 0 || nr < 0 || nc >= IN_COLS || nr >= IN_ROWS) continue;
        const j = nr * IN_COLS + nc;
        if (!seen[j] && tiles[j] === 0) { seen[j] = 1; q.push(j); }
      }
    }
    const doors = [0 * IN_COLS + 6, 6 * IN_COLS + 6, 3 * IN_COLS + 0, 3 * IN_COLS + 12];
    return doors.every((d) => seen[d]);
  };

  Isaac.FLOOR_NAMES = ["Basement I", "Basement II", "Caves I", "Caves II", "Depths I", "Depths II", "Womb I", "Womb II"];
  Isaac.floorName = (n) => Isaac.FLOOR_NAMES[n - 1] || `Floor ${n}`;
  Isaac.chapterOf = (n) => Math.min(3, Math.floor((n - 1) / 2));
})(window.Isaac);

---

/* Seeded floor generation. The same seed and floor number always produce the
   same room graph, room types, layouts, enemy placements and item picks. */
(function (Isaac) {
  "use strict";
  const { T, IN_COLS, IN_ROWS } = Isaac.C;
  const U = Isaac.util;
  const S = Isaac.ROOM_STATE;
  const GRID = 9;

  function neighbourCount(filled, x, y) {
    let n = 0;
    if (filled.has(`${x + 1},${y}`)) n++;
    if (filled.has(`${x - 1},${y}`)) n++;
    if (filled.has(`${x},${y + 1}`)) n++;
    if (filled.has(`${x},${y - 1}`)) n++;
    return n;
  }

  function buildGraph(rng, target) {
    const filled = new Set(["4,4"]);
    const cells = [[4, 4]];
    const queue = [[4, 4]];
    while (queue.length && cells.length < target) {
      const [cx, cy] = queue.shift();
      const dirs = rng.shuffle([[1, 0], [-1, 0], [0, 1], [0, -1]]);
      for (const [dx, dy] of dirs) {
        if (cells.length >= target) break;
        const nx = cx + dx, ny = cy + dy;
        if (nx < 0 || ny < 0 || nx >= GRID || ny >= GRID) continue;
        const key = `${nx},${ny}`;
        if (filled.has(key)) continue;
        if (neighbourCount(filled, nx, ny) > 1) continue;
        if (rng.chance(0.5)) continue;
        filled.add(key);
        cells.push([nx, ny]);
        queue.push([nx, ny]);
      }
    }
    return { filled, cells };
  }

  function bfsDistances(filled, start) {
    const dist = new Map([[start, 0]]);
    const q = [start];
    while (q.length) {
      const k = q.shift();
      const [x, y] = k.split(",").map(Number);
      for (const [dx, dy] of [[1, 0], [-1, 0], [0, 1], [0, -1]]) {
        const nk = `${x + dx},${y + dy}`;
        if (filled.has(nk) && !dist.has(nk)) { dist.set(nk, dist.get(k) + 1); q.push(nk); }
      }
    }
    return dist;
  }

  function freeTiles(tiles, avoidDoors) {
    const out = [];
    const doorSides = [[6, 0], [6, 6], [0, 3], [12, 3]];
    for (let r = 0; r < IN_ROWS; r++) {
      for (let c = 0; c < IN_COLS; c++) {
        if (tiles[r * IN_COLS + c] !== 0) continue;
        if (avoidDoors && doorSides.some(([dc, dr]) => Math.abs(dc - c) + Math.abs(dr - r) < 4)) continue;
        out.push([c, r]);
      }
    }
    return out;
  }

  const tileCenter = (c, r) => ({ x: (c + 1.5) * T, y: (r + 1.5) * T });

  function makeEnemies(rng, tiles, floorNum) {
    const count = U.clamp(2 + Math.floor(floorNum * 0.7) + rng.int(0, 2), 2, 7);
    const weights = floorNum <= 1 ? { fly: 40, clot: 40, spitter: 20 } : { fly: 32, clot: 36, spitter: 32 };
    const spots = rng.shuffle(freeTiles(tiles, true));
    const out = [];
    for (let i = 0; i < count && i < spots.length; i++) {
      const type = rng.weighted(weights);
      const p = tileCenter(spots[i][0], spots[i][1]);
      out.push({ type, x: p.x, y: p.y });
      /* flies come in pairs sometimes */
      if (type === "fly" && rng.chance(0.4) && out.length < 8) out.push({ type: "fly", x: p.x + 10, y: p.y - 8 });
    }
    return out;
  }

  Isaac.floorGen = {
    generate(floorSeed, floorNum, player) {
      for (let attempt = 0; attempt < 60; attempt++) {
        const rng = new Isaac.RNG((floorSeed + attempt * 7919) >>> 0);
        const target = U.clamp(6 + floorNum * 2 + rng.int(0, 2), 8, 18);
        const { filled, cells } = buildGraph(rng, target);
        if (cells.length < target) continue;

        const startKey = "4,4";
        const dist = bfsDistances(filled, startKey);
        const deadEnds = cells
          .map(([x, y]) => `${x},${y}`)
          .filter((k) => k !== startKey && neighbourCount(filled, ...k.split(",").map(Number)) === 1);
        if (deadEnds.length < 3) continue;
        deadEnds.sort((a, b) => dist.get(b) - dist.get(a));
        const bossKey = deadEnds[0];
        if (dist.get(bossKey) < 3) continue;
        const rest = rng.shuffle(deadEnds.slice(1));
        const treasureKey = rest[0];
        const shopKey = rest[1];

        return this.build(rng, cells, { bossKey, treasureKey, shopKey, startKey }, floorSeed, floorNum, player);
      }
      /* Extremely unlikely fallback: a straight corridor that is always valid. */
      const rng = new Isaac.RNG(floorSeed ^ 0xabcdef);
      const cells = [[4, 4], [5, 4], [6, 4], [7, 4], [4, 3], [4, 2], [3, 4], [2, 4]];
      return this.build(rng, cells, { startKey: "4,4", bossKey: "7,4", treasureKey: "4,2", shopKey: "2,4" }, floorSeed, floorNum, player);
    },

    build(rng, cells, keys, floorSeed, floorNum, player) {
      const index = new Map();
      const rooms = cells.map(([gx, gy], id) => {
        const key = `${gx},${gy}`;
        index.set(key, id);
        let type = "normal";
        if (key === keys.startKey) type = "spawn";
        else if (key === keys.bossKey) type = "boss";
        else if (key === keys.treasureKey) type = "treasure";
        else if (key === keys.shopKey) type = "shop";
        return { id, gx, gy, type };
      });

      const lockSpecial = floorNum >= 2;
      for (const room of rooms) {
        room.doors = { n: null, s: null, e: null, w: null };
        const around = { n: [0, -1], s: [0, 1], e: [1, 0], w: [-1, 0] };
        for (const d in around) {
          const k = `${room.gx + around[d][0]},${room.gy + around[d][1]}`;
          if (index.has(k)) room.doors[d] = index.get(k);
        }
        room.locked = lockSpecial && (room.type === "treasure" || room.type === "shop");
        room.state = room.locked ? S.LOCKED : S.UNVISITED;
        room.cleared = room.type !== "normal" && room.type !== "boss";
        room.rewardGiven = false;
        room.bossDefeated = false;
        room.trapdoor = false;
        room.pickups = [];
        room.pedestals = [];
        room.shop = [];
        room.enemySpec = [];
        room.nextPickupId = 1;

        let layout = 0;
        if (room.type === "normal") layout = rng.int(0, Isaac.LAYOUTS.length - 1);
        let tiles = Isaac.layoutTiles(layout);
        if (!Isaac.layoutConnected(tiles)) { layout = 0; tiles = Isaac.layoutTiles(0); }
        /* occasionally promote one rock to a tinted rock */
        if (room.type === "normal" && rng.chance(0.35)) {
          const rocks = [];
          tiles.forEach((t, i) => { if (t === 1) rocks.push(i); });
          if (rocks.length) tiles[rng.pick(rocks)] = 2;
        }
        room.layout = layout;
        room.tiles = tiles;

        const cx = 7.5 * T, cy = 4.5 * T;
        if (room.type === "normal") {
          room.enemySpec = makeEnemies(rng, tiles, floorNum);
        } else if (room.type === "boss") {
          room.enemySpec = [{ type: "monstro", x: cx, y: 3.2 * T }];
        } else if (room.type === "treasure") {
          room.pedestals.push({ item: Isaac.pickItem(rng, "treasure", player), x: cx, y: cy, taken: false });
        } else if (room.type === "shop") {
          const extra = rng.chance(0.5) ? "bomb" : "key";
          room.shop.push(
            { kind: "item", item: Isaac.pickItem(rng, "shop", player), price: 15, bought: false, x: 4.5 * T, y: cy },
            { kind: "pickup", type: "heart", price: 3, bought: false, x: 7.5 * T, y: cy },
            { kind: "pickup", type: extra, price: 5, bought: false, x: 10.5 * T, y: cy }
          );
        }
      }
      return { seed: floorSeed >>> 0, floorNum, rooms, startId: index.get(keys.startKey) };
    }
  };
})(window.Isaac);

---

/* Collision helpers shared by the player, enemies, pickups and projectiles.
   Entities collide with tiles as axis-aligned boxes (stable sliding along
   walls) and with each other as circles. No allocations per call. */
(function (Isaac) {
  "use strict";
  const { T, COLS, ROWS, IN_COLS, IN_ROWS } = Isaac.C;

  const K = (Isaac.collide = {
    /* opts.flying: ignores rocks. opts.door: 'player' may pass open doors. */
    solid(room, col, row, opts) {
      if (col < 0 || row < 0 || col >= COLS || row >= ROWS) return true;
      if (col === 0 || row === 0 || col === COLS - 1 || row === ROWS - 1) {
        if (opts && opts.player) {
          const dir = K.doorDirAt(col, row);
          if (dir && Isaac.game.doorOpen(room, dir)) return false;
        }
        return true;
      }
      if (opts && opts.flying) return false;
      return room.tiles[(row - 1) * IN_COLS + (col - 1)] !== 0;
    },

    /* Projectiles treat every wall tile (doors included) and every rock as solid. */
    solidPoint(room, x, y) {
      const col = Math.floor(x / T), row = Math.floor(y / T);
      if (col <= 0 || row <= 0 || col >= COLS - 1 || row >= ROWS - 1) return true;
      return room.tiles[(row - 1) * IN_COLS + (col - 1)] !== 0;
    },

    doorDirAt(col, row) {
      if (col === 7 && row === 0) return "n";
      if (col === 7 && row === ROWS - 1) return "s";
      if (col === 0 && row === 4) return "w";
      if (col === COLS - 1 && row === 4) return "e";
      return null;
    },

    overlapsSolid(room, x, y, hw, hh, opts) {
      const c0 = Math.floor((x - hw) / T), c1 = Math.floor((x + hw - 0.001) / T);
      const r0 = Math.floor((y - hh) / T), r1 = Math.floor((y + hh - 0.001) / T);
      for (let r = r0; r <= r1; r++) for (let c = c0; c <= c1; c++) if (K.solid(room, c, r, opts)) return true;
      return false;
    },

    /* Moves e by (dx, dy) with axis-separated resolution. Returns bitmask of hit axes (1 = x, 2 = y). */
    move(e, dx, dy, room, opts) {
      const hw = e.hw || e.r, hh = e.hh || e.r;
      let hit = 0;
      if (dx) {
        const nx = e.x + dx;
        if (K.overlapsSolid(room, nx, e.y, hw, hh, opts)) {
          if (dx > 0) e.x = Math.floor((nx + hw) / T) * T - hw - 0.01;
          else e.x = Math.floor((nx - hw) / T + 1) * T + hw + 0.01;
          if (K.overlapsSolid(room, e.x, e.y, hw, hh, opts)) e.x -= dx; /* safety */
          hit |= 1;
        } else e.x = nx;
      }
      if (dy) {
        const ny = e.y + dy;
        if (K.overlapsSolid(room, e.x, ny, hw, hh, opts)) {
          if (dy > 0) e.y = Math.floor((ny + hh) / T) * T - hh - 0.01;
          else e.y = Math.floor((ny - hh) / T + 1) * T + hh + 0.01;
          if (K.overlapsSolid(room, e.x, e.y, hw, hh, opts)) e.y -= dy;
          hit |= 2;
        } else e.y = ny;
      }
      return hit;
    },

    circles(a, b, extra) {
      const r = a.r + b.r + (extra || 0);
      const dx = b.x - a.x, dy = b.y - a.y;
      return dx * dx + dy * dy < r * r;
    },

    /* If an entity somehow ends up inside a solid tile (bad save, bomb edge case),
       move it to the nearest free interior tile. */
    ensureFree(e, room) {
      const hw = e.hw || e.r, hh = e.hh || e.r;
      if (!K.overlapsSolid(room, e.x, e.y, hw, hh)) return;
      let best = null, bestD = Infinity;
      for (let r = 0; r < IN_ROWS; r++) for (let c = 0; c < IN_COLS; c++) {
        if (room.tiles[r * IN_COLS + c] !== 0) continue;
        const x = (c + 1.5) * T, y = (r + 1.5) * T;
        const d = (x - e.x) ** 2 + (y - e.y) ** 2;
        if (d < bestD) { bestD = d; best = { x, y }; }
      }
      if (best) { e.x = best.x; e.y = best.y; }
    },

    /* Flow field toward a target tile over the 13 x 7 interior. */
    flow: new Int16Array(IN_COLS * IN_ROWS),
    flowTarget: -1,
    queue: new Int16Array(IN_COLS * IN_ROWS),

    buildFlow(room, tx, ty) {
      const tc = Isaac.util.clamp(Math.floor(tx / T) - 1, 0, IN_COLS - 1);
      const tr = Isaac.util.clamp(Math.floor(ty / T) - 1, 0, IN_ROWS - 1);
      const target = tr * IN_COLS + tc;
      if (target === K.flowTarget && !room.flowDirty) return;
      K.flowTarget = target;
      room.flowDirty = false;
      const f = K.flow, q = K.queue;
      f.fill(-1);
      let head = 0, tail = 0;
      f[target] = 0;
      q[tail++] = target;
      while (head < tail) {
        const i = q[head++];
        const c = i % IN_COLS, r = (i / IN_COLS) | 0;
        const d = f[i] + 1;
        for (let k = 0; k < 4; k++) {
          let j;
          if (k === 0) { if (c === 0) continue; j = i - 1; }
          else if (k === 1) { if (c === IN_COLS - 1) continue; j = i + 1; }
          else if (k === 2) { if (r === 0) continue; j = i - IN_COLS; }
          else { if (r === IN_ROWS - 1) continue; j = i + IN_COLS; }
          if (f[j] !== -1 || room.tiles[j] !== 0) continue;
          f[j] = d;
          q[tail++] = j;
        }
      }
    },

    /* Writes a unit direction into out following the flow field. */
    flowDir(e, px, py, out) {
      const c = Math.floor(e.x / T) - 1, r = Math.floor(e.y / T) - 1;
      const f = K.flow;
      const here = (c >= 0 && r >= 0 && c < IN_COLS && r < IN_ROWS) ? f[r * IN_COLS + c] : -1;
      let bx = px, by = py;
      if (here > 1) {
        let best = here;
        for (let dr = -1; dr <= 1; dr++) for (let dc = -1; dc <= 1; dc++) {
          if (!dr && !dc) continue;
          const nc = c + dc, nr = r + dr;
          if (nc < 0 || nr < 0 || nc >= IN_COLS || nr >= IN_ROWS) continue;
          const v = f[nr * IN_COLS + nc];
          if (v < 0 || v >= best) continue;
          /* no corner cutting through rocks */
          if (dr && dc && (f[r * IN_COLS + nc] < 0 || f[nr * IN_COLS + c] < 0)) continue;
          best = v;
          bx = (nc + 1.5) * T; by = (nr + 1.5) * T;
        }
      }
      const dx = bx - e.x, dy = by - e.y;
      const len = Math.hypot(dx, dy) || 1;
      out.x = dx / len; out.y = dy / len;
      return out;
    }
  });
})(window.Isaac);

---

/* The player: movement, shooting, health. Health values are counted in
   half hearts: red = filled red halves, soul = soul halves, maxHearts = red
   containers (whole hearts). Nothing here can drive a value negative. */
(function (Isaac) {
  "use strict";
  const { T, MAX_HEARTS } = Isaac.C;
  const U = Isaac.util;
  const K = Isaac.collide;
  const DIR = { up: [0, -1], down: [0, 1], left: [-1, 0], right: [1, 0] };

  const P = (Isaac.Player = {
    create() {
      const p = {
        x: 7.5 * T, y: 4.5 * T, vx: 0, vy: 0, r: 9, hw: 9, hh: 8,
        items: [], active: { id: "yum_heart", charge: 4 },
        coins: 0, keys: 0, bombs: 1,
        maxHearts: 3, red: 6, soul: 0,
        invuln: 0, fireCooldown: 0, eye: 0,
        face: "down", walkT: 0, shootAnim: 0, dead: false,
        stats: null
      };
      P.recompute(p);
      return p;
    },

    recompute(p) {
      p.stats = Isaac.computeStats(p.items);
    },

    totalHearts(p) { return p.maxHearts + Math.ceil(p.soul / 2); },

    addContainer(p, n, fill) {
      const room = MAX_HEARTS - P.totalHearts(p);
      const add = Math.max(0, Math.min(n, room));
      p.maxHearts += add;
      if (fill) p.red = Math.min(p.maxHearts * 2, p.red + add * 2);
      if (add < n) p.soul = Math.max(0, p.soul - (n - add) * 2); /* keep total under the cap */
    },

    heal(p, halves) {
      p.red = U.clamp(p.red + halves, 0, p.maxHearts * 2);
    },

    addSoul(p, halves) {
      const cap = (MAX_HEARTS - p.maxHearts) * 2;
      p.soul = U.clamp(p.soul + halves, 0, Math.max(0, cap));
    },

    canTakeRed(p) { return p.red < p.maxHearts * 2; },
    canTakeSoul(p) { return p.maxHearts * 2 + p.soul < MAX_HEARTS * 2; },

    hurt(p, halves, game) {
      if (p.dead || p.invuln > 0 || halves <= 0) return false;
      let left = halves;
      const fromSoul = Math.min(p.soul, left);
      p.soul -= fromSoul; left -= fromSoul;
      p.red = Math.max(0, p.red - left);
      p.invuln = 1.0;
      Isaac.audio.play("hurt");
      Isaac.particles.blood(p.x, p.y - 4, 8);
      game.shake(4);
      if (p.red <= 0 && p.soul <= 0) {
        p.dead = true;
        game.onPlayerDeath();
      }
      return true;
    },

    update(p, game, dt) {
      const room = game.room;
      const s = p.stats;
      if (p.invuln > 0) p.invuln = Math.max(0, p.invuln - dt);
      if (p.fireCooldown > 0) p.fireCooldown -= dt;
      if (p.shootAnim > 0) p.shootAnim -= dt;

      /* movement with acceleration so it feels weighty but responsive */
      const mv = Isaac.input.moveVector();
      const speed = 118 * s.moveSpeed;
      const accel = Math.min(1, dt * 14);
      p.vx = U.lerp(p.vx, mv.x * speed, accel);
      p.vy = U.lerp(p.vy, mv.y * speed, accel);
      if (Math.abs(p.vx) < 0.5) p.vx = 0;
      if (Math.abs(p.vy) < 0.5) p.vy = 0;

      /* door assist: gently line the player up with a door they are pushing toward */
      this.doorAssist(p, game, mv, dt);

      K.move(p, p.vx * dt, p.vy * dt, room, { player: true });

      if (mv.x || mv.y) {
        p.walkT += dt * 10;
        if (!Isaac.input.shootDir()) p.face = Math.abs(mv.x) > Math.abs(mv.y) ? (mv.x > 0 ? "right" : "left") : (mv.y > 0 ? "down" : "up");
      }

      /* shooting */
      const sd = Isaac.input.shootDir();
      if (sd) {
        p.face = sd;
        if (p.fireCooldown <= 0) {
          this.fire(p, sd);
          p.fireCooldown = s.tearDelay;
        }
      }
    },

    doorAssist(p, game, mv, dt) {
      const room = game.room;
      const pull = 90 * dt;
      if (mv.y < -0.3 && p.y < 2.2 * T && game.doorOpen(room, "n") && Math.abs(p.x - 7.5 * T) < 18) p.x += U.clamp(7.5 * T - p.x, -pull, pull);
      if (mv.y > 0.3 && p.y > 6.8 * T && game.doorOpen(room, "s") && Math.abs(p.x - 7.5 * T) < 18) p.x += U.clamp(7.5 * T - p.x, -pull, pull);
      if (mv.x < -0.3 && p.x < 2.2 * T && game.doorOpen(room, "w") && Math.abs(p.y - 4.5 * T) < 18) p.y += U.clamp(4.5 * T - p.y, -pull, pull);
      if (mv.x > 0.3 && p.x > 12.8 * T && game.doorOpen(room, "e") && Math.abs(p.y - 4.5 * T) < 18) p.y += U.clamp(4.5 * T - p.y, -pull, pull);
    },

    fire(p, dir) {
      const s = p.stats;
      const [dx, dy] = DIR[dir];
      p.eye ^= 1;
      const side = p.eye ? 3.5 : -3.5;
      const ox = dy !== 0 ? side : 0, oy = dx !== 0 ? side * 0.5 : 0;
      const spd = 225 * s.shotSpeed;
      /* tears inherit part of the player's momentum, like the original */
      const vx = dx * spd + p.vx * 0.35;
      const vy = dy * spd + p.vy * 0.35;
      const ok = Isaac.projectiles.spawnTear(p.x + ox + dx * 6, p.y - 2 + oy + dy * 4, vx, vy, s.damage, s.range * T, "player");
      if (ok) {
        p.shootAnim = 0.12;
        Isaac.audio.play("tear");
      }
    }
  });
})(window.Isaac);

---

/* Enemy definitions and behaviour. Every enemy spends its first moments in a
   harmless spawn state so a room never hits the player the instant they walk in.
   The boss telegraphs every attack: crouch before hops, an open glowing mouth
   before the spit burst, and a tracked shadow that locks in place before the slam. */
(function (Isaac) {
  "use strict";
  const { T } = Isaac.C;
  const U = Isaac.util;
  const K = Isaac.collide;
  const tmp = { x: 0, y: 0 };

  const DEFS = {
    fly: { hp: 4, r: 7, speed: 78, flying: true },
    clot: { hp: 10, r: 10, speed: 54 },
    spitter: { hp: 12, r: 10, speed: 50 },
    monstro: { hp: 220, r: 24, speed: 0, boss: true }
  };
  Isaac.ENEMY_TYPES = Object.keys(DEFS);

  const E = (Isaac.enemies = {
    DEFS,

    create(spec, floorNum, rng) {
      const d = DEFS[spec.type];
      if (!d) return null;
      const scale = d.boss ? 1 + (floorNum - 1) * 0.35 : 1 + (floorNum - 1) * 0.28;
      const hp = Math.round(d.hp * scale);
      const e = {
        type: spec.type, x: spec.x, y: spec.y, vx: 0, vy: 0,
        r: d.r, hw: d.boss ? 20 : d.r * 0.8, hh: d.boss ? 14 : d.r * 0.75,
        hp, maxHp: hp, flying: !!d.flying, boss: !!d.boss,
        speed: d.speed * (1 + (floorNum - 1) * 0.05),
        t: rng.float(0, 10), phase: rng.float(0, Math.PI * 2),
        state: "move", stateT: 0, timer: rng.float(1.2, 2.4),
        hitFlash: 0, spawnT: d.boss ? 1.1 : 0.6, dead: false, dying: false,
        z: 0, jx: 0, jy: 0, jitterT: 0, strafe: rng.chance(0.5) ? 1 : -1,
        hitOffset: d.boss ? 12 : 0, squash: 0, mouth: 0,
        shadowX: spec.x, shadowY: spec.y, hops: 0, lastAttack: "", repeat: 0
      };
      if (e.boss) { e.state = "intro"; e.stateT = 1.1; }
      return e;
    },

    update(e, game, dt) {
      if (e.hitFlash > 0) e.hitFlash -= dt;
      if (e.dying) {
        e.dieT -= dt;
        e.t += dt;
        if (Isaac.vrng.chance(dt * 18)) Isaac.particles.blood(e.x + Isaac.vrng.float(-18, 18), e.y - 14 + Isaac.vrng.float(-12, 12), 3);
        if (e.dieT <= 0) {
          e.dead = true;
          Isaac.particles.blood(e.x, e.y - 12, 40);
          game.shake(8);
          Isaac.audio.play("die");
          game.onBossDefeated(e);
        }
        return;
      }
      if (e.spawnT > 0) { e.spawnT -= dt; return; }
      e.t += dt;
      switch (e.type) {
        case "fly": this.fly(e, game, dt); break;
        case "clot": this.clot(e, game, dt); break;
        case "spitter": this.spitter(e, game, dt); break;
        case "monstro": this.monstro(e, game, dt); break;
      }
    },

    fly(e, game, dt) {
      const p = game.player;
      e.jitterT -= dt;
      if (e.jitterT <= 0) {
        e.jitterT = game.grng.float(0.15, 0.35);
        e.jx = game.grng.float(-1, 1) * 90;
        e.jy = game.grng.float(-1, 1) * 90;
      }
      const ang = e.t * 2.6 + e.phase;
      const tx = p.x + Math.cos(ang) * 28, ty = p.y + Math.sin(ang) * 22;
      let dx = tx - e.x, dy = ty - e.y;
      const len = Math.hypot(dx, dy) || 1;
      dx /= len; dy /= len;
      const k = Math.min(1, dt * 3);
      e.vx += (dx * e.speed + e.jx - e.vx) * k;
      e.vy += (dy * e.speed + e.jy - e.vy) * k;
      const sp = Math.hypot(e.vx, e.vy);
      const max = e.speed * 1.4;
      if (sp > max) { e.vx = e.vx / sp * max; e.vy = e.vy / sp * max; }
      K.move(e, e.vx * dt, e.vy * dt, game.room, { flying: true });
    },

    clot(e, game, dt) {
      const p = game.player;
      const d = U.dist(e.x, e.y, p.x, p.y);
      if (d < 44) { tmp.x = (p.x - e.x) / (d || 1); tmp.y = (p.y - e.y) / (d || 1); }
      else K.flowDir(e, p.x, p.y, tmp);
      /* moves in little lunges */
      const pulse = 0.55 + 0.45 * Math.max(0, Math.sin(e.t * 7));
      e.squash = Math.sin(e.t * 7);
      const k = Math.min(1, dt * 6);
      e.vx += (tmp.x * e.speed * pulse - e.vx) * k;
      e.vy += (tmp.y * e.speed * pulse - e.vy) * k;
      K.move(e, e.vx * dt, e.vy * dt, game.room);
    },

    spitter(e, game, dt) {
      const p = game.player;
      const d = U.dist(e.x, e.y, p.x, p.y);
      if (e.state === "wind") {
        e.stateT -= dt;
        e.mouth = 1 - e.stateT / 0.5;
        e.vx *= 0.85; e.vy *= 0.85;
        if (e.stateT <= 0) {
          const a = Math.atan2(p.y - 3 - e.y, p.x - e.x);
          Isaac.projectiles.spawnEnemyShot(e.x + Math.cos(a) * 8, e.y + Math.sin(a) * 8, Math.cos(a) * 140, Math.sin(a) * 140, 280);
          Isaac.audio.play("spit");
          e.state = "move";
          e.mouth = 0;
          e.timer = game.grng.float(1.8, 2.8);
        }
      } else {
        if (d < 105) { tmp.x = (e.x - p.x) / (d || 1); tmp.y = (e.y - p.y) / (d || 1); }
        else if (d > 175) K.flowDir(e, p.x, p.y, tmp);
        else {
          const ax = (p.x - e.x) / (d || 1), ay = (p.y - e.y) / (d || 1);
          tmp.x = -ay * e.strafe; tmp.y = ax * e.strafe;
          if (game.grng.chance(dt * 0.4)) e.strafe *= -1;
        }
        const k = Math.min(1, dt * 5);
        e.vx += (tmp.x * e.speed - e.vx) * k;
        e.vy += (tmp.y * e.speed - e.vy) * k;
        e.timer -= dt;
        if (e.timer <= 0 && d < 300) { e.state = "wind"; e.stateT = 0.5; }
      }
      const hit = K.move(e, e.vx * dt, e.vy * dt, game.room);
      if (hit) e.strafe *= -1;
    },

    /* ---------------- Monstro-style boss ---------------- */
    monstro(e, game, dt) {
      const p = game.player;
      const g = game.grng;
      e.stateT -= dt;
      const go = (state, time) => { e.state = state; e.stateT = time; };

      switch (e.state) {
        case "intro":
          e.squash = Math.sin(e.t * 4) * 0.1;
          if (e.stateT <= 0) { Isaac.audio.play("roar"); go("idle", 0.6); }
          break;

        case "idle": {
          e.squash = Math.sin(e.t * 5) * 0.06;
          const d = U.dist(e.x, e.y, p.x, p.y) || 1;
          K.move(e, (p.x - e.x) / d * 18 * dt, (p.y - e.y) / d * 18 * dt, game.room);
          if (e.stateT <= 0) {
            let pick = g.weighted({ hop: 40, spit: 30, jump: 30 });
            if (pick === e.lastAttack && e.repeat >= 1) pick = pick === "hop" ? "spit" : "hop";
            e.repeat = pick === e.lastAttack ? e.repeat + 1 : 0;
            e.lastAttack = pick;
            if (pick === "hop") { e.hops = 3; go("hopCrouch", 0.28); }
            else if (pick === "spit") { go("spitWind", 0.8); }
            else go("jumpWind", 0.6);
          }
          break;
        }

        case "hopCrouch":
          e.squash = -0.25;
          if (e.stateT <= 0) {
            const d = U.dist(e.x, e.y, p.x, p.y) || 1;
            const reach = Math.min(d, 70);
            e.vx = (p.x - e.x) / d * reach / 0.45;
            e.vy = (p.y - e.y) / d * reach / 0.45;
            go("hopAir", 0.45);
          }
          break;

        case "hopAir": {
          const prog = 1 - e.stateT / 0.45;
          e.z = Math.sin(U.clamp(prog, 0, 1) * Math.PI) * 24;
          e.squash = 0.15;
          K.move(e, e.vx * dt, e.vy * dt, game.room);
          if (e.stateT <= 0) {
            e.z = 0; e.vx = e.vy = 0;
            game.shake(2);
            Isaac.audio.play("door");
            e.hops--;
            if (e.hops > 0) go("hopCrouch", 0.22);
            else go("recover", 0.6);
          }
          break;
        }

        case "spitWind":
          e.mouth = 1 - e.stateT / 0.8;
          e.squash = -0.1 * e.mouth;
          if (e.stateT <= 0) {
            const base = Math.atan2(p.y - (e.y - 8), p.x - e.x);
            const n = 10 + Math.min(4, game.floorNum);
            for (let i = 0; i < n; i++) {
              const a = base + g.float(-0.55, 0.55);
              const sp = g.float(95, 170);
              Isaac.projectiles.spawnEnemyShot(e.x + Math.cos(a) * 14, e.y - 8 + Math.sin(a) * 10, Math.cos(a) * sp, Math.sin(a) * sp, 320);
            }
            Isaac.audio.play("spit");
            game.shake(3);
            go("spitRecover", 0.9);
          }
          break;

        case "spitRecover":
          e.mouth = Math.max(0, e.stateT / 0.9);
          if (e.stateT <= 0) { e.mouth = 0; go("idle", g.float(0.5, 0.9)); }
          break;

        case "jumpWind":
          e.squash = -0.3 * (1 - e.stateT / 0.6);
          if (e.stateT <= 0) { e.shadowX = e.x; e.shadowY = e.y; go("jumpUp", 0.35); Isaac.audio.play("roar"); }
          break;

        case "jumpUp":
          e.z = (1 - e.stateT / 0.35) * 320;
          e.squash = 0.35;
          if (e.stateT <= 0) { e.z = 320; go("jumpAir", 1.0); }
          break;

        case "jumpAir": {
          const d = U.dist(e.x, e.y, p.x, p.y) || 1;
          const sp = Math.min(160, d / dt);
          K.move(e, (p.x - e.x) / d * sp * dt, (p.y - e.y) / d * sp * dt, game.room);
          if (e.stateT <= 0) go("jumpLock", 0.45);
          break;
        }

        case "jumpLock":
          /* shadow is fixed here, flashing: the landing spot is now final */
          if (e.stateT <= 0) go("jumpFall", 0.2);
          break;

        case "jumpFall":
          e.z = Math.max(0, (e.stateT / 0.2) * 320);
          if (e.stateT <= 0) {
            e.z = 0;
            e.squash = -0.35;
            game.shake(7);
            Isaac.audio.play("slam");
            Isaac.particles.burst(e.x, e.y, 18, "#7a5a44", 120, 3);
            if (U.dist(e.x, e.y, p.x, p.y) < 36) Isaac.Player.hurt(p, 2, game);
            const n = 8 + Math.min(4, game.floorNum);
            const off = g.float(0, Math.PI);
            for (let i = 0; i < n; i++) {
              const a = off + (i / n) * Math.PI * 2;
              Isaac.projectiles.spawnEnemyShot(e.x + Math.cos(a) * 20, e.y + Math.sin(a) * 12, Math.cos(a) * 120, Math.sin(a) * 120, 280);
            }
            go("recover", 1.0);
          }
          break;

        case "recover":
          e.squash *= 0.9;
          if (e.stateT <= 0) go("idle", g.float(0.4, 0.8));
          break;
      }
    },

    airborne(e) { return (e.z || 0) > 14; },

    damage(e, dmg, game, kvx, kvy) {
      if (e.dead || e.dying || e.spawnT > 0) return;
      e.hp -= dmg;
      e.hitFlash = 0.08;
      if (!e.boss) {
        const sp = Math.hypot(kvx, kvy) || 1;
        e.vx += kvx / sp * 60;
        e.vy += kvy / sp * 60;
      }
      Isaac.audio.play("hit");
      if (e.hp <= 0) this.kill(e, game);
    },

    kill(e, game) {
      if (e.boss) {
        e.dying = true;
        e.dieT = 1.4;
        e.z = 0;
        e.mouth = 1;
        Isaac.audio.play("roar");
        return;
      }
      e.dead = true;
      Isaac.particles.blood(e.x, e.y, e.type === "fly" ? 6 : 12);
      Isaac.audio.play("die");
    }
  });
})(window.Isaac);

---

/* Central game state. One object, one simulation, driven by main.js.
   Modes: menu | playing | transition | paused | gameover */
(function (Isaac) {
  "use strict";
  const { T, W, H, MAX_ENEMIES } = Isaac.C;
  const U = Isaac.util;
  const S = Isaac.ROOM_STATE;
  const K = Isaac.collide;
  const PL = Isaac.Player;
  const EN = Isaac.enemies;

  const G = (Isaac.game = {
    mode: "menu",
    runSeed: "",
    floorNum: 1,
    floor: null,
    room: null,
    player: null,
    enemies: [],
    grng: new Isaac.RNG(1),
    elapsed: 0,
    shakeAmt: 0,
    bgDirty: true,
    trapArmed: false,
    transition: null,
    itemBanner: null,
    floorBanner: null,
    fade: 0,
    deathT: 0,
    activeFlash: 0,
    lastToast: { msg: "", t: 0 },
    hooks: {},

    setMode(m) {
      this.mode = m;
      if (this.hooks.onMode) this.hooks.onMode(m);
    },

    /* ---------------- run lifecycle ---------------- */
    newRun(seed) {
      Isaac.save.clear();
      this.runSeed = seed || Isaac.makeSeedString();
      this.player = PL.create();
      this.elapsed = 0;
      this.startFloor(1);
      this.setMode("playing");
    },

    startFloor(n) {
      this.floorNum = n;
      const floorSeed = U.hash(this.runSeed + ":floor:" + n);
      this.grng = new Isaac.RNG(U.hash(this.runSeed + ":play:" + n));
      this.floor = Isaac.floorGen.generate(floorSeed, n, this.player);
      this.enterRoom(this.floor.startId, null);
      const p = this.player;
      p.x = 7.5 * T; p.y = 4.5 * T; p.vx = p.vy = 0;
      this.floorBanner = { text: Isaac.floorName(n), t: 2.6 };
      this.fade = 1;
      this.saveNow();
    },

    loadRun(d) {
      this.runSeed = d.runSeed;
      this.floorNum = d.floorNum;
      this.elapsed = d.elapsed;
      this.grng = new Isaac.RNG(1);
      this.grng.state = d.grngState;
      const startId = Math.max(0, d.rooms.findIndex((r) => r.type === "spawn"));
      this.floor = { seed: d.floorSeed, floorNum: d.floorNum, rooms: d.rooms, startId };
      const p = PL.create();
      Object.assign(p, {
        items: d.player.items, active: d.player.active,
        coins: d.player.coins, keys: d.player.keys, bombs: d.player.bombs,
        maxHearts: d.player.maxHearts, red: d.player.red, soul: d.player.soul
      });
      PL.recompute(p);
      this.player = p;
      this.enterRoom(d.currentRoomId, null);
      p.x = d.player.x; p.y = d.player.y;
      K.ensureFree(p, this.room);
      p.invuln = 1.5;
      this.floorBanner = { text: Isaac.floorName(this.floorNum), t: 2.0 };
      this.fade = 1;
      this.validateState();
      this.setMode("playing");
    },

    nextFloor() {
      if (this.mode !== "playing") return;
      Isaac.audio.play("floor");
      this.startFloor(this.floorNum + 1);
    },

    onPlayerDeath() {
      this.deathT = 1.3;
      this.player.vx = this.player.vy = 0;
    },

    finishDeath() {
      Isaac.save.clear();
      Isaac.audio.play("gameover");
      this.setMode("gameover");
      if (this.hooks.onGameOver) {
        this.hooks.onGameOver({ floor: Isaac.floorName(this.floorNum), time: U.fmtTime(this.elapsed), seed: this.runSeed, items: this.player.items.length });
      }
    },

    /* ---------------- rooms ---------------- */
    enterRoom(id, fromDir) {
      const room = this.floor && this.floor.rooms[id];
      if (!room) throw new Error("enterRoom: room " + id + " does not exist");
      this.room = room;
      this.enemies.length = 0;
      Isaac.projectiles.clear();
      Isaac.bombs.clear();
      Isaac.particles.clear();
      room.decals = room.decals || [];
      room.flowDirty = true;
      this.bgDirty = true;
      this.trapArmed = false;

      const p = this.player;
      if (fromDir) {
        const pt = Isaac.entryPoint(Isaac.OPPOSITE[fromDir]);
        p.x = pt.x; p.y = pt.y;
        p.vx *= 0.3; p.vy *= 0.3;
      }

      if (room.state === S.LOCKED) room.state = S.UNVISITED;
      const hostile = (room.type === "normal" || room.type === "boss") && !room.cleared;
      if (hostile) {
        room.state = S.ACTIVE;
        for (const spec of room.enemySpec) {
          if (this.enemies.length >= MAX_ENEMIES) break;
          const e = EN.create(spec, this.floorNum, this.grng);
          if (e) this.enemies.push(e);
        }
        if (this.enemies.length) {
          Isaac.audio.play("door");
          if (room.type === "boss") this.banner("Monstro", "Watch its shadow");
        }
      } else if (room.type === "treasure" || room.type === "shop") {
        room.state = S.SPECIAL;
      } else if (room.state === S.UNVISITED || room.state === S.VISITED) {
        room.state = room.type === "spawn" ? S.VISITED : S.CLEARED;
      }
      K.ensureFree(p, room);
      this.queueSave();
      return true;
    },

    doorOpen(room, dir) {
      const nid = room.doors[dir];
      if (nid === null || nid === undefined) return false;
      if (room.state === S.ACTIVE) return false;
      const n = this.floor.rooms[nid];
      return !!n && !n.locked;
    },

    doorLocked(room, dir) {
      const nid = room.doors[dir];
      if (nid === null || nid === undefined) return false;
      const n = this.floor.rooms[nid];
      return !!n && n.locked;
    },

    beginTransition(dir) {
      const nid = this.room.doors[dir];
      if (nid === null || nid === undefined) return;
      Isaac.renderer.snapshot(this);
      this.enterRoom(nid, dir);
      this.transition = { dir, t: 0, dur: 0.3 };
      this.setMode("transition");
    },

    checkExit() {
      const p = this.player;
      let dir = null;
      if (p.y < T * 0.62) dir = "n";
      else if (p.y > H - T * 0.62) dir = "s";
      else if (p.x < T * 0.62) dir = "w";
      else if (p.x > W - T * 0.62) dir = "e";
      if (dir && this.doorOpen(this.room, dir)) this.beginTransition(dir);
    },

    checkLockedDoors() {
      const p = this.player;
      for (const dir of Isaac.DIRS) {
        if (!this.doorLocked(this.room, dir)) continue;
        const [c, r] = Isaac.DOOR_TILE[dir];
        if (U.dist(p.x, p.y, (c + 0.5) * T, (r + 0.5) * T) > 30) continue;
        if (this.room.state === S.ACTIVE) continue;
        if (p.keys > 0) {
          p.keys -= 1;
          const n = this.floor.rooms[this.room.doors[dir]];
          n.locked = false;
          n.state = S.UNVISITED;
          Isaac.audio.play("unlock");
          this.toast("Door unlocked.");
          this.saveNow();
        } else {
          this.toast("You need a key.");
        }
      }
    },

    checkClear() {
      const room = this.room;
      if (room.state !== S.ACTIVE) return;
      for (const e of this.enemies) if (!e.dead) return;
      room.state = S.CLEARED;
      room.cleared = true;
      Isaac.audio.play("doorOpen");
      if (!room.rewardGiven) {
        room.rewardGiven = true;
        if (room.type === "normal") this.roomReward(room);
        this.addCharge(1);
      }
      this.saveNow();
    },

    roomReward(room) {
      const rng = new Isaac.RNG(U.hash(this.floor.seed + ":reward:" + room.id));
      if (!rng.chance(0.65)) return;
      const type = rng.weighted({ penny: 30, nickel: 4, key: 12, bomb: 12, heart: 12, halfheart: 6, soulheart: 3, chest: 5 });
      Isaac.pickups.pop(room, type, 7.5 * T, 4.5 * T, rng);
    },

    onBossDefeated() {
      const room = this.room;
      if (room.bossDefeated) return;
      room.bossDefeated = true;
      room.trapdoor = true;
      this.trapArmed = false;
      room.pedestals.push({ item: Isaac.pickItem(this.grng, "boss", this.player), x: 7.5 * T, y: 5.7 * T, taken: false, cooldown: 0.8 });
      Isaac.pickups.spawn(room, "heart", 5.5 * T, 5.7 * T);
      this.banner("Monstro defeated", "Take the item, then jump down the trapdoor");
    },

    addCharge(n) {
      const a = this.player.active;
      if (!a) return;
      const max = Isaac.ITEMS[a.id].charge;
      if (a.charge >= max) return;
      a.charge = Math.min(max, a.charge + n);
      Isaac.audio.play("charge");
    },

    useActive() {
      const a = this.player.active;
      if (!a) { this.toast("You don't have an active item."); return; }
      const def = Isaac.ITEMS[a.id];
      if (a.charge < def.charge) { this.toast(`${def.name} needs ${def.charge - a.charge} more room clear${def.charge - a.charge === 1 ? "" : "s"}.`); Isaac.audio.play("deny"); return; }
      if (def.use(this) === false) { Isaac.audio.play("deny"); return; }
      a.charge = 0;
      this.activeFlash = 0.5;
      Isaac.audio.play("active");
      this.saveNow();
    },

    /* ---------------- update ---------------- */
    update(dt) {
      if (this.mode === "transition") {
        this.transition.t += dt;
        if (this.transition.t >= this.transition.dur) { this.transition = null; this.setMode("playing"); }
        return;
      }
      if (this.mode !== "playing") return;

      const inp = Isaac.input;
      if (inp.consume("pause")) { if (this.hooks.pause) this.hooks.pause(); return; }
      if (inp.consume("mute") && this.hooks.toggleMute) this.hooks.toggleMute();
      if (inp.consume("debug") && this.hooks.toggleDebug) this.hooks.toggleDebug();

      this.elapsed += dt;
      const p = this.player, room = this.room;

      if (!p.dead) {
        PL.update(p, this, dt);
        if (inp.consume("bomb")) Isaac.bombs.place(this);
        if (inp.consume("item")) this.useActive();
      } else {
        inp.consume("bomb"); inp.consume("item");
        this.deathT -= dt;
        if (this.deathT <= 0) { this.finishDeath(); return; }
      }

      if (room.state === S.ACTIVE) K.buildFlow(room, p.x, p.y);

      for (const e of this.enemies) EN.update(e, this, dt);
      this.separateEnemies();
      if (!p.dead) this.contactDamage();

      let w = 0;
      for (const e of this.enemies) if (!e.dead) this.enemies[w++] = e;
      this.enemies.length = w;

      Isaac.projectiles.update(this, dt);
      Isaac.bombs.update(this, dt);
      if (this.mode !== "playing") return; /* a trapdoor or death may have changed state */
      Isaac.pickups.update(this, dt);
      if (this.mode !== "playing" || this.room !== room) return;
      Isaac.particles.update(dt);

      if (!p.dead) {
        this.checkLockedDoors();
        this.checkClear();
        this.checkExit();
      }

      if (this.shakeAmt > 0) this.shakeAmt = Math.max(0, this.shakeAmt - dt * 30);
      if (this.itemBanner && (this.itemBanner.t -= dt) <= 0) this.itemBanner = null;
      if (this.floorBanner && (this.floorBanner.t -= dt) <= 0) this.floorBanner = null;
      if (this.fade > 0) this.fade = Math.max(0, this.fade - dt * 2.5);
      if (this.activeFlash > 0) this.activeFlash -= dt;
    },

    separateEnemies() {
      const list = this.enemies;
      for (let i = 0; i < list.length; i++) {
        const a = list[i];
        if (a.dead || a.dying || a.flying || a.boss) continue;
        for (let j = i + 1; j < list.length; j++) {
          const b = list[j];
          if (b.dead || b.dying || b.flying || b.boss) continue;
          const dx = b.x - a.x, dy = b.y - a.y;
          const min = a.r + b.r - 2;
          const d2 = dx * dx + dy * dy;
          if (d2 >= min * min || d2 < 0.0001) continue;
          const d = Math.sqrt(d2), push = (min - d) * 0.5;
          const nx = dx / d, ny = dy / d;
          K.move(a, -nx * push, -ny * push, this.room);
          K.move(b, nx * push, ny * push, this.room);
        }
      }
    },

    contactDamage() {
      const p = this.player;
      for (const e of this.enemies) {
        if (e.dead || e.dying || e.spawnT > 0 || EN.airborne(e)) continue;
        const rr = e.r * 0.85 + p.r * 0.8;
        const dx = p.x - e.x, dy = p.y - e.y;
        if (dx * dx + dy * dy < rr * rr) {
          if (PL.hurt(p, 1, this)) {
            const d = Math.hypot(dx, dy) || 1;
            p.vx += dx / d * 220; p.vy += dy / d * 220;
          }
        }
      }
    },

    /* ---------------- helpers ---------------- */
    boss() {
      for (const e of this.enemies) if (e.boss && !e.dead) return e;
      return null;
    },

    shake(n) { this.shakeAmt = Math.max(this.shakeAmt, n); },

    banner(title, sub) { this.itemBanner = { title, sub, t: 2.6 }; },

    toast(msg) {
      const now = performance.now();
      if (this.lastToast.msg === msg && now - this.lastToast.t < 1600) return;
      this.lastToast = { msg, t: now };
      if (this.hooks.toast) this.hooks.toast(msg);
    },

    queueSave() {
      if (this.player && !this.player.dead && this.floor) Isaac.save.queue(this);
    },

    saveNow() {
      if (this.player && !this.player.dead && this.floor) Isaac.save.now(this);
    },

    /* Checked before loading into play and on demand from the debug panel. */
    validateState() {
      const p = this.player;
      if (!p) throw new Error("state: no player");
      if (!this.floor || !Array.isArray(this.floor.rooms) || !this.room) throw new Error("state: no floor or room");
      if (this.floor.rooms[this.room.id] !== this.room) throw new Error("state: current room is not part of the floor");
      for (const k of ["x", "y", "red", "soul", "maxHearts"]) if (!Number.isFinite(p[k])) throw new Error("state: player." + k + " is not finite");
      for (const k in p.stats) if (!Number.isFinite(p.stats[k])) throw new Error("state: stat " + k + " is not finite");
      p.coins = Math.max(0, p.coins | 0);
      p.keys = Math.max(0, p.keys | 0);
      p.bombs = Math.max(0, p.bombs | 0);
      p.red = U.clamp(p.red, 0, p.maxHearts * 2);
      p.soul = Math.max(0, p.soul);
      return true;
    }
  });
})(window.Isaac);

---

/* Canvas renderer. Everything is drawn procedurally in logical 480 x 288
   units and scaled once through setTransform, so it stays crisp on high-DPI
   phones without a huge backing store. The static room (walls, floor, rocks)
   is cached to an offscreen canvas and only redrawn when it changes. */
(function (Isaac) {
  "use strict";
  const { T, W, H, COLS, ROWS, IN_COLS, IN_ROWS } = Isaac.C;
  const U = Isaac.util;
  const S = Isaac.ROOM_STATE;
  const TAU = Math.PI * 2;

  const PAL = [
    { floor: "#5e412c", floor2: "#583c28", wall: "#3a2617", hi: "#533722", lo: "#1f130a", rock: "#7d736b", rockHi: "#a59a90", rockLo: "#4a423d" },
    { floor: "#5f5244", floor2: "#594c3f", wall: "#3a3128", hi: "#4d4236", lo: "#1f1a15", rock: "#76706a", rockHi: "#9d968e", rockLo: "#48433e" },
    { floor: "#404048", floor2: "#3b3b42", wall: "#25252b", hi: "#36363e", lo: "#111114", rock: "#6c6c76", rockHi: "#92929c", rockLo: "#414149" },
    { floor: "#6f2b30", floor2: "#67272c", wall: "#461418", hi: "#5d1c21", lo: "#260809", rock: "#8c4f53", rockHi: "#b0777a", rockLo: "#562d30" }
  ];

  const R = (Isaac.renderer = {
    canvas: null, ctx: null, scale: 1,
    bg: null, bgCtx: null, bgFor: null, bgScale: 0,
    snap: null, snapCtx: null,
    t: 0,

    init(canvas) {
      this.canvas = canvas;
      this.ctx = canvas.getContext("2d", { alpha: false });
      this.bg = document.createElement("canvas");
      this.bgCtx = this.bg.getContext("2d");
      this.snap = document.createElement("canvas");
      this.snapCtx = this.snap.getContext("2d");
    },

    /* cssW/cssH in CSS pixels. Backing store is clamped for performance. */
    resize(cssW, cssH, dpr) {
      let ratio = Math.min(dpr || 1, 2);
      const maxPixels = 2600000;
      if (cssW * cssH * ratio * ratio > maxPixels) ratio = Math.sqrt(maxPixels / (cssW * cssH));
      const bw = Math.max(1, Math.round(cssW * ratio));
      const bh = Math.max(1, Math.round(cssH * ratio));
      if (this.canvas.width !== bw || this.canvas.height !== bh) {
        this.canvas.width = bw;
        this.canvas.height = bh;
      }
      this.scale = bw / W;
      Isaac.game.bgDirty = true;
    },

    /* ---------------- cached background ---------------- */
    buildBg(game, room) {
      const s = this.scale;
      const bw = this.canvas.width, bh = this.canvas.height;
      if (this.bg.width !== bw || this.bg.height !== bh) { this.bg.width = bw; this.bg.height = bh; }
      const c = this.bgCtx;
      c.setTransform(s, 0, 0, s, 0, 0);
      c.imageSmoothingEnabled = false;
      const pal = PAL[Isaac.chapterOf(game.floorNum)];
      const rng = new Isaac.RNG(U.hash((game.floor ? game.floor.seed : 1) + ":bg:" + room.id));

      c.fillStyle = pal.wall;
      c.fillRect(0, 0, W, H);
      /* brick texture on the walls */
      c.fillStyle = pal.lo;
      for (let y = 0; y < H; y += 8) {
        const off = (y / 8) % 2 ? 8 : 0;
        for (let x = -8 + off; x < W; x += 16) {
          if (y >= T && y < H - T && x >= T && x < W - T) continue;
          c.fillRect(x, y, 1, 8);
        }
        c.fillRect(0, y, W, 1);
      }
      c.fillStyle = pal.hi;
      c.fillRect(T - 3, T - 3, W - 2 * T + 6, 3);
      c.fillRect(T - 3, T - 3, 3, H - 2 * T + 6);
      c.fillStyle = pal.lo;
      c.fillRect(T - 3, H - T, W - 2 * T + 6, 3);
      c.fillRect(W - T, T - 3, 3, H - 2 * T + 6);

      /* floor */
      for (let r = 0; r < IN_ROWS; r++) for (let q = 0; q < IN_COLS; q++) {
        c.fillStyle = (r + q) % 2 ? pal.floor : pal.floor2;
        c.fillRect((q + 1) * T, (r + 1) * T, T, T);
      }
      for (let i = 0; i < 70; i++) {
        c.fillStyle = rng.chance(0.5) ? "rgba(0,0,0,0.12)" : "rgba(255,255,255,0.05)";
        const sz = rng.int(1, 3);
        c.fillRect(rng.int(T, W - T - 3), rng.int(T, H - T - 3), sz, sz);
      }
      /* soft shadow along the inner wall edge */
      const grad = (x0, y0, x1, y1, x, y, w, h) => {
        const g = c.createLinearGradient(x0, y0, x1, y1);
        g.addColorStop(0, "rgba(0,0,0,0.35)");
        g.addColorStop(1, "rgba(0,0,0,0)");
        c.fillStyle = g;
        c.fillRect(x, y, w, h);
      };
      grad(0, T, 0, T + 14, T, T, W - 2 * T, 14);
      grad(0, H - T, 0, H - T - 10, T, H - T - 10, W - 2 * T, 10);
      grad(T, 0, T + 12, 0, T, T, 12, H - 2 * T);
      grad(W - T, 0, W - T - 12, 0, W - T - 12, T, 12, H - 2 * T);

      /* rocks */
      for (let r = 0; r < IN_ROWS; r++) for (let q = 0; q < IN_COLS; q++) {
        const t = room.tiles[r * IN_COLS + q];
        if (t) this.drawRock(c, (q + 1) * T, (r + 1) * T, t === 2, pal, rng);
      }
      this.bgFor = room;
      this.bgScale = s;
      game.bgDirty = false;
    },

    drawRock(c, x, y, tinted, pal, rng) {
      c.fillStyle = "rgba(0,0,0,0.3)";
      c.beginPath(); c.ellipse(x + 16, y + 26, 13, 5, 0, 0, TAU); c.fill();
      c.fillStyle = pal.rockLo;
      c.beginPath(); c.ellipse(x + 16, y + 18, 14, 12, 0, 0, TAU); c.fill();
      c.fillStyle = pal.rock;
      c.beginPath(); c.ellipse(x + 16, y + 16, 13, 11, 0, 0, TAU); c.fill();
      c.fillStyle = pal.rockHi;
      c.beginPath(); c.ellipse(x + 12, y + 11, 6, 4, -0.3, 0, TAU); c.fill();
      c.fillStyle = pal.rockLo;
      c.fillRect(x + 17 + rng.int(-2, 2), y + 14, 1, 7);
      c.fillRect(x + 17, y + 20, 5, 1);
      if (tinted) {
        c.fillStyle = "#b9dcf2";
        c.fillRect(x + 9, y + 15, 3, 3); c.fillRect(x + 20, y + 10, 4, 3); c.fillRect(x + 14, y + 20, 3, 3);
        c.fillStyle = "#6fa3c7";
        c.fillRect(x + 10, y + 16, 2, 1); c.fillRect(x + 21, y + 11, 2, 1);
      }
    },

    /* ---------------- frame ---------------- */
    render(game, dt) {
      this.t += dt || 0;
      const ctx = this.ctx, s = this.scale;
      ctx.setTransform(s, 0, 0, s, 0, 0);
      ctx.imageSmoothingEnabled = false;

      if (!game.room) { this.renderIdle(ctx); return; }
      if (game.bgDirty || this.bgFor !== game.room || this.bgScale !== s) this.buildBg(game, game.room);

      const tr = game.transition;
      if (tr) {
        const k = 1 - Math.pow(1 - U.clamp(tr.t / tr.dur, 0, 1), 3);
        const v = Isaac.DIR_VEC[tr.dir];
        const ox = -v[0] * W, oy = -v[1] * H;
        ctx.fillStyle = "#000";
        ctx.fillRect(0, 0, W, H);
        ctx.drawImage(this.snap, ox * k, oy * k, W, H);
        ctx.save();
        ctx.translate(-ox * (1 - k), -oy * (1 - k));
        this.drawRoom(ctx, game, true);
        ctx.restore();
      } else {
        ctx.save();
        if (game.shakeAmt > 0) ctx.translate(Isaac.vrng.float(-1, 1) * game.shakeAmt, Isaac.vrng.float(-1, 1) * game.shakeAmt);
        this.drawRoom(ctx, game, true);
        ctx.restore();
      }
      this.drawHUD(ctx, game);
      if (game.fade > 0) {
        ctx.fillStyle = `rgba(0,0,0,${game.fade})`;
        ctx.fillRect(0, 0, W, H);
      }
    },

    renderIdle(ctx) {
      const pal = PAL[0];
      ctx.fillStyle = pal.wall;
      ctx.fillRect(0, 0, W, H);
      for (let r = 0; r < IN_ROWS; r++) for (let q = 0; q < IN_COLS; q++) {
        ctx.fillStyle = (r + q) % 2 ? pal.floor : pal.floor2;
        ctx.fillRect((q + 1) * T, (r + 1) * T, T, T);
      }
      ctx.fillStyle = "rgba(0,0,0,0.55)";
      ctx.fillRect(0, 0, W, H);
    },

    snapshot(game) {
      if (this.snap.width !== this.canvas.width || this.snap.height !== this.canvas.height) {
        this.snap.width = this.canvas.width; this.snap.height = this.canvas.height;
      }
      const c = this.snapCtx, s = this.scale;
      c.setTransform(s, 0, 0, s, 0, 0);
      c.imageSmoothingEnabled = false;
      if (game.bgDirty || this.bgFor !== game.room || this.bgScale !== s) this.buildBg(game, game.room);
      this.drawRoom(c, game, false);
    },

    drawRoom(ctx, game, withPlayer) {
      const room = game.room;
      ctx.drawImage(this.bg, 0, 0, W, H);
      const t = this.t;

      for (const d of room.decals || []) {
        ctx.fillStyle = "rgba(15,8,4,0.45)";
        ctx.beginPath(); ctx.ellipse(d.x, d.y, 20, 13, 0, 0, TAU); ctx.fill();
      }
      for (const dir of Isaac.DIRS) if (room.doors[dir] !== null) this.drawDoor(ctx, game, room, dir);

      if (room.trapdoor) {
        const x = 7.5 * T, y = 3.1 * T;
        ctx.fillStyle = "#2b1b10"; ctx.fillRect(x - 15, y - 11, 30, 22);
        ctx.fillStyle = "#050302"; ctx.fillRect(x - 12, y - 8, 24, 16);
        ctx.fillStyle = "#4a3120"; ctx.fillRect(x - 15, y - 11, 30, 3);
      }

      for (const ped of room.pedestals) this.drawPedestal(ctx, ped, t);
      for (const sh of room.shop) this.drawShopSlot(ctx, sh, t, game.player);
      for (const pk of room.pickups) this.drawPickup(ctx, pk.type, pk.x, pk.y, t, true);

      for (const b of Isaac.bombs.pool.items) if (b.active) this.drawBomb(ctx, b, t);

      /* shadows first so bodies overlap them properly */
      const p = game.player;
      if (withPlayer && !p.dead) this.shadow(ctx, p.x, p.y + 8, 8, 3);
      for (const e of game.enemies) this.shadow(ctx, e.x, e.y + (e.boss ? 10 : e.r * 0.8), e.boss ? 22 : e.r * 0.9, e.boss ? 7 : 3, e);

      /* y-sorted bodies */
      const list = this.sortBuf || (this.sortBuf = []);
      list.length = 0;
      for (const e of game.enemies) list.push(e);
      if (withPlayer) list.push(p);
      list.sort((a, b) => a.y - b.y);
      for (const ent of list) {
        if (ent === p) this.drawPlayer(ctx, p, game, t);
        else this.drawEnemy(ctx, ent, t);
      }

      for (const tr of Isaac.projectiles.tears.items) if (tr.active) this.drawShot(ctx, tr, "#a9dcff", "#3f7fb6");
      for (const tr of Isaac.projectiles.eproj.items) if (tr.active) this.drawShot(ctx, tr, "#e84a4a", "#7c1212");

      for (const pa of Isaac.particles.pool.items) {
        if (!pa.active) continue;
        const a = U.clamp(pa.life / pa.max, 0, 1);
        if (pa.kind === 1) {
          ctx.globalAlpha = a;
          ctx.fillStyle = pa.color;
          ctx.beginPath(); ctx.arc(pa.x, pa.y, pa.size, 0, TAU); ctx.fill();
          ctx.globalAlpha = 1;
        } else if (pa.kind === 2) {
          ctx.globalAlpha = a * 0.8;
          ctx.fillStyle = pa.color;
          ctx.beginPath(); ctx.arc(pa.x, pa.y, pa.size, 0, TAU); ctx.fill();
          ctx.globalAlpha = 1;
        } else {
          ctx.globalAlpha = Math.min(1, a * 1.5);
          ctx.fillStyle = pa.color;
          ctx.fillRect(pa.x - pa.size / 2, pa.y - pa.size / 2, pa.size, pa.size);
          ctx.globalAlpha = 1;
        }
      }
    },

    shadow(ctx, x, y, rx, ry, e) {
      let k = 1;
      if (e && e.z) k = U.clamp(1 - e.z / 400, 0.45, 1);
      ctx.fillStyle = "rgba(0,0,0,0.35)";
      ctx.beginPath(); ctx.ellipse(x, y, rx * k, ry * k, 0, 0, TAU); ctx.fill();
      if (e && e.boss && (e.state === "jumpLock" || e.state === "jumpFall")) {
        /* landing marker flashes once the spot is locked in */
        if (Math.floor(this.t * 12) % 2 === 0) {
          ctx.strokeStyle = "rgba(255,60,60,0.9)";
          ctx.lineWidth = 2;
          ctx.beginPath(); ctx.ellipse(x, y, rx + 6, ry + 4, 0, 0, TAU); ctx.stroke();
        }
      }
    },

    /* ---------------- doors ---------------- */
    drawDoor(ctx, game, room, dir) {
      const [c, r] = Isaac.DOOR_TILE[dir];
      const neighbour = game.floor.rooms[room.doors[dir]];
      let kind = "normal";
      for (const k of ["boss", "treasure", "shop"]) if (neighbour.type === k || room.type === k) kind = k;
      const open = game.doorOpen(room, dir);
      const locked = game.doorLocked(room, dir);
      const angle = { n: 0, s: Math.PI, e: Math.PI / 2, w: -Math.PI / 2 }[dir];

      ctx.save();
      ctx.translate((c + 0.5) * T, (r + 0.5) * T);
      ctx.rotate(angle);
      const frame = kind === "boss" ? "#6d1010" : kind === "treasure" ? "#c9a227" : kind === "shop" ? "#8f8f99" : "#7b5433";
      const frameLo = kind === "boss" ? "#3a0707" : kind === "treasure" ? "#7d6414" : kind === "shop" ? "#55555d" : "#4a301c";
      ctx.fillStyle = frameLo; ctx.fillRect(-22, -16, 44, 38);
      ctx.fillStyle = frame; ctx.fillRect(-20, -16, 40, 35);
      if (kind === "boss") {
        ctx.fillStyle = "#e9e1d0";
        for (let i = -16; i <= 12; i += 7) { ctx.beginPath(); ctx.moveTo(i, -16); ctx.lineTo(i + 3, -21); ctx.lineTo(i + 6, -16); ctx.fill(); }
      }
      ctx.fillStyle = "#070403"; ctx.fillRect(-13, -12, 26, 34);
      if (open) {
        const g = ctx.createLinearGradient(0, -12, 0, 22);
        g.addColorStop(0, "#000"); g.addColorStop(1, "rgba(40,25,15,0.9)");
        ctx.fillStyle = g; ctx.fillRect(-13, -12, 26, 34);
      } else if (locked) {
        ctx.fillStyle = "#6b4423"; ctx.fillRect(-13, -12, 26, 34);
        ctx.fillStyle = "#4d2f17"; for (let i = -9; i < 13; i += 7) ctx.fillRect(i, -12, 1, 34);
        ctx.fillStyle = "#e7c24a"; ctx.fillRect(-6, 4, 12, 10);
        ctx.strokeStyle = "#e7c24a"; ctx.lineWidth = 2;
        ctx.beginPath(); ctx.arc(0, 4, 4, Math.PI, 0); ctx.stroke();
        ctx.fillStyle = "#5a4309"; ctx.fillRect(-1, 7, 2, 4);
      } else {
        for (let i = -11; i <= 9; i += 6) {
          ctx.fillStyle = "#3d3d42"; ctx.fillRect(i, -12, 4, 34);
          ctx.fillStyle = "#9a9aa3"; ctx.fillRect(i, -12, 2, 34);
        }
        ctx.fillStyle = "#6c6c74"; ctx.fillRect(-13, 2, 26, 3);
      }
      if (kind === "shop") {
        ctx.fillStyle = "#e0b83a"; ctx.beginPath(); ctx.arc(0, -19, 3, 0, TAU); ctx.fill();
      }
      ctx.restore();
    },

    /* ---------------- player ---------------- */
    drawPlayer(ctx, p, game, t) {
      if (p.dead) {
        ctx.fillStyle = "#e9c4a4";
        ctx.beginPath(); ctx.ellipse(p.x, p.y + 4, 12, 6, 0, 0, TAU); ctx.fill();
        ctx.fillStyle = "#1a0e08"; ctx.fillRect(p.x - 6, p.y + 2, 3, 1); ctx.fillRect(p.x + 3, p.y + 2, 3, 1);
        return;
      }
      if (p.invuln > 0 && Math.floor(t * 16) % 2 === 0) return;
      const items = p.items;
      const bigHead = items.includes("crickets_head");
      const x = p.x, y = p.y;
      const bob = (p.vx || p.vy) ? Math.sin(p.walkT) * 1 : 0;
      const skin = "#f1c7a6", skinLo = "#d59f7f";

      /* legs + body */
      ctx.fillStyle = skinLo;
      const step = (p.vx || p.vy) ? Math.sin(p.walkT) * 2 : 0;
      ctx.fillRect(x - 4, y + 4 + step * 0.3, 3, 4);
      ctx.fillRect(x + 1, y + 4 - step * 0.3, 3, 4);
      ctx.fillStyle = skin;
      ctx.beginPath(); ctx.ellipse(x, y + 2, 5.5, 5, 0, 0, TAU); ctx.fill();

      /* head */
      const hr = bigHead ? 11 : 9.5;
      const hy = y - 7 + bob;
      ctx.fillStyle = skinLo;
      ctx.beginPath(); ctx.arc(x, hy + 1, hr, 0, TAU); ctx.fill();
      ctx.fillStyle = skin;
      ctx.beginPath(); ctx.arc(x, hy, hr, 0, TAU); ctx.fill();
      if (bigHead) { ctx.fillStyle = "#3a2615"; ctx.fillRect(x - 5, hy - hr, 10, 3); }

      const f = p.face;
      if (f !== "up") {
        const ox = f === "left" ? -2.5 : f === "right" ? 2.5 : 0;
        const oy = f === "down" ? 1 : 0;
        const shooting = p.shootAnim > 0;
        ctx.fillStyle = "#18100b";
        const eyeH = shooting ? 2 : 4;
        ctx.fillRect(x - 5 + ox, hy - 2 + oy, 3, eyeH);
        ctx.fillRect(x + 2 + ox, hy - 2 + oy, 3, eyeH);
        ctx.fillStyle = "#fff";
        ctx.fillRect(x - 4 + ox, hy - 2 + oy, 1, 1);
        ctx.fillRect(x + 3 + ox, hy - 2 + oy, 1, 1);
        if (items.includes("sad_onion")) {
          ctx.fillStyle = "#7cc3f0";
          ctx.fillRect(x - 5 + ox, hy + 2 + oy, 2, 4);
          ctx.fillRect(x + 3 + ox, hy + 2 + oy, 2, 4);
        }
        if (items.includes("pentagram")) {
          ctx.fillStyle = "#b3171c";
          ctx.fillRect(x - 1, hy - hr + 3, 2, 2);
        }
        ctx.fillStyle = "#6b3a2a";
        ctx.fillRect(x - 2 + ox, hy + 4 + oy, 4, 1);
      } else {
        ctx.fillStyle = "#e3b393";
        ctx.fillRect(x - 2, hy - 3, 4, 3);
      }
      if (items.includes("wooden_spoon")) {
        ctx.fillStyle = "#b07a45"; ctx.fillRect(x + 6, y - 1, 2, 7);
        ctx.fillRect(x + 5, y - 3, 4, 3);
      }
    },

    /* ---------------- enemies ---------------- */
    drawEnemy(ctx, e, t) {
      const flash = e.hitFlash > 0;
      const col = (c) => (flash ? "#ffffff" : c);
      if (e.spawnT > 0 && !e.boss) {
        ctx.globalAlpha = U.clamp(1 - e.spawnT / 0.6, 0.15, 1);
      }
      switch (e.type) {
        case "fly": {
          const wing = Math.abs(Math.sin(t * 40 + e.phase));
          ctx.fillStyle = "rgba(230,230,240,0.75)";
          ctx.beginPath(); ctx.ellipse(e.x - 5, e.y - 6, 4, 2 + wing * 2, -0.5, 0, TAU); ctx.fill();
          ctx.beginPath(); ctx.ellipse(e.x + 5, e.y - 6, 4, 2 + wing * 2, 0.5, 0, TAU); ctx.fill();
          ctx.fillStyle = col("#3a0c0c");
          ctx.beginPath(); ctx.arc(e.x, e.y - 3, 5.5, 0, TAU); ctx.fill();
          ctx.fillStyle = col("#b0191d");
          ctx.beginPath(); ctx.arc(e.x, e.y - 3, 4, 0, TAU); ctx.fill();
          ctx.fillStyle = "#fff"; ctx.fillRect(e.x - 3, e.y - 5, 2, 2); ctx.fillRect(e.x + 1, e.y - 5, 2, 2);
          break;
        }
        case "clot": {
          const sq = (e.squash || 0) * 0.12;
          ctx.fillStyle = col("#5c0b0e");
          ctx.beginPath(); ctx.ellipse(e.x, e.y - 3, 11 * (1 + sq), 10 * (1 - sq), 0, 0, TAU); ctx.fill();
          ctx.fillStyle = col("#a3171c");
          ctx.beginPath(); ctx.ellipse(e.x, e.y - 4, 9.5 * (1 + sq), 8.5 * (1 - sq), 0, 0, TAU); ctx.fill();
          ctx.fillStyle = col("#d44045");
          ctx.beginPath(); ctx.ellipse(e.x - 3, e.y - 8, 3, 2, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#f5e6c8"; ctx.fillRect(e.x - 5, e.y - 5, 3, 3); ctx.fillRect(e.x + 2, e.y - 5, 3, 3);
          ctx.fillStyle = "#000"; ctx.fillRect(e.x - 4, e.y - 4, 1, 1); ctx.fillRect(e.x + 3, e.y - 4, 1, 1);
          break;
        }
        case "spitter": {
          const m = e.mouth || 0;
          ctx.fillStyle = col("#3f4a2c");
          ctx.beginPath(); ctx.ellipse(e.x, e.y - 1, 10, 9, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = col("#72804f");
          ctx.beginPath(); ctx.ellipse(e.x, e.y - 3, 9, 8 + m * 2, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#d83b3b"; ctx.fillRect(e.x - 5, e.y - 7, 3, 2); ctx.fillRect(e.x + 2, e.y - 7, 3, 2);
          ctx.fillStyle = "#1c0707";
          ctx.beginPath(); ctx.ellipse(e.x, e.y, 3 + m * 3, 1.5 + m * 3, 0, 0, TAU); ctx.fill();
          if (m > 0.2) { ctx.fillStyle = `rgba(255,70,70,${m * 0.6})`; ctx.beginPath(); ctx.arc(e.x, e.y, 5 + m * 4, 0, TAU); ctx.fill(); }
          break;
        }
        case "monstro": this.drawMonstro(ctx, e, t, col); break;
      }
      ctx.globalAlpha = 1;
    },

    drawMonstro(ctx, e, t, col) {
      const z = e.z || 0;
      if (z > 300) return;
      const sq = e.squash || 0;
      const sx = 1 + sq * 0.5, sy = 1 - sq * 0.5;
      const x = e.x, y = e.y - z - 10;
      let shake = 0;
      if (e.dying) shake = Math.sin(t * 60) * 2;
      ctx.save();
      ctx.translate(x + shake, y);
      ctx.scale(sx, sy);
      ctx.fillStyle = col("#8f3c3f");
      ctx.beginPath(); ctx.ellipse(0, 2, 27, 21, 0, 0, TAU); ctx.fill();
      ctx.fillStyle = col("#d98488");
      ctx.beginPath(); ctx.ellipse(0, 0, 25, 19, 0, 0, TAU); ctx.fill();
      ctx.fillStyle = col("#eaa6a8");
      ctx.beginPath(); ctx.ellipse(-8, -9, 9, 5, -0.3, 0, TAU); ctx.fill();
      ctx.fillStyle = "#1a0a0a";
      ctx.fillRect(-12, -8, 5, 4); ctx.fillRect(7, -8, 5, 4);
      const m = Math.max(0.15, e.mouth || 0);
      ctx.fillStyle = "#3d0507";
      ctx.beginPath(); ctx.ellipse(0, 7, 14, 3 + m * 8, 0, 0, TAU); ctx.fill();
      ctx.fillStyle = "#f2ead8";
      for (let i = -10; i <= 8; i += 5) ctx.fillRect(i, 4 - m * 3, 3, 3);
      if ((e.mouth || 0) > 0.3 && e.state === "spitWind" && Math.floor(t * 14) % 2 === 0) {
        ctx.fillStyle = "rgba(255,90,90,0.5)";
        ctx.beginPath(); ctx.ellipse(0, 7, 18, 6 + m * 8, 0, 0, TAU); ctx.fill();
      }
      ctx.restore();
    },

    drawShot(ctx, s, fill, edge) {
      const z = Math.max(0, s.z);
      ctx.fillStyle = "rgba(0,0,0,0.3)";
      ctx.beginPath(); ctx.ellipse(s.x, s.y + 6, s.r * 0.9, s.r * 0.4, 0, 0, TAU); ctx.fill();
      const y = s.y - z + 6;
      ctx.fillStyle = edge;
      ctx.beginPath(); ctx.arc(s.x, y, s.r + 0.8, 0, TAU); ctx.fill();
      ctx.fillStyle = fill;
      ctx.beginPath(); ctx.arc(s.x, y, s.r, 0, TAU); ctx.fill();
      ctx.fillStyle = "rgba(255,255,255,0.85)";
      ctx.fillRect(s.x - 2, y - 2, 2, 2);
    },

    drawBomb(ctx, b, t) {
      const lift = b.drop > 0 ? b.drop * 30 : 0;
      const blink = b.fuse < 0.5 && Math.floor(t * 20) % 2 === 0;
      const swell = b.fuse < 0.6 ? (0.6 - b.fuse) * 3 : 0;
      this.shadow(ctx, b.x, b.y + 5, 7, 2.5);
      ctx.fillStyle = blink ? "#d63a3a" : "#2c2c30";
      ctx.beginPath(); ctx.arc(b.x, b.y - 2 - lift, 7 + swell, 0, TAU); ctx.fill();
      ctx.fillStyle = "#6e6e76"; ctx.fillRect(b.x - 4, b.y - 6 - lift, 3, 2);
      ctx.fillStyle = "#8a6a3a"; ctx.fillRect(b.x + 2, b.y - 12 - lift, 2, 4);
      if (Math.floor(t * 18) % 2 === 0) { ctx.fillStyle = "#ffd23f"; ctx.fillRect(b.x + 1, b.y - 15 - lift, 4, 4); }
    },

    /* ---------------- pickups and items ---------------- */
    drawPickup(ctx, type, x, y, t, withShadow) {
      if (withShadow) this.shadow(ctx, x, y + 5, 6, 2);
      switch (type) {
        case "penny": case "nickel": case "dime": {
          const c = type === "penny" ? ["#8a5a1c", "#d69a45", "#f3c97a"] : type === "nickel" ? ["#6f7780", "#c3ccd6", "#eef3f8"] : ["#6c7fa0", "#aebfdf", "#ffffff"];
          const w = Math.abs(Math.cos(t * 3 + x)) * 4 + 2;
          ctx.fillStyle = c[0]; ctx.beginPath(); ctx.ellipse(x, y, w + 1, 6, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = c[1]; ctx.beginPath(); ctx.ellipse(x, y, w, 5, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = c[2]; ctx.fillRect(x - 1, y - 3, 1, 3);
          break;
        }
        case "key":
          ctx.fillStyle = "#8a6a14";
          ctx.fillRect(x - 6, y - 3, 6, 6); ctx.fillRect(x, y - 1, 8, 3); ctx.fillRect(x + 5, y + 1, 2, 3);
          ctx.fillStyle = "#f0cb4b";
          ctx.fillRect(x - 5, y - 2, 4, 4); ctx.fillRect(x, y - 1, 7, 2); ctx.fillRect(x + 5, y + 1, 1, 2);
          ctx.fillStyle = "#6a4f10"; ctx.fillRect(x - 4, y - 1, 2, 2);
          break;
        case "bomb":
          ctx.fillStyle = "#1f1f22"; ctx.beginPath(); ctx.arc(x, y, 6, 0, TAU); ctx.fill();
          ctx.fillStyle = "#56565e"; ctx.fillRect(x - 3, y - 4, 2, 2);
          ctx.fillStyle = "#8a6a3a"; ctx.fillRect(x + 2, y - 9, 2, 4);
          break;
        case "heart": case "halfheart": case "soulheart":
          this.heartShape(ctx, x, y - 1, type === "soulheart" ? "#b7cdf0" : "#e2262e", type === "soulheart" ? "#5d78a8" : "#7a0c10", type === "halfheart" ? 0.5 : 1);
          break;
        case "chest":
          ctx.fillStyle = "#6e4c0c"; ctx.fillRect(x - 9, y - 7, 18, 13);
          ctx.fillStyle = "#e0b43a"; ctx.fillRect(x - 8, y - 6, 16, 11);
          ctx.fillStyle = "#a47c1c"; ctx.fillRect(x - 8, y - 2, 16, 2);
          ctx.fillStyle = "#3a2a06"; ctx.fillRect(x - 1, y - 1, 3, 4);
          break;
      }
    },

    heartShape(ctx, x, y, fill, edge, portion) {
      const path = () => {
        ctx.beginPath();
        ctx.moveTo(x, y + 5);
        ctx.bezierCurveTo(x - 8, y - 1, x - 5, y - 7, x, y - 3);
        ctx.bezierCurveTo(x + 5, y - 7, x + 8, y - 1, x, y + 5);
        ctx.closePath();
      };
      ctx.fillStyle = edge; path(); ctx.fill();
      ctx.save();
      path(); ctx.clip();
      ctx.fillStyle = "#2a0d0e";
      ctx.fillRect(x - 8, y - 8, 16, 14);
      ctx.fillStyle = fill;
      ctx.fillRect(x - 8, y - 8, 16 * (portion >= 1 ? 1 : 0.5), 14);
      ctx.fillStyle = "rgba(255,255,255,0.55)";
      if (portion > 0) ctx.fillRect(x - 4, y - 4, 2, 2);
      ctx.restore();
    },

    drawPedestal(ctx, ped, t) {
      const x = ped.x, y = ped.y;
      this.shadow(ctx, x, y + 8, 13, 4);
      ctx.fillStyle = "#4e4943"; ctx.fillRect(x - 11, y - 2, 22, 11);
      ctx.fillStyle = "#8a847c"; ctx.fillRect(x - 12, y - 5, 24, 5);
      ctx.fillStyle = "#a9a298"; ctx.fillRect(x - 12, y - 5, 24, 1);
      if (!ped.taken) {
        const bob = Math.sin(t * 2.5) * 2;
        this.drawItemIcon(ctx, ped.item, x, y - 17 + bob, 1);
      }
    },

    drawShopSlot(ctx, s, t, p) {
      if (s.bought) return;
      if (s.kind === "item") this.drawItemIcon(ctx, s.item, s.x, s.y - 6 + Math.sin(t * 2.5) * 1.5, 1);
      else this.drawPickup(ctx, s.type, s.x, s.y - 4, t, true);
      const afford = p.coins >= s.price;
      this.text(ctx, s.price + "\u00A2", s.x, s.y + 16, 9, afford ? "#ffffff" : "#ff8080", "center");
    },

    drawItemIcon(ctx, id, x, y, k) {
      ctx.save();
      ctx.translate(x, y);
      if (k !== 1) ctx.scale(k, k);
      switch (id) {
        case "sad_onion":
          ctx.fillStyle = "#e8d9a8"; ctx.beginPath(); ctx.ellipse(0, 1, 7, 7, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#b9a468"; ctx.fillRect(-1, -9, 2, 4);
          ctx.fillStyle = "#18100b"; ctx.fillRect(-4, -1, 2, 2); ctx.fillRect(2, -1, 2, 2);
          ctx.fillStyle = "#6fc0f0"; ctx.fillRect(-4, 1, 2, 4); ctx.fillRect(2, 1, 2, 4);
          break;
        case "crickets_head":
          ctx.fillStyle = "#f1c7a6"; ctx.beginPath(); ctx.arc(0, 0, 8, 0, TAU); ctx.fill();
          ctx.fillStyle = "#3a2615"; ctx.fillRect(-6, -8, 12, 4);
          ctx.fillStyle = "#18100b"; ctx.fillRect(-4, -1, 2, 3); ctx.fillRect(2, -1, 2, 3);
          ctx.fillStyle = "#6b3a2a"; ctx.fillRect(-2, 4, 4, 1);
          break;
        case "pentagram": {
          ctx.strokeStyle = "#c1191f"; ctx.lineWidth = 1.6;
          ctx.beginPath(); ctx.arc(0, 0, 8, 0, TAU); ctx.stroke();
          ctx.beginPath();
          for (let i = 0; i < 5; i++) {
            const a = -Math.PI / 2 + i * (4 * Math.PI / 5);
            ctx[i ? "lineTo" : "moveTo"](Math.cos(a) * 7, Math.sin(a) * 7);
          }
          ctx.closePath(); ctx.stroke();
          break;
        }
        case "wooden_spoon":
          ctx.fillStyle = "#b07a45"; ctx.fillRect(-1, -2, 2, 10);
          ctx.beginPath(); ctx.ellipse(0, -5, 4, 5, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#8a5a2c"; ctx.beginPath(); ctx.ellipse(0, -5, 2.5, 3.5, 0, 0, TAU); ctx.fill();
          break;
        case "number_one":
          ctx.fillStyle = "#f4e04d"; ctx.beginPath(); ctx.arc(0, 1, 7, 0, TAU); ctx.fill();
          ctx.fillStyle = "#1a1a1a"; ctx.fillRect(-1, -4, 3, 10); ctx.fillRect(-3, -3, 2, 2); ctx.fillRect(-3, 5, 7, 1);
          break;
        case "breakfast":
          ctx.fillStyle = "#e8e8ee"; ctx.beginPath(); ctx.ellipse(0, 2, 9, 5, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#ffffff"; ctx.beginPath(); ctx.ellipse(-1, 1, 5, 3, 0, 0, TAU); ctx.fill();
          ctx.fillStyle = "#f5b82e"; ctx.beginPath(); ctx.arc(-1, 1, 2, 0, TAU); ctx.fill();
          break;
        case "yum_heart":
          this.heartShape(ctx, 0, 0, "#e2262e", "#7a0c10", 1);
          ctx.fillStyle = "#2a0d0e"; ctx.beginPath(); ctx.arc(5, -3, 2.5, 0, TAU); ctx.fill();
          break;
        case "the_d6":
          ctx.fillStyle = "#6b6b70"; ctx.fillRect(-7, -7, 14, 14);
          ctx.fillStyle = "#f4f4f4"; ctx.fillRect(-6, -6, 12, 12);
          ctx.fillStyle = "#1a1a1a";
          for (const [a, b] of [[-3, -3], [3, -3], [-3, 0], [3, 0], [-3, 3], [3, 3]]) ctx.fillRect(a - 1, b - 1, 2, 2);
          break;
        default:
          ctx.fillStyle = "#fff"; ctx.fillRect(-5, -5, 10, 10);
      }
      ctx.restore();
    },

    /* ---------------- HUD ---------------- */
    text(ctx, str, x, y, size, color, align) {
      ctx.font = `bold ${size}px ui-monospace, "Courier New", monospace`;
      ctx.textAlign = align || "left";
      ctx.textBaseline = "middle";
      ctx.fillStyle = "#000";
      ctx.fillText(str, x + 1, y + 1);
      ctx.fillText(str, x - 1, y + 1);
      ctx.fillText(str, x + 1, y - 1);
      ctx.fillText(str, x - 1, y - 1);
      ctx.fillStyle = color || "#fff";
      ctx.fillText(str, x, y);
    },

    drawHUD(ctx, game) {
      const p = game.player;
      if (!p) return;

      /* active item + charge */
      ctx.fillStyle = "rgba(0,0,0,0.5)";
      ctx.fillRect(3, 3, 26, 26);
      if (p.active) {
        const def = Isaac.ITEMS[p.active.id];
        const ready = p.active.charge >= def.charge;
        if (game.activeFlash > 0) { ctx.fillStyle = `rgba(255,255,255,${game.activeFlash})`; ctx.fillRect(3, 3, 26, 26); }
        this.drawItemIcon(ctx, p.active.id, 16, 16, 1);
        ctx.fillStyle = "#111"; ctx.fillRect(30, 3, 6, 26);
        const h = 24 * (p.active.charge / def.charge);
        ctx.fillStyle = ready ? (Math.floor(this.t * 4) % 2 ? "#fff36b" : "#ffd23f") : "#4fb3ff";
        ctx.fillRect(31, 28 - h, 4, h);
        ctx.fillStyle = "#111";
        for (let i = 1; i < def.charge; i++) ctx.fillRect(31, 4 + (24 / def.charge) * i, 4, 1);
      }

      /* hearts */
      let hx = 46, hy = 10, n = 0;
      const place = () => { const x = hx + (n % 6) * 13, y = hy + Math.floor(n / 6) * 12; n++; return [x, y]; };
      for (let i = 0; i < p.maxHearts; i++) {
        const [x, y] = place();
        const filled = U.clamp(p.red - i * 2, 0, 2);
        this.heartShape(ctx, x, y, "#e2262e", "#5a0a0d", filled / 2);
      }
      for (let i = 0; i < Math.ceil(p.soul / 2); i++) {
        const [x, y] = place();
        const filled = U.clamp(p.soul - i * 2, 0, 2);
        this.heartShape(ctx, x, y, "#b7cdf0", "#3e5680", filled / 2);
      }

      /* consumables */
      const rows = [["penny", p.coins], ["bomb", p.bombs], ["key", p.keys]];
      rows.forEach(([type, v], i) => {
        const y = 44 + i * 15;
        this.drawPickup(ctx, type, 10, y, 0, false);
        this.text(ctx, String(v).padStart(2, "0"), 20, y, 10, "#fff");
      });

      /* passive items */
      p.items.slice(-12).forEach((id, i) => this.drawItemIcon(ctx, id, 44 + i * 16, H - 13, 0.7));

      this.drawMinimap(ctx, game);

      const boss = game.boss();
      if (boss) {
        const bw = 180, bx = (W - bw) / 2, by = H - 15;
        ctx.fillStyle = "#000"; ctx.fillRect(bx - 2, by - 2, bw + 4, 10);
        ctx.fillStyle = "#3a0a0a"; ctx.fillRect(bx, by, bw, 6);
        ctx.fillStyle = "#d62b2b"; ctx.fillRect(bx, by, bw * U.clamp(boss.hp / boss.maxHp, 0, 1), 6);
        this.text(ctx, "Monstro", W / 2, by - 8, 9, "#ffd0d0", "center");
      }

      if (game.floorBanner) {
        const a = U.clamp(game.floorBanner.t, 0, 1);
        ctx.globalAlpha = a;
        ctx.fillStyle = "rgba(0,0,0,0.55)";
        ctx.fillRect(0, H / 2 - 22, W, 34);
        this.text(ctx, game.floorBanner.text, W / 2, H / 2 - 5, 18, "#f3e6c8", "center");
        ctx.globalAlpha = 1;
      }
      if (game.itemBanner) {
        const a = U.clamp(game.itemBanner.t, 0, 1);
        ctx.globalAlpha = a;
        ctx.fillStyle = "rgba(0,0,0,0.6)";
        ctx.fillRect(0, 50, W, 34);
        this.text(ctx, game.itemBanner.title, W / 2, 61, 13, "#ffffff", "center");
        this.text(ctx, game.itemBanner.sub, W / 2, 75, 9, "#d8c9a8", "center");
        ctx.globalAlpha = 1;
      }
    },

    drawMinimap(ctx, game) {
      const floor = game.floor;
      if (!floor) return;
      const bx = W - 94, by = 3, bw = 90, bh = 56;
      const cw = 11, ch = 8, gap = 2;
      ctx.fillStyle = "rgba(0,0,0,0.5)";
      ctx.fillRect(bx, by, bw, bh);
      ctx.save();
      ctx.beginPath(); ctx.rect(bx, by, bw, bh); ctx.clip();
      const cur = game.room;
      const cx = bx + bw / 2, cy = by + bh / 2;
      const known = new Set();
      for (const r of floor.rooms) {
        if (r.state === S.UNVISITED || r.state === S.LOCKED) continue;
        known.add(r.id);
        for (const d of Isaac.DIRS) if (r.doors[d] !== null) known.add(r.doors[d]);
      }
      for (const r of floor.rooms) {
        if (!known.has(r.id)) continue;
        const x = cx + (r.gx - cur.gx) * (cw + gap) - cw / 2;
        const y = cy + (r.gy - cur.gy) * (ch + gap) - ch / 2;
        const visited = r.state !== S.UNVISITED && r.state !== S.LOCKED;
        ctx.fillStyle = r === cur ? "#ffffff" : visited ? "#9b9b9b" : "#3d3d3d";
        ctx.fillRect(x, y, cw, ch);
        if (!visited) { ctx.strokeStyle = "#7a7a7a"; ctx.lineWidth = 1; ctx.strokeRect(x + 0.5, y + 0.5, cw - 1, ch - 1); }
        const ix = x + cw / 2, iy = y + ch / 2;
        if (r.type === "boss") { ctx.fillStyle = "#c01818"; ctx.fillRect(ix - 2, iy - 2, 4, 3); ctx.fillStyle = "#fff"; ctx.fillRect(ix - 1, iy - 1, 1, 1); ctx.fillRect(ix + 1, iy - 1, 1, 1); }
        else if (r.type === "treasure") { ctx.fillStyle = "#e8c23a"; ctx.fillRect(ix - 2, iy - 2, 5, 4); }
        else if (r.type === "shop") { ctx.fillStyle = "#e0b83a"; ctx.beginPath(); ctx.arc(ix, iy, 2.2, 0, TAU); ctx.fill(); }
        if (r.locked) { ctx.fillStyle = "#e7c24a"; ctx.fillRect(x + cw - 3, y, 3, 3); }
      }
      ctx.restore();
    }
  });
})(window.Isaac);

---

/* Tears and enemy shots. Both live in fixed pools; when a pool is full new
   shots are simply dropped, so projectiles can never pile up without limit.
   Each shot flies level at a fixed height, then drops once it has used most
   of its range, which gives the arcing look and a visible shadow. */
(function (Isaac) {
  "use strict";
  const C = Isaac.C;
  const K = Isaac.collide;
  const HEIGHT = 12;
  const GRAV = 520;

  const make = () => ({ x: 0, y: 0, vx: 0, vy: 0, z: HEIGHT, vz: 0, dmg: 0, traveled: 0, maxRange: 0, age: 0, owner: "player", r: 4 });
  const tears = new Isaac.Pool(C.MAX_TEARS, make);
  const eproj = new Isaac.Pool(C.MAX_EPROJ, make);

  function init(t, x, y, vx, vy, dmg, range, owner, r) {
    t.x = x; t.y = y; t.vx = vx; t.vy = vy;
    t.z = HEIGHT; t.vz = 0; t.dmg = dmg; t.traveled = 0; t.maxRange = range;
    t.age = 0; t.owner = owner; t.r = r;
    return t;
  }

  function step(t, dt) {
    const sp = Math.hypot(t.vx, t.vy);
    t.x += t.vx * dt; t.y += t.vy * dt;
    t.traveled += sp * dt;
    t.age += dt;
    if (t.traveled > t.maxRange * 0.72) {
      t.vz -= GRAV * dt;
      t.z += t.vz * dt;
    }
  }

  const PR = (Isaac.projectiles = {
    tears, eproj,

    spawnTear(x, y, vx, vy, dmg, range, owner) {
      const t = tears.obtain();
      if (!t) return false;
      init(t, x, y, vx, vy, dmg, range, owner, 4);
      return true;
    },

    spawnEnemyShot(x, y, vx, vy, range) {
      const t = eproj.obtain();
      if (!t) return false;
      init(t, x, y, vx, vy, 1, range || 260, "enemy", 4.5);
      return true;
    },

    update(game, dt) {
      const room = game.room;
      const enemies = game.enemies;
      const p = game.player;

      for (const t of tears.items) {
        if (!t.active) continue;
        step(t, dt);
        if (t.z <= 0 || t.age > 4 || K.solidPoint(room, t.x, t.y)) { PR.pop(tears, t, "#9fd4ff"); continue; }
        for (let i = 0; i < enemies.length; i++) {
          const e = enemies[i];
          if (e.dead || e.dying || e.spawnT > 0 || (e.z || 0) > 14) continue;
          const rr = e.r + t.r;
          const dx = e.x - t.x, dy = (e.y - (e.hitOffset || 0)) - t.y;
          if (dx * dx + dy * dy < rr * rr) {
            Isaac.enemies.damage(e, t.dmg, game, t.vx, t.vy);
            PR.pop(tears, t, "#9fd4ff");
            break;
          }
        }
      }

      for (const t of eproj.items) {
        if (!t.active) continue;
        step(t, dt);
        if (t.z <= 0 || t.age > 5 || K.solidPoint(room, t.x, t.y)) { PR.pop(eproj, t, "#d8383a"); continue; }
        if (!p.dead) {
          const rr = p.r - 1 + t.r;
          const dx = p.x - t.x, dy = (p.y - 3) - t.y;
          if (dx * dx + dy * dy < rr * rr) {
            if (Isaac.Player.hurt(p, 1, game) || p.invuln > 0) PR.pop(eproj, t, "#d8383a");
          }
        }
      }
    },

    pop(pool, t, color) {
      Isaac.particles.splash(t.x, t.y - Math.max(0, t.z), color);
      pool.release(t);
      if (pool === tears) Isaac.audio.play("splash");
    },

    clear() { tears.clear(); eproj.clear(); }
  });
})(window.Isaac);