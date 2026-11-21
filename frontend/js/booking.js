// Booking flow JS – Rapido/Uber style
let selectedPorterId = null;
let currentFare = 0;

const demoStations = [
  { stationId: 1, name: 'New Delhi Railway Station', code: 'NDLS' },
  { stationId: 2, name: 'Chhatrapati Shivaji Maharaj Terminus', code: 'CSTM' },
  { stationId: 3, name: 'Chennai Central', code: 'MAS' },
  { stationId: 4, name: 'Howrah Junction', code: 'HWH' },
  { stationId: 5, name: 'Bengaluru City Junction', code: 'SBC' }
];

const demoLocations = {
  1: [
    { locationId: 1, name: 'Main Entrance (Ajmeri Gate)', type: 'ENTRANCE' },
    { locationId: 2, name: 'Platform 1-2', type: 'PLATFORM' },
    { locationId: 3, name: 'Platform 3-4', type: 'PLATFORM' },
    { locationId: 4, name: 'Platform 5-6', type: 'PLATFORM' },
    { locationId: 6, name: 'Parking Area A', type: 'PARKING' },
    { locationId: 7, name: 'Waiting Hall (Upper)', type: 'WAITING_HALL' }
  ],
  2: [
    { locationId: 9, name: 'Main Entrance', type: 'ENTRANCE' },
    { locationId: 10, name: 'Platform 1-3', type: 'PLATFORM' },
    { locationId: 11, name: 'Platform 4-6', type: 'PLATFORM' }
  ],
  3: [
    { locationId: 15, name: 'Main Entrance', type: 'ENTRANCE' },
    { locationId: 16, name: 'Platform 1-2', type: 'PLATFORM' },
    { locationId: 17, name: 'Platform 3-5', type: 'PLATFORM' }
  ]
};

const demoServices = [
  { serviceTypeId: 1, name: 'Basic Porter', baseFare: 30, perBagCharge: 10, heavyCharge: 0 },
  { serviceTypeId: 2, name: 'Heavy Luggage Assistance', baseFare: 40, perBagCharge: 15, heavyCharge: 20 },
  { serviceTypeId: 3, name: 'Premium Assistance', baseFare: 60, perBagCharge: 20, heavyCharge: 25 }
];

const demoPorters = [
  { porterId: 1, fullName: 'Ramesh Yadav', rating: 4.7, experience: 8, phone: '9123456780' },
  { porterId: 2, fullName: 'Suresh Kumar', rating: 4.5, experience: 5, phone: '9123456781' },
  { porterId: 3, fullName: 'Mahesh Patel', rating: 4.9, experience: 12, phone: '9123456782' }
];

document.addEventListener('DOMContentLoaded', () => {
  loadStations();
  loadServices();

  document.getElementById('stationId').addEventListener('change', function() {
    loadLocations(this.value);
  });

  ['numBags', 'serviceTypeId', 'isHeavy'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('change', updateFare);
  });
});

function loadStations() {
  const sel = document.getElementById('stationId');
  fetch('../api/data/stations')
    .then(r => r.json())
    .then(data => {
      if (data.success) fillStations(data.stations);
      else fillStations(demoStations);
    })
    .catch(() => fillStations(demoStations));
}

function fillStations(list) {
  const sel = document.getElementById('stationId');
  sel.innerHTML = '<option value="">Select station...</option>';
  list.forEach(s => {
    sel.innerHTML += `<option value="${s.stationId || s.station_id}">${s.name || s.station_name}</option>`;
  });
}

function loadLocations(stationId) {
  const pickup = document.getElementById('pickupId');
  const drop = document.getElementById('dropId');
  pickup.disabled = false;
  drop.disabled = false;

  fetch('../api/data/locations/' + stationId)
    .then(r => r.json())
    .then(data => {
      if (data.success) fillLocations(data.locations);
      else fillLocations(demoLocations[stationId] || demoLocations[1]);
    })
    .catch(() => fillLocations(demoLocations[stationId] || demoLocations[1]));
}

function fillLocations(list) {
  const pickup = document.getElementById('pickupId');
  const drop = document.getElementById('dropId');
  let opts = '<option value="">Select...</option>';
  list.forEach(l => {
    opts += `<option value="${l.locationId || l.location_id}">${l.name || l.location_name}</option>`;
  });
  pickup.innerHTML = opts;
  drop.innerHTML = opts;
}

function loadServices() {
  const sel = document.getElementById('serviceTypeId');
  fetch('../api/data/services')
    .then(r => r.json())
    .then(data => {
      if (data.success) fillServices(data.services);
      else fillServices(demoServices);
    })
    .catch(() => fillServices(demoServices));
}

function fillServices(list) {
  const sel = document.getElementById('serviceTypeId');
  sel.innerHTML = '';
  list.forEach(s => {
    sel.innerHTML += `<option value="${s.serviceTypeId}" data-base="${s.baseFare}" data-bag="${s.perBagCharge}" data-heavy="${s.heavyCharge}">${s.name} (Base ₹${s.baseFare})</option>`;
  });
  updateFare();
}

function updateFare() {
  const sel = document.getElementById('serviceTypeId');
  const opt = sel.options[sel.selectedIndex];
  if (!opt) return;
  const base = parseFloat(opt.dataset.base || 30);
  const bag = parseFloat(opt.dataset.bag || 10);
  const heavy = parseFloat(opt.dataset.heavy || 0);
  const num = parseInt(document.getElementById('numBags').value) || 1;
  const isHeavy = document.getElementById('isHeavy').checked;
  let total = base + (num * bag);
  if (isHeavy) total += heavy;
  currentFare = total;
  document.getElementById('fareAmount').textContent = '₹' + total.toFixed(0);
  document.getElementById('fareBreakdown').innerHTML =
    `Base ₹${base} + ${num} bag(s) × ₹${bag}` + (isHeavy ? ` + Heavy ₹${heavy}` : '');
}

function goStep(n) {
  if (n === 2) {
    if (!document.getElementById('stationId').value || !document.getElementById('pickupId').value || !document.getElementById('dropId').value) {
      alert('Please select station, pickup and drop locations');
      return;
    }
  }
  if (n === 3) {
    updateFare();
    loadPorters();
  }

  for (let i = 1; i <= 4; i++) {
    document.getElementById('step' + i).classList.add('hidden');
    document.getElementById('ws' + i).classList.remove('active', 'done');
  }
  document.getElementById('step' + n).classList.remove('hidden');
  for (let i = 1; i < n; i++) document.getElementById('ws' + i).classList.add('done');
  document.getElementById('ws' + n).classList.add('active');
}

function loadPorters() {
  const stationId = document.getElementById('stationId').value;
  const listEl = document.getElementById('portersList');
  listEl.innerHTML = '<div class="spinner"></div>';

  fetch('../api/data/porters/' + stationId)
    .then(r => r.json())
    .then(data => {
      if (data.success && data.porters.length) renderPorters(data.porters);
      else renderPorters(demoPorters);
    })
    .catch(() => renderPorters(demoPorters));
}

function renderPorters(list) {
  const listEl = document.getElementById('portersList');
  if (!list.length) {
    listEl.innerHTML = '<p>No porters available right now. Try again shortly.</p>';
    return;
  }
  let html = `<p style="margin-bottom:1rem;color:var(--gray-500);">Select a porter or let us auto-assign the best available one.</p>`;
  list.forEach(p => {
    html += `<div class="porter-card" data-id="${p.porterId}" onclick="selectPorter(this, ${p.porterId})">
      <div class="avatar">${p.fullName.charAt(0)}</div>
      <div style="flex:1;">
        <strong>${p.fullName}</strong><br>
        <span style="font-size:0.85rem;color:var(--gray-500);">⭐ ${p.rating || p.average_rating || '4.5'} · ${p.experience || p.experience_years || 5} yrs exp</span>
      </div>
      <span class="badge badge-success">Available</span>
    </div>`;
  });
  html += `<div class="porter-card selected" data-id="auto" onclick="selectPorter(this, 'auto')">
    <div class="avatar">⚡</div>
    <div style="flex:1;"><strong>Auto Assign Best Porter</strong><br>
    <span style="font-size:0.85rem;color:var(--gray-500);">System will assign highest-rated available porter</span></div>
  </div>`;
  listEl.innerHTML = html;
  selectedPorterId = 'auto';
  document.getElementById('confirmBtn').disabled = false;
}

function selectPorter(el, id) {
  document.querySelectorAll('.porter-card').forEach(c => c.classList.remove('selected'));
  el.classList.add('selected');
  selectedPorterId = id;
  document.getElementById('confirmBtn').disabled = false;
}

function confirmBooking() {
  const formData = new FormData();
  formData.append('action', 'create');
  formData.append('stationId', document.getElementById('stationId').value);
  formData.append('pickupId', document.getElementById('pickupId').value);
  formData.append('dropId', document.getElementById('dropId').value);
  formData.append('serviceTypeId', document.getElementById('serviceTypeId').value);
  formData.append('numBags', document.getElementById('numBags').value);
  formData.append('luggageType', document.getElementById('luggageType').value);
  formData.append('weight', document.getElementById('weight').value);
  formData.append('isHeavy', document.getElementById('isHeavy').checked ? '1' : '0');
  formData.append('instructions', document.getElementById('instructions').value);

  const btn = document.getElementById('confirmBtn');
  btn.disabled = true;
  btn.textContent = 'Booking...';

  fetch('../api/booking', { method: 'POST', body: formData })
    .then(r => r.json())
    .then(data => {
      if (data.success) {
        showTracking(data.bookingCode || 'SP' + Date.now(), data.estimatedFare || currentFare);
      } else {
        // Demo success
        showTracking('SP' + Date.now().toString().slice(-8), currentFare);
      }
    })
    .catch(() => {
      showTracking('SP' + Date.now().toString().slice(-8), currentFare);
    });
}

function showTracking(code, fare) {
  goStep(4);
  document.getElementById('bookingCode').textContent = code;
  document.getElementById('finalFare').textContent = fare;
  // Simulate status progression
  const statuses = ['SEARCHING', 'PORTER_ASSIGNED', 'PORTER_ACCEPTED', 'PORTER_ARRIVING', 'SERVICE_STARTED', 'SERVICE_COMPLETED'];
  let i = 0;
  const interval = setInterval(() => {
    i++;
    if (i >= statuses.length) {
      clearInterval(interval);
      return;
    }
    document.querySelectorAll('.status-item').forEach(el => {
      el.classList.remove('active');
      if (statuses.indexOf(el.dataset.status) < i) el.classList.add('done');
      if (el.dataset.status === statuses[i]) el.classList.add('active');
    });
  }, 2500);
}