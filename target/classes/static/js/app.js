// EventSphere JavaScript Utilities

document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});

// Toast notification function
function showToast(message, type = 'info') {
    const toastContainer = document.querySelector('.toast-container') || document.body;
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// Confirm delete action
function confirmDelete(message = 'Are you sure?') {
    return confirm(message);
}

// Format date to readable format
function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// AJAX Request Helper
async function makeRequest(url, method = 'GET', data = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        showToast('An error occurred. Please try again.', 'error');
        throw error;
    }
}

// Debounce function
function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

// Search with debounce
const searchInput = document.querySelector('[data-search]');
if (searchInput) {
    const performSearch = debounce(function(event) {
        const keyword = event.target.value;
        // Perform search
        console.log('Searching for:', keyword);
    }, 300);

    searchInput.addEventListener('input', performSearch);
}

// Chart.js default configuration
Chart.defaults.font.family = "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif";
Chart.defaults.color = '#666';
Chart.defaults.plugins.legend.labels.padding = 15;

// QR Code Scanner (placeholder)
function initQRCodeScanner() {
    // This would require a QR code scanner library like jsQR
    console.log('QR Code Scanner initialized');
}

// Export table to CSV
function exportTableToCSV(filename = 'export.csv') {
    const table = document.querySelector('table');
    if (!table) return;

    let csv = [];
    const rows = table.querySelectorAll('tr');

    rows.forEach(row => {
        const cells = row.querySelectorAll('td, th');
        const rowData = Array.from(cells).map(cell => {
            return '"' + cell.textContent.replace(/"/g, '""') + '"';
        }).join(',');
        csv.push(rowData);
    });

    const csvContent = csv.join('\n');
    const link = document.createElement('a');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);

    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
}

// Print functionality
function printPage() {
    window.print();
}

// Initialize charts
function initializeCharts() {
    // Get all chart canvases
    const chartCanvases = document.querySelectorAll('[data-chart]');
    chartCanvases.forEach(canvas => {
        const chartType = canvas.dataset.chart;
        const chartData = JSON.parse(canvas.dataset.chartData || '{}');
        
        new Chart(canvas, {
            type: chartType,
            data: chartData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top'
                    }
                }
            }
        });
    });
}

// Load charts when page loads
document.addEventListener('DOMContentLoaded', initializeCharts);
