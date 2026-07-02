// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// ============ AUTH FUNCTIONS ============

// Login
document.addEventListener('DOMContentLoaded', function() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;
            const errorDiv = document.getElementById('loginError');
            
            try {
                const response = await fetch(`${API_BASE_URL}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });
                
                const data = await response.json();
                
                if (response.ok) {
                    // Save user data
                    localStorage.setItem('user', JSON.stringify(data.user));
                    localStorage.setItem('isLoggedIn', 'true');
                    
                    // Redirect to dashboard
                    window.location.href = 'dashboard.html';
                } else {
                    errorDiv.textContent = data.message || 'Login failed';
                    errorDiv.style.display = 'block';
                }
            } catch (error) {
                errorDiv.textContent = 'Network error. Please try again.';
                errorDiv.style.display = 'block';
            }
        });
    }
    
    // Register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const name = document.getElementById('registerName').value;
            const email = document.getElementById('registerEmail').value;
            const age = parseInt(document.getElementById('registerAge').value);
            const password = document.getElementById('registerPassword').value;
            const confirmPassword = document.getElementById('registerConfirmPassword').value;
            const errorDiv = document.getElementById('registerError');
            const successDiv = document.getElementById('registerSuccess');
            
            // Clear previous messages
            errorDiv.style.display = 'none';
            successDiv.style.display = 'none';
            
            // Validate passwords
            if (password !== confirmPassword) {
                errorDiv.textContent = 'Passwords do not match!';
                errorDiv.style.display = 'block';
                return;
            }
            
            if (password.length < 6) {
                errorDiv.textContent = 'Password must be at least 6 characters!';
                errorDiv.style.display = 'block';
                return;
            }
            
            try {
                const response = await fetch(`${API_BASE_URL}/auth/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, age, password })
                });
                
                const data = await response.json();
                
                if (response.ok) {
                    successDiv.textContent = 'Registration successful! Redirecting to login...';
                    successDiv.style.display = 'block';
                    
                    setTimeout(() => {
                        window.location.href = 'index.html';
                    }, 2000);
                } else {
                    errorDiv.textContent = data.message || 'Registration failed';
                    errorDiv.style.display = 'block';
                }
            } catch (error) {
                errorDiv.textContent = 'Network error. Please try again.';
                errorDiv.style.display = 'block';
            }
        });
    }
    
    // ============ DASHBOARD FUNCTIONS ============
    
    // Check if user is logged in
    if (document.querySelector('.dashboard-container')) {
        const user = JSON.parse(localStorage.getItem('user'));
        const isLoggedIn = localStorage.getItem('isLoggedIn');
        
        if (!isLoggedIn || !user) {
            window.location.href = 'index.html';
        }
        
        // Display user name
        document.getElementById('userName').textContent = `Welcome, ${user.name}`;
        
        // Load dashboard data
        loadDashboardData();
        
        // Navigation
        document.querySelectorAll('.sidebar-nav a').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.dataset.page;
                showPage(page);
                
                // Update active link
                document.querySelectorAll('.sidebar-nav a').forEach(a => a.classList.remove('active'));
                link.classList.add('active');
            });
        });
        
        // Logout
        document.getElementById('logoutBtn').addEventListener('click', () => {
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
            window.location.href = 'index.html';
        });
        
        // Add student form
        const addStudentForm = document.getElementById('addStudentForm');
        if (addStudentForm) {
            addStudentForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                
                const name = document.getElementById('studentName').value;
                const email = document.getElementById('studentEmail').value;
                const age = parseInt(document.getElementById('studentAge').value);
                const errorDiv = document.getElementById('addStudentError');
                const successDiv = document.getElementById('addStudentSuccess');
                
                errorDiv.style.display = 'none';
                successDiv.style.display = 'none';
                
                try {
                    const response = await fetch(`${API_BASE_URL}/users`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ name, email, age })
                    });
                    
                    const data = await response.json();
                    
                    if (response.ok) {
                        successDiv.textContent = 'Student added successfully!';
                        successDiv.style.display = 'block';
                        
                        // Reset form
                        addStudentForm.reset();
                        
                        // Reload data
                        loadDashboardData();
                        loadAllStudents();
                    } else {
                        errorDiv.textContent = data.message || 'Failed to add student';
                        errorDiv.style.display = 'block';
                    }
                } catch (error) {
                    errorDiv.textContent = 'Network error. Please try again.';
                    errorDiv.style.display = 'block';
                }
            });
        }
        
        // Search students
        const searchInput = document.getElementById('searchStudents');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                const searchTerm = e.target.value.toLowerCase();
                filterStudents(searchTerm);
            });
        }
    }
});

// ============ DASHBOARD DATA FUNCTIONS ============

async function loadDashboardData() {
    try {
        const response = await fetch(`${API_BASE_URL}/users`);
        const students = await response.json();
        
        if (response.ok) {
            // Update stats
            document.getElementById('totalStudents').textContent = students.length;
            
            const ages = students.map(s => s.age);
            const averageAge = ages.reduce((a, b) => a + b, 0) / ages.length;
            document.getElementById('averageAge').textContent = averageAge.toFixed(1);
            document.getElementById('youngestAge').textContent = Math.min(...ages);
            document.getElementById('oldestAge').textContent = Math.max(...ages);
            
            // Show recent students (last 5)
            const recentStudents = students.slice(-5).reverse();
            const recentTable = document.getElementById('recentStudentsTable');
            if (recentTable) {
                if (recentStudents.length === 0) {
                    recentTable.innerHTML = '<tr><td colspan="4" class="text-center">No students found</td></tr>';
                } else {
                    recentTable.innerHTML = recentStudents.map(student => `
                        <tr>
                            <td>${student.id}</td>
                            <td>${student.name}</td>
                            <td>${student.email}</td>
                            <td>${student.age}</td>
                        </tr>
                    `).join('');
                }
            }
            
            // Load all students
            loadAllStudents();
        }
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

async function loadAllStudents() {
    try {
        const response = await fetch(`${API_BASE_URL}/users`);
        const students = await response.json();
        
        const table = document.getElementById('studentsTable');
        if (table) {
            if (students.length === 0) {
                table.innerHTML = '<tr><td colspan="4" class="text-center">No students found</td></tr>';
            } else {
                table.innerHTML = students.map(student => `
                    <tr>
                        <td>${student.id}</td>
                        <td>${student.name}</td>
                        <td>${student.email}</td>
                        <td>${student.age}</td>
                    </tr>
                `).join('');
            }
        }
        
        // Store all students for filtering
        window.allStudents = students;
    } catch (error) {
        console.error('Error loading students:', error);
    }
}
// Add this to your existing app.js

// Navigation helper
function navigateTo(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active-page'));
    document.getElementById(page).classList.add('active-page');
    
    document.querySelectorAll('.sidebar-nav a').forEach(a => a.classList.remove('active'));
    document.querySelector(`.sidebar-nav a[data-page="${page}"]`).classList.add('active');
    
    const titles = {
        'overview': 'Overview',
        'students': 'All Students',
        'add-student': 'Add Student',
        'profile': 'My Profile',
        'settings': 'Settings'
    };
    document.getElementById('pageTitle').textContent = titles[page] || 'Dashboard';
}

// Menu toggle for mobile
document.addEventListener('DOMContentLoaded', function() {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('open');
        });
    }
    
    // Close sidebar on outside click (mobile)
    document.addEventListener('click', function(e) {
        if (window.innerWidth <= 768) {
            if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        }
    });
});

// Export data function
function exportData() {
    const students = window.allStudents || [];
    if (students.length === 0) {
        alert('No data to export');
        return;
    }
    
    // Create CSV
    let csv = 'ID,Name,Email,Age\n';
    students.forEach(s => {
        csv += `${s.id},"${s.name}","${s.email}",${s.age}\n`;
    });
    
    // Download
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'students_data.csv';
    a.click();
}

// Edit profile
function editProfile() {
    alert('Profile editing feature coming soon!');
}

// Theme settings
function setTheme(theme) {
    document.querySelectorAll('.theme-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    if (theme === 'dark') {
        document.body.classList.add('dark-theme');
    } else {
        document.body.classList.remove('dark-theme');
    }
}

function filterStudents(searchTerm) {
    const table = document.getElementById('studentsTable');
    if (!table) return;
    
    const students = window.allStudents || [];
    const filtered = students.filter(student => 
        student.name.toLowerCase().includes(searchTerm) ||
        student.email.toLowerCase().includes(searchTerm)
    );
    
    if (filtered.length === 0) {
        table.innerHTML = '<tr><td colspan="4" class="text-center">No students found matching your search</td></tr>';
    } else {
        table.innerHTML = filtered.map(student => `
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.email}</td>
                <td>${student.age}</td>
            </tr>
        `).join('');
    }
}

function showPage(page) {
    // Hide all pages
    document.querySelectorAll('.page').forEach(p => p.style.display = 'none');
    
    // Show selected page
    const selectedPage = document.getElementById(page);
    if (selectedPage) {
        selectedPage.style.display = 'block';
    }
    
    // Update page title
    const titles = {
        'overview': 'Overview',
        'students': 'All Students',
        'add-student': 'Add Student',
        'profile': 'My Profile'
    };
    document.getElementById('pageTitle').textContent = titles[page] || 'Dashboard';
    
    // Load profile data if profile page
    if (page === 'profile') {
        loadProfile();
    }
}

function loadProfile() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user) {
        document.getElementById('profileName').textContent = user.name;
        document.getElementById('profileEmail').textContent = user.email;
        document.getElementById('profileAge').textContent = user.age;
        document.getElementById('profileJoined').textContent = user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A';
    }
}

// Check if user is on login/register page and already logged in
if (window.location.pathname.includes('index.html') || window.location.pathname.endsWith('/')) {
    if (localStorage.getItem('isLoggedIn') === 'true') {
        window.location.href = 'dashboard.html';
    }
}