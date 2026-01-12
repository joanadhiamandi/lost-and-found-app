const form = document.getElementById('loginForm');
const alertContainer = document.getElementById('alertContainer');
const closeBtn = document.getElementById('closeBtn');

const fields = {
    username: document.getElementById('username'),
    password: document.getElementById('password')
};

const errorMessages = {
    username: document.getElementById('usernameError'),
    password: document.getElementById('passwordError')
};

// Close button - go back home
closeBtn.addEventListener('click', () => {
    window.location.href = '/';
});

// Real-time validation on blur
fields.username.addEventListener('blur', validateUsername);
fields.password.addEventListener('blur', validatePassword);

function validateUsername() {
    const value = fields.username.value.trim();
    const error = errorMessages.username;

    if (!value) {
        showError(fields.username, error, 'Username or email is required');
        return false;
    }

    clearError(fields.username, error);
    return true;
}

function validatePassword() {
    const value = fields.password.value;
    const error = errorMessages.password;

    if (!value) {
        showError(fields.password, error, 'Password is required');
        return false;
    }

    clearError(fields.password, error);
    return true;
}

function showError(field, errorElement, message) {
    field.classList.add('error');
    errorElement.textContent = message;
    errorElement.classList.add('show');
}

function clearError(field, errorElement) {
    field.classList.remove('error');
    errorElement.classList.remove('show');
    errorElement.textContent = '';
}

function showAlert(message, type = 'error') {
    alertContainer.textContent = message;
    alertContainer.className = `alert alert-${type} show`;
    alertContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// Form submission
form.addEventListener('submit', function(e) {
    const isUsernameValid = validateUsername();
    const isPasswordValid = validatePassword();

    if (!isUsernameValid || !isPasswordValid) {
        e.preventDefault();
        showAlert('Please enter your username/email and password');
        return;
    }
    // If all valid, let form submit normally to /dologin
});
