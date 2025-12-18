// JProfiler Dynamic Updates
(function() {
    let eventSource = null;
    let reconnectAttempts = 0;
    const maxReconnectAttempts = 5;
    const reconnectDelay = 2000;
    
    // Store placeholder values for each component
    const componentPlaceholders = {};

    function connectSSE() {
        if (eventSource) {
            eventSource.close();
        }

        eventSource = new EventSource('/updates');

        eventSource.onopen = function() {
            console.log('SSE connection established');
            reconnectAttempts = 0;
        };

        eventSource.onmessage = function(event) {
            try {
                const data = JSON.parse(event.data);
                handleUpdate(data);
            } catch (e) {
                console.error('Error parsing SSE data:', e);
            }
        };

        eventSource.addEventListener('update', function(event) {
            try {
                const data = JSON.parse(event.data);
                handleUpdate(data);
            } catch (e) {
                console.error('Error parsing update event:', e);
            }
        });

        eventSource.onerror = function(error) {
            console.error('SSE error:', error);
            eventSource.close();

            if (reconnectAttempts < maxReconnectAttempts) {
                reconnectAttempts++;
                console.log('Reconnecting in ' + reconnectDelay + 'ms... (attempt ' + reconnectAttempts + ')');
                setTimeout(connectSSE, reconnectDelay);
            } else {
                console.error('Max reconnect attempts reached');
            }
        };
    }

    function handleUpdate(data) {
        if (data.type === 'connected') {
            console.log('Connected to JProfiler updates');
            return;
        }
        
        if (data.type === 'text') {
            updateTextComponent(data.id, data.value);
        } else if (data.type === 'placeholder') {
            updatePlaceholder(data.id, data.placeholder, data.value);
        } else if (data.type === 'html') {
            updateHTML(data.id, data.html);
        }
    }

    function updateTextComponent(id, text) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = text;
        } else {
            console.warn('Element not found:', id);
        }
    }

    function updatePlaceholder(id, placeholder, value) {
        const element = document.getElementById(id);
        if (element) {
            // Initialize component placeholders if not exists
            if (!componentPlaceholders[id]) {
                componentPlaceholders[id] = {};
            }
            
            // Store the new placeholder value
            componentPlaceholders[id][placeholder] = value;
            
            // Get the template from data attribute
            const template = element.getAttribute('data-template');
            if (template) {
                // Replace all placeholders in the template
                let renderedText = template;
                for (const [key, val] of Object.entries(componentPlaceholders[id])) {
                    const regex = new RegExp('\\{' + key + '\\}', 'g');
                    renderedText = renderedText.replace(regex, val);
                }
                element.textContent = renderedText;
                console.log('Updated', id, 'with', placeholder, '=', value);
            } else {
                console.warn('No template found for element:', id);
            }
        } else {
            console.warn('Element not found:', id);
        }
    }

    function updateHTML(id, html) {
        const element = document.getElementById(id);
        if (element) {
            element.innerHTML = html;
        } else {
            console.warn('Element not found:', id);
        }
    }

    // Connect on page load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', connectSSE);
    } else {
        connectSSE();
    }

    // Cleanup on page unload
    window.addEventListener('beforeunload', function() {
        if (eventSource) {
            eventSource.close();
        }
    });
})();
