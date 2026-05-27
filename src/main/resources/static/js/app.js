// ===== PAGE NAV =====
function showPage(id) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
  const page = document.getElementById('page-' + id);
  if (page) { page.classList.add('active'); page.scrollIntoView({behavior:'instant'}); window.scrollTo(0,0); }
  const tabs = document.querySelectorAll('.nav-tab');
  const map = {landing:0,login:1,register:2,student:3,events:4,eventdetail:5,organizer:6,create:7,volunteer:8,qr:9,certificates:10,feedback:11,admin:12,profile:13,notifications:14,components:15};
  if (map[id] !== undefined && tabs[map[id]]) tabs[map[id]].classList.add('active');
}

// ===== TOAST =====
function showToast(msg, type) {
  const c = document.getElementById('toastContainer');
  const t = document.createElement('div');
  t.className = 'toast' + (type ? ' toast-'+type : '');
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

// ===== CONFETTI =====
function fireConfetti() {
  const c = document.getElementById('confettiContainer');
  const colors = ['#4F46E5','#06B6D4','#10B981','#F59E0B','#EF4444','#7C3AED','#EC4899'];
  for (let i = 0; i < 60; i++) {
    const p = document.createElement('div');
    p.className = 'confetti-piece';
    p.style.cssText = `
      left:${Math.random()*100}%;
      top:${Math.random()*20}%;
      background:${colors[Math.floor(Math.random()*colors.length)]};
      animation-delay:${Math.random()*0.8}s;
      animation-duration:${1.2+Math.random()*0.8}s;
      width:${6+Math.random()*8}px;
      height:${6+Math.random()*8}px;
      transform:rotate(${Math.random()*360}deg);
    `;
    c.appendChild(p);
    setTimeout(() => p.remove(), 2500);
  }
}

// ===== REGISTER STEPS =====
function goStep(n) {
  document.querySelectorAll('.register-step').forEach(s => s.classList.remove('active'));
  document.getElementById('step'+n).classList.add('active');
  [1,2,3].forEach(i => {
    const sc = document.getElementById('sc'+i);
    const sl = document.getElementById('sl'+i);
    if (sc) { sc.className = 'step-circle ' + (i < n ? 'done' : i === n ? 'active' : 'upcoming'); }
    if (sl) { sl.className = 'step-label' + (i <= n ? ' active' : ''); }
    if (i < 3) { const conn = document.getElementById('conn'+i); if(conn) conn.className = 'step-connector' + (i < n ? ' done' : ''); }
  });
  if (n === 1) { document.getElementById('sc1').textContent = '1'; }
  if (n === 2) { document.getElementById('sc1').textContent = '✓'; }
  if (n === 3) { document.getElementById('sc1').textContent = '✓'; document.getElementById('sc2').textContent = '✓'; }
}

// ===== ROLE SELECTION =====
function selectRole(el) {
  document.querySelectorAll('.role-card').forEach(c => c.classList.remove('selected'));
  el.classList.add('selected');
}

// ===== FAQ TOGGLE =====
function toggleFaq(el) {
  const ans = el.nextElementSibling;
  ans.classList.toggle('open');
}

// ===== PASSWORD TOGGLE =====
function togglePassword() {
  const inp = document.getElementById('passwordInput');
  inp.type = inp.type === 'password' ? 'text' : 'password';
}

// ===== COUNTDOWN TIMER =====
function updateCountdown() {
  const d = document.getElementById('cd-days');
  const h = document.getElementById('cd-hours');
  const m = document.getElementById('cd-mins');
  const s = document.getElementById('cd-secs');
  if (!d || !h || !m || !s) return;
  let secs = parseInt(s.textContent);
  let mins = parseInt(m.textContent);
  let hrs = parseInt(h.textContent);
  let days = parseInt(d.textContent);
  secs--;
  if (secs < 0) { secs = 59; mins--; }
  if (mins < 0) { mins = 59; hrs--; }
  if (hrs < 0) { hrs = 23; days--; }
  const f = n => String(n).padStart(2,'0');
  d.textContent = f(days);
  h.textContent = f(hrs);
  m.textContent = f(mins);
  s.textContent = f(secs);
}
setInterval(updateCountdown, 1000);

// ===== SIMULATE QR SCAN =====
const names = ['Aarav Patel','Diya Sharma','Karan Mehta','Priya Singh','Rahul Verma','Sneha Kumar','Vijay Rao','Ananya Joshi'];
let scanIdx = 0;
function simulateScan() {
  const name = names[scanIdx % names.length];
  scanIdx++;
  const count = document.getElementById('scanCount');
  if (count) count.textContent = 147 + scanIdx;
  showToast('✅ ' + name + ' checked in!', 'success');
  // Add to recent list
  const card = document.getElementById('qrParticipantCard');
  if (card) {
    card.querySelector('.qr-participant-name').textContent = name;
setInterval(updateCountdown, 1000);

// ===== SIMULATE QR SCAN =====
const names = ['Aarav Patel','Diya Sharma','Karan Mehta','Priya Singh','Rahul Verma','Sneha Kumar','Vijay Rao','Ananya Joshi'];
let scanIdx = 0;
function simulateScan() {
  const name = names[scanIdx % names.length];
  scanIdx++;
  const count = document.getElementById('scanCount');
  if (count) count.textContent = 147 + scanIdx;
  showToast('✅ ' + name + ' checked in!', 'success');
  // Add to recent list
  const card = document.getElementById('qrParticipantCard');
  if (card) {
    card.querySelector('.qr-participant-name').textContent = name;
    card.querySelector('.qr-participant-info').textContent = '2021CS' + (1234 + scanIdx) + ' · IIT Delhi · CSE';
    card.querySelector('.qr-status').className = 'qr-status present';
    card.querySelector('.qr-status').textContent = '✅ Marked Present · ' + new Date().toLocaleTimeString('en-IN',{hour:'2-digit',minute:'2-digit'});
  }
}

// ===== STAR RATING =====
function rateStar(n) {
  const stars = document.querySelectorAll('#starRating .star');
  stars.forEach((s,i) => { s.className = 'star' + (i < n ? ' filled' : ''); });
}

// ===== RIPPLE EFFECT =====
document.addEventListener('click', function(e) {
  const btn = e.target.closest('.btn');
  if (!btn) return;
  const ripple = document.createElement('span');
  ripple.className = 'ripple';
  const rect = btn.getBoundingClientRect();
  const size = Math.max(rect.width, rect.height);
  ripple.style.cssText = `width:${size}px;height:${size}px;left:${e.clientX-rect.left-size/2}px;top:${e.clientY-rect.top-size/2}px`;
  btn.appendChild(ripple);
  setTimeout(() => ripple.remove(), 600);
});

// ===== SMOOTH FORM SUBMIT (loading state) =====
document.addEventListener('submit', function(e) {
  const form = e.target;
  const btn = form.querySelector('button[type="submit"], .btn-submit');
  if (btn && !btn.classList.contains('no-loading')) {
    btn.classList.add('loading');
    btn.disabled = true;
    // Re-enable after 8s as safety fallback
    setTimeout(() => { btn.classList.remove('loading'); btn.disabled = false; }, 8000);
  }
});

// ===== AUTO-DISMISS FLASH MESSAGES =====
document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.flash-alert, .alert-success, .alert-danger').forEach(function(el) {
    setTimeout(() => { el.style.transition='opacity 0.5s'; el.style.opacity='0'; setTimeout(()=>el.remove(),500); }, 5000);
  });

  // Stagger animation on event card grids
  document.querySelectorAll('.event-card-grid, .stagger-children').forEach(function(grid) {
    grid.querySelectorAll(':scope > *').forEach(function(child, i) {
      child.style.animationDelay = (i * 0.07) + 's';
    });
  });

  // Count-up numbers
  document.querySelectorAll('[data-count]').forEach(function(el) {
    const target = parseInt(el.dataset.count, 10);
    let current = 0;
    const step = Math.ceil(target / 40);
    const timer = setInterval(function() {
      current = Math.min(current + step, target);
      el.textContent = current.toLocaleString();
      if (current >= target) clearInterval(timer);
    }, 30);
  });
});
