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
        } else if (data.type === 'image') {
            updateImageComponent(data.id, data.base64);
        } else if (data.type === 'chart') {
            updateChartComponent(data.id, data.svg);
        } else if (data.type === 'button-text') {
            updateButtonTextComponent(data.id, data.text);
        } else if (data.type === 'field-value') {
            updateFieldValueComponent(data.id, data.value);
        } else if (data.type === 'checkbox-state') {
            updateCheckboxComponent(data.id, data.checked);
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

    function updateImageComponent(id, base64Image) {
        const element = document.getElementById(id);
        if (element) {
            // Find the img tag inside the component
            const imgElement = element.querySelector('img');
            if (imgElement) {
                imgElement.src = 'data:image/png;base64,' + base64Image;
                console.log('Updated image', id);
            } else {
                console.warn('Image element not found in:', id);
            }
        } else {
            console.warn('Component element not found:', id);
        }
    }

    function updateChartComponent(id, svgHtml) {
        const element = document.getElementById(id);
        if (element) {
            element.innerHTML = svgHtml;
            console.log('Updated chart', id);
        } else {
            console.warn('Element not found:', id);
        }
    }

    function updateButtonTextComponent(id, text) {
        const element = document.getElementById(id);
        if (element) {
            const buttonElement = element.querySelector('button');
            if (buttonElement) {
                buttonElement.textContent = text;
                console.log('Updated button text', id);
            }
        } else {
            console.warn('Button element not found:', id);
        }
    }

    function updateFieldValueComponent(id, value) {
        const element = document.getElementById(id);
        if (element) {
            const inputElement = element.querySelector('input');
            if (inputElement) {
                inputElement.value = value;
                inputElement.setAttribute('data-value', value);
                console.log('Updated field value', id);
            }
        } else {
            console.warn('Field element not found:', id);
        }
    }

    function updateCheckboxComponent(id, checked) {
        const element = document.getElementById(id);
        if (element) {
            const checkboxElement = element.querySelector('input[type="checkbox"]');
            if (checkboxElement) {
                checkboxElement.checked = checked;
                checkboxElement.setAttribute('data-checked', checked ? 'true' : 'false');
                console.log('Updated checkbox state', id, checked);
            }
        } else {
            console.warn('Checkbox element not found:', id);
        }
    }

    // Send event to backend
    function sendEvent(componentId, eventType, data) {
        fetch('/event', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                componentId: componentId,
                type: eventType,
                data: data
            })
        })
        .then(response => response.json())
        .then(result => {
            if (result.status !== 'ok') {
                console.error('Event error:', result);
            }
        })
        .catch(error => {
            console.error('Error sending event:', error);
        });
    }

    // Setup event listeners for interactive components
    function setupComponentListeners() {
        // Text fields
        document.querySelectorAll('input[type="text"], input[type="number"], input[type="email"]').forEach(function(input) {
            const componentDiv = input.closest('div[id]');
            if (componentDiv) {
                const componentId = componentDiv.id;
                
                // Send on enter key
                input.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        sendEvent(componentId, 'TEXT_INPUT', { value: input.value });
                    }
                });
                
                // Send on blur (focus lost)
                input.addEventListener('blur', function() {
                    sendEvent(componentId, 'TEXT_CHANGE', { value: input.value });
                });
            }
        });

        // Checkboxes
        document.querySelectorAll('input[type="checkbox"]').forEach(function(checkbox) {
            const componentDiv = checkbox.closest('div[id]');
            if (componentDiv) {
                const componentId = componentDiv.id;
                checkbox.addEventListener('change', function() {
                    sendEvent(componentId, 'CHECKBOX_CHANGE', { checked: checkbox.checked });
                });
            }
        });

        // Buttons
        document.querySelectorAll('button').forEach(function(button) {
            const componentDiv = button.closest('div[id]');
            if (componentDiv) {
                const componentId = componentDiv.id;
                // Only add if no onclick already defined
                if (!button.hasAttribute('onclick') || button.getAttribute('onclick') === '') {
                    button.addEventListener('click', function() {
                        sendEvent(componentId, 'BUTTON_CLICK', { text: button.textContent });
                    });
                }
            }
        });
    }

    // Connect on page load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            connectSSE();
            setupComponentListeners();
        });
    } else {
        connectSSE();
        setupComponentListeners();
    }

    // Cleanup on page unload
    window.addEventListener('beforeunload', function() {
        if (eventSource) {
            eventSource.close();
        }
    });

    // Expose sendEvent globally for custom usage
    window.JProfiler = {
        sendEvent: sendEvent
    };
})();
