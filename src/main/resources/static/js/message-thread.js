// message-thread.js - Handle message sending and thread display

document.addEventListener('DOMContentLoaded', function() {
    const sendBtn = document.getElementById('sendBtn');
    const messageText = document.getElementById('messageText');

    sendBtn.addEventListener('click', sendMessage);
    messageText.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') {
            sendMessage();
        }
    });

    // Auto-scroll to bottom
    scrollToBottom();
});

function sendMessage() {
    const messageText = document.getElementById('messageText').value.trim();
    const errorMsg = document.getElementById('errorMsg');

    // Clear previous errors
    errorMsg.textContent = '';
    errorMsg.classList.remove('show');

    if (!messageText) {
        errorMsg.textContent = 'Message cannot be empty';
        errorMsg.classList.add('show');
        return;
    }

    if (messageText.length > 1000) {
        errorMsg.textContent = 'Message is too long (max 1000 characters)';
        errorMsg.classList.add('show');
        return;
    }

    const sendBtn = document.getElementById('sendBtn');
    sendBtn.disabled = true;
    sendBtn.textContent = 'Sending...';

    fetch('/messages/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            recipientId: otherUserId,
            itemId: itemId,
            messageText: messageText
        })
    })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                // Clear input
                document.getElementById('messageText').value = '';

                // Add message to display
                addMessageToDisplay(data.message);

                // Scroll to bottom
                scrollToBottom();
            } else {
                errorMsg.textContent = data.error || 'Failed to send message';
                errorMsg.classList.add('show');
            }
        })
        .catch(e => {
            errorMsg.textContent = 'Network error: ' + e.message;
            errorMsg.classList.add('show');
        })
        .finally(() => {
            sendBtn.disabled = false;
            sendBtn.textContent = 'Send';
        });
}

function addMessageToDisplay(message) {
    const display = document.getElementById('messagesDisplay');

    // Remove empty message placeholder if it exists
    const emptyMsg = display.querySelector('.empty-message');
    if (emptyMsg) emptyMsg.remove();

    const msgDiv = document.createElement('div');
    msgDiv.className = 'message-item sent';
    msgDiv.innerHTML = `
        <div class="message-content">
            <p class="text">${escapeHtml(message.messageText)}</p>
            <span class="timestamp">${formatTime(message.createdAt)}</span>
        </div>
    `;

    display.appendChild(msgDiv);
}

function scrollToBottom() {
    const display = document.getElementById('messagesDisplay');
    display.scrollTop = display.scrollHeight;
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}
