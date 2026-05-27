import { spawn } from "node:child_process";
import { existsSync } from "node:fs";
import { mkdir, rm, writeFile } from "node:fs/promises";
import path from "node:path";

const baseUrl = (process.env.DEMO_BASE_URL || "http://localhost:18182/eventsphere").replace(/\/$/, "");
const outputDir = path.resolve(process.env.DEMO_SCREENSHOT_DIR || "docs/screenshots/demo");
const eventId = process.env.DEMO_EVENT_ID || "1";
const width = Number(process.env.DEMO_SCREENSHOT_WIDTH || 1440);
const height = Number(process.env.DEMO_SCREENSHOT_HEIGHT || 1000);

const chromeCandidates = [
  process.env.CHROME_PATH,
  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
  "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
  "/usr/bin/google-chrome",
  "/usr/bin/chromium",
  "/usr/bin/chromium-browser"
].filter(Boolean);

const chromePath = chromeCandidates.find((candidate) => existsSync(candidate));

if (!chromePath) {
  throw new Error("Chrome or Edge was not found. Set CHROME_PATH to the browser executable.");
}

const publicPages = [
  ["home", "/"],
  ["events", "/events"],
  ["event-details", `/events/${eventId}`],
  ["about", "/about"],
  ["contact", "/contact"],
  ["certificate-verify", "/certificate-verify"],
  ["login", "/auth/login"],
  ["register", "/auth/register"]
];

const roleSuites = [
  {
    role: "admin",
    email: "admin@eventsphere.com",
    password: "admin123",
    pages: [
      ["admin-dashboard", "/admin/dashboard"],
      ["admin-users", "/admin/users"]
    ]
  },
  {
    role: "organizer",
    email: "organizer@eventsphere.com",
    password: "organizer123",
    pages: [
      ["organizer-dashboard", "/organizer/dashboard"],
      ["organizer-new-event", "/organizer/events/new"],
      ["organizer-registrations", `/organizer/events/${eventId}/registrations`],
      ["organizer-attendance", `/organizer/events/${eventId}/attendance`],
      ["organizer-feedback", `/organizer/events/${eventId}/feedback`]
    ]
  },
  {
    role: "student",
    email: "student1@eventsphere.com",
    password: "student123",
    pages: [
      ["student-dashboard", "/student/dashboard"],
      ["student-registrations", "/student/my-registrations"],
      ["student-certificates", "/student/certificates"]
    ]
  },
  {
    role: "volunteer",
    email: "volunteer@eventsphere.com",
    password: "volunteer123",
    pages: [
      ["volunteer-dashboard", "/volunteer/dashboard"],
      ["volunteer-attendance", "/volunteer/attendance"]
    ]
  }
];

class CdpClient {
  constructor(wsUrl) {
    this.nextId = 1;
    this.callbacks = new Map();
    this.listeners = new Map();
    this.ws = new WebSocket(wsUrl);
    this.ready = new Promise((resolve, reject) => {
      this.ws.addEventListener("open", resolve, { once: true });
      this.ws.addEventListener("error", reject, { once: true });
    });

    this.ws.addEventListener("message", (event) => {
      const message = JSON.parse(event.data);
      if (message.id && this.callbacks.has(message.id)) {
        const { resolve, reject } = this.callbacks.get(message.id);
        this.callbacks.delete(message.id);
        if (message.error) {
          reject(new Error(message.error.message));
        } else {
          resolve(message.result || {});
        }
        return;
      }

      const listeners = this.listeners.get(message.method) || [];
      for (const listener of listeners) {
        listener(message.params || {});
      }
    });
  }

  async send(method, params = {}) {
    await this.ready;
    const id = this.nextId++;
    const payload = JSON.stringify({ id, method, params });
    return new Promise((resolve, reject) => {
      this.callbacks.set(id, { resolve, reject });
      this.ws.send(payload);
    });
  }

  waitFor(method, timeoutMs = 15000) {
    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        cleanup();
        reject(new Error(`Timed out waiting for ${method}`));
      }, timeoutMs);

      const listener = (params) => {
        cleanup();
        resolve(params);
      };

      const cleanup = () => {
        clearTimeout(timeout);
        const listeners = this.listeners.get(method) || [];
        this.listeners.set(method, listeners.filter((item) => item !== listener));
      };

      const listeners = this.listeners.get(method) || [];
      listeners.push(listener);
      this.listeners.set(method, listeners);
    });
  }

  close() {
    this.ws.close();
  }
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function fetchJson(url, attempts = 60) {
  let lastError;
  for (let attempt = 0; attempt < attempts; attempt++) {
    try {
      const response = await fetch(url);
      if (response.ok) {
        return response.json();
      }
      lastError = new Error(`${response.status} ${response.statusText}`);
    } catch (error) {
      lastError = error;
    }
    await sleep(250);
  }
  throw lastError;
}

async function launchBrowser(label) {
  const port = 9300 + Math.floor(Math.random() * 500);
  const userDataDir = path.join("C:\\tmp", `eventsphere-demo-${label}-${Date.now()}`);
  await rm(userDataDir, { recursive: true, force: true });

  const browser = spawn(chromePath, [
    "--headless=new",
    "--disable-gpu",
    "--no-first-run",
    "--no-default-browser-check",
    "--hide-scrollbars",
    `--window-size=${width},${height}`,
    `--remote-debugging-port=${port}`,
    `--user-data-dir=${userDataDir}`,
    "about:blank"
  ], {
    stdio: "ignore",
    windowsHide: true
  });

  const targets = await fetchJson(`http://127.0.0.1:${port}/json/list`);
  const page = targets.find((target) => target.type === "page");
  const client = new CdpClient(page.webSocketDebuggerUrl);
  await client.send("Page.enable");
  await client.send("Runtime.enable");
  await client.send("Emulation.setDeviceMetricsOverride", {
    width,
    height,
    deviceScaleFactor: 1,
    mobile: false
  });

  return {
    client,
    async close() {
      client.close();
      if (browser.exitCode === null) {
        browser.kill();
        await Promise.race([
          new Promise((resolve) => browser.once("exit", resolve)),
          sleep(3000)
        ]);
      }

      for (let attempt = 0; attempt < 5; attempt++) {
        try {
          await rm(userDataDir, { recursive: true, force: true });
          break;
        } catch (error) {
          if (error.code === "EBUSY" && attempt === 4) {
            console.warn(`Skipped locked temporary Chrome profile cleanup: ${userDataDir}`);
            break;
          }
          if (error.code !== "EBUSY") {
            throw error;
          }
          await sleep(500);
        }
      }
    }
  };
}

async function navigate(client, route) {
  const load = client.waitFor("Page.loadEventFired").catch(() => null);
  await client.send("Page.navigate", { url: `${baseUrl}${route}` });
  await load;
  await sleep(900);
}

async function evaluate(client, expression) {
  const result = await client.send("Runtime.evaluate", {
    expression,
    returnByValue: true,
    awaitPromise: true
  });
  return result.result?.value;
}

async function assertHealthyPage(client, name) {
  const info = await evaluate(client, `({
    href: location.href,
    title: document.title,
    text: document.body ? document.body.innerText.slice(0, 1000) : ""
  })`);

  if (!info || /Whitelabel Error Page|HTTP Status 500|There was an unexpected error/i.test(info.text)) {
    throw new Error(`${name} rendered an error page at ${info?.href || "unknown URL"}`);
  }

  if (/\/auth\/login/.test(info.href) && !["login", "register"].includes(name)) {
    throw new Error(`${name} redirected to login instead of rendering the expected protected page`);
  }

  return info;
}

async function screenshot(client, name, route, manifest) {
  await navigate(client, route);
  const info = await assertHealthyPage(client, name);
  const image = await client.send("Page.captureScreenshot", {
    format: "png",
    fromSurface: true,
    captureBeyondViewport: false
  });
  const file = `${name}.png`;
  await writeFile(path.join(outputDir, file), image.data, "base64");
  manifest.push({
    name,
    route,
    url: `${baseUrl}${route}`,
    title: info.title,
    file
  });
  console.log(`saved ${file}`);
}

async function login(client, email, password) {
  await navigate(client, "/auth/login");
  await evaluate(client, `(() => {
    document.getElementById("email").value = ${JSON.stringify(email)};
    document.getElementById("password").value = ${JSON.stringify(password)};
    document.querySelector("form").requestSubmit();
  })()`);
  await sleep(1500);
  const href = await evaluate(client, "location.href");
  if (/error=true/.test(href)) {
    throw new Error(`Login failed for ${email}`);
  }
}

await mkdir(outputDir, { recursive: true });
const manifest = [];

const publicBrowser = await launchBrowser("public");
try {
  for (const [name, route] of publicPages) {
    await screenshot(publicBrowser.client, name, route, manifest);
  }
} finally {
  await publicBrowser.close();
}

for (const suite of roleSuites) {
  const browser = await launchBrowser(suite.role);
  try {
    await login(browser.client, suite.email, suite.password);
    for (const [name, route] of suite.pages) {
      await screenshot(browser.client, name, route, manifest);
    }
  } finally {
    await browser.close();
  }
}

await writeFile(
  path.join(outputDir, "manifest.json"),
  `${JSON.stringify({ baseUrl, capturedAt: new Date().toISOString(), screenshots: manifest }, null, 2)}\n`
);

console.log(`Captured ${manifest.length} screenshots in ${outputDir}`);
