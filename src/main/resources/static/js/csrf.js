(() => {
    const safeMethods = new Set(['GET', 'HEAD', 'OPTIONS', 'TRACE']);
    const originalFetch = window.fetch.bind(window);

    function token() {
        const cookie = document.cookie.split('; ').find(value => value.startsWith('XSRF-TOKEN='));
        return cookie ? decodeURIComponent(cookie.substring('XSRF-TOKEN='.length)) : null;
    }

    window.fetch = (input, init = {}) => {
        const method = (init.method || (input instanceof Request ? input.method : 'GET')).toUpperCase();
        const url = new URL(input instanceof Request ? input.url : input, window.location.origin);

        if (url.origin === window.location.origin && !safeMethods.has(method)) {
            const csrfToken = token();
            if (csrfToken) {
                const headers = new Headers(init.headers || (input instanceof Request ? input.headers : undefined));
                headers.set('X-XSRF-TOKEN', csrfToken);
                init = { ...init, headers, credentials: init.credentials || 'same-origin' };
            }
        }

        return originalFetch(input, init);
    };
})();
