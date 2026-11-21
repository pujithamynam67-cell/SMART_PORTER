// Passenger Dashboard JS
document.addEventListener('DOMContentLoaded', () => {
  const name = sessionStorage.getItem('name') || 'Passenger';
  document.getElementById('userName').textContent = name;
  document.getElementById('avatar').textContent = name.charAt(0).toUpperCase();

  // Demo data fallback
  const demoBookings = [
    { bookingCode: 'SP20261120001', stationName: 'New Delhi', status: 'SERVICE_COMPLETED', estimatedFare: 50, bookedAt: '2026-11-15' },
    { bookingCode: 'SP20261120007', stationName: 'New Delhi', status: 'SERVICE_COMPLETED', estimatedFare: 70, bookedAt: '2026-11-18' },
    { bookingCode: 'SP20261120016', stationName: 'New Delhi', status: 'PORTER_ARRIVING', estimatedFare: 40, bookedAt: '2026-11-20' }
  ];

  function renderBookings(list) {
    const tbody = document.getElementById('recentBody');
    const historyBody = document.getElementById('historyBody');
    if (!list || list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No bookings yet. <a href="booking.html">Book now</a></td></tr>';
      return;
    }
    let html = '';
    list.slice(0, 5).forEach(b => {
      const badge = statusBadge(b.status);
      html += `<tr>
        <td>${b.bookingCode || b.booking_code}</td>
        <td>${b.stationName || b.station_name || '—'}</td>
        <td>${badge}</td>
        <td>₹${b.estimatedFare || b.estimated_fare || 0}</td>
        <td>${(b.bookedAt || b.booked_at || '').toString().substring(0,10)}</td>
      </tr>`;
    });
    tbody.innerHTML = html;
    if (historyBody) {
      let h = '';
      list.forEach(b => {
        h += `<tr>
          <td>${b.bookingCode || ''}</td>
          <td>${b.porterName || '—'}</td>
          <td>${(b.pickupLocation||'')} → ${(b.dropLocation||'')}</td>
          <td>${statusBadge(b.status)}</td>
          <td>₹${b.estimatedFare || 0}</td>
          <td>${(b.bookedAt||'').toString().substring(0,10)}</td>
        </tr>`;
      });
      historyBody.innerHTML = h;
    }
    document.getElementById('statBookings').textContent = list.length;
    document.getElementById('statCompleted').textContent = list.filter(b => b.status === 'SERVICE_COMPLETED').length;
    document.getElementById('statActive').textContent = list.filter(b => !['SERVICE_COMPLETED','CANCELLED'].includes(b.status)).length;
  }

  function statusBadge(status) {
    const map = {
      'SERVICE_COMPLETED': 'badge-success',
      'CANCELLED': 'badge-danger',
      'SEARCHING': 'badge-warning',
      'PORTER_ARRIVING': 'badge-info',
      'SERVICE_STARTED': 'badge-primary'
    };
    const cls = map[status] || 'badge-info';
    return `<span class="badge ${cls}">${(status||'').replace(/_/g,' ')}</span>`;
  }

  // Try API, fallback to demo
  fetch('../api/booking/my')
    .then(r => r.json())
    .then(data => {
      if (data.success && data.bookings) renderBookings(data.bookings);
      else renderBookings(demoBookings);
    })
    .catch(() => renderBookings(demoBookings));

  // Section switching
  document.querySelectorAll('[data-section]').forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      document.querySelectorAll('.sidebar-menu a').forEach(a => a.classList.remove('active'));
      link.classList.add('active');
      document.getElementById('homeSection').classList.add('hidden');
      document.getElementById('historySection').classList.add('hidden');
      const sec = link.dataset.section;
      if (sec === 'home') document.getElementById('homeSection').classList.remove('hidden');
      if (sec === 'history') document.getElementById('historySection').classList.remove('hidden');
    });
  });
});