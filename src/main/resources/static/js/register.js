const form = document.getElementById('registrationForm');
const alertContainer = document.getElementById('alertContainer');
const closeBtn = document.getElementById('closeBtn');

const fields = {
    fullName: document.getElementById('fullName'),
    username: document.getElementById('username'),
    email: document.getElementById('email'),
    password: document.getElementById('password'),
    confirmPassword: document.getElementById('confirmPassword')
};

const errorMessages = {
    fullName: document.getElementById('fullNameError'),
    username: document.getElementById('usernameError'),
    email: document.getElementById('emailError'),
    password: document.getElementById('passwordError'),
    confirmPassword: document.getElementById('confirmPasswordError')
};

// Close button - go back home
closeBtn.addEventListener('click', () => {
    window.location.href = '/';
});

// Password strength indicator
fields.password.addEventListener('input', (e) => {
    const password = e.target.value;
    const strengthBar = document.getElementById('passwordStrengthBar');

    let strength = 'weak';

    if (password.length >= 12 && /[a-z]/.test(password) && /[A-Z]/.test(password) && /[0-9]/.test(password)) {
        strength = 'strong';
    } else if (password.length >= 8) {
        strength = 'fair';
    }

    strengthBar.className = `password-strength__bar ${strength}`;
    validatePasswordMatch();
});

// Real-time validation on blur
fields.fullName.addEventListener('blur', validateFullName);
fields.username.addEventListener('blur', validateUsername);
fields.email.addEventListener('blur', validateEmail);
fields.password.addEventListener('blur', validatePassword);
fields.confirmPassword.addEventListener('blur', validatePasswordMatch);

function validateFullName() {
    const value = fields.fullName.value.trim();
    const error = errorMessages.fullName;

    if (!value) {
        showError(fields.fullName, error, 'Full name is required');
        return false;
    }

    if (value.length < 2) {
        showError(fields.fullName, error, 'Full name must be at least 2 characters');
        return false;
    }

    clearError(fields.fullName, error);
    return true;
}

function validateUsername() {
    const value = fields.username.value.trim();
    const error = errorMessages.username;

    if (!value) {
        showError(fields.username, error, 'Username is required');
        return false;
    }

    if (value.length < 3) {
        showError(fields.username, error, 'Username must be at least 3 characters');
        return false;
    }

    if (!/^[a-zA-Z0-9_-]+$/.test(value)) {
        showError(fields.username, error, 'Username can only contain letters, numbers, dashes, and underscores');
        return false;
    }

    clearError(fields.username, error);
    return true;
}

function validateEmail() {
    const value = fields.email.value.trim();
    const error = errorMessages.email;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!value) {
        showError(fields.email, error, 'Email address is required');
        return false;
    }

    if (!emailRegex.test(value)) {
        showError(fields.email, error, 'Please enter a valid email address');
        return false;
    }

    clearError(fields.email, error);
    return true;
}

function validatePassword() {
    const value = fields.password.value;
    const error = errorMessages.password;

    if (!value) {
        showError(fields.password, error, 'Password is required');
        return false;
    }

    if (value.length < 8) {
        showError(fields.password, error, 'Password must be at least 8 characters');
        return false;
    }

    clearError(fields.password, error);
    return true;
}

function validatePasswordMatch() {
    const password = fields.password.value;
    const confirmPassword = fields.confirmPassword.value;
    const error = errorMessages.confirmPassword;

    if (!confirmPassword) {
        clearError(fields.confirmPassword, error);
        return true;
    }

    if (password !== confirmPassword) {
        showError(fields.confirmPassword, error, 'Passwords do not match');
        return false;
    }

    clearError(fields.confirmPassword, error);
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
    const isFullNameValid = validateFullName();
    const isUsernameValid = validateUsername();
    const isEmailValid = validateEmail();
    const isPasswordValid = validatePassword();
    const isPasswordMatchValid = validatePasswordMatch();

    if (!isFullNameValid || !isUsernameValid || !isEmailValid || !isPasswordValid || !isPasswordMatchValid) {
        e.preventDefault();
        showAlert('Please fix the errors above before submitting');
        return;
    }
    // If all valid, let form submit normally to /doregister
});
