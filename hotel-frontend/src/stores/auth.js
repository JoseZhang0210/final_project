import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

function parseJwtPayload(token) {
    if (!token) return null;
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        const base64Url = parts[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.warn('解析 JWT 失敗:', e);
        return null;
    }
}

export const useAuthStore = defineStore('auth', () => {
    const isLoggedIn = ref(!!localStorage.getItem('token'))
    const authorities = ref(
        localStorage.getItem('authorities')
            ? JSON.parse(localStorage.getItem('authorities'))
            : []
    )
    const name = ref(localStorage.getItem('name') || '')

    let refreshTimer = null
    let refreshPromise = null

    // 判斷是否具備員工身分
    const isEmployee = computed(() => {
        if (!isLoggedIn.value || !Array.isArray(authorities.value)) return false;
        return authorities.value.some(a => 
            a === 'ROLE_EMPLOYEE' || 
            a === 'ROLE_ADMIN' || 
            (typeof a === 'string' && a.startsWith('POSITION_'))
        );
    });

    // 判斷是否為超級管理員（總經理 / 特權職位）
    const isSuperAdmin = computed(() => {
        if (!isLoggedIn.value || !Array.isArray(authorities.value)) return false;
        return authorities.value.some(a => 
            a === 'ROLE_ADMIN' || 
            a === 'SUPER_ADMIN' || 
            a === 'POSITION_總經理' ||
            a === 'POSITION_管理員'
        );
    });

    // 提取使用者職位名稱
    const userPosition = computed(() => {
        if (!Array.isArray(authorities.value)) return '';
        const posAuth = authorities.value.find(a => typeof a === 'string' && a.startsWith('POSITION_'));
        return posAuth ? posAuth.replace('POSITION_', '') : '';
    });

    // 檢查是否具有指定權限代碼（超管自動通過）
    function hasPermission(permissionCode) {
        if (!isLoggedIn.value) return false;
        if (isSuperAdmin.value) return true;
        if (!permissionCode) return true;
        return Array.isArray(authorities.value) && authorities.value.includes(permissionCode);
    }

    // 檢查是否具有任一權限代碼
    function hasAnyPermission(permissionCodes) {
        if (!isLoggedIn.value) return false;
        if (isSuperAdmin.value) return true;
        if (!Array.isArray(permissionCodes) || permissionCodes.length === 0) return true;
        if (!Array.isArray(authorities.value)) return false;
        return permissionCodes.some(code => authorities.value.includes(code));
    }

    // 自動續期定時器
    function startAutoRefreshTimer() {
        stopAutoRefreshTimer();
        // 每 15 分鐘定期檢查一次 Token 有效期
        refreshTimer = setInterval(() => {
            checkAndRefreshToken();
        }, 15 * 60 * 1000);
    }

    function stopAutoRefreshTimer() {
        if (refreshTimer) {
            clearInterval(refreshTimer);
            refreshTimer = null;
        }
    }

    // 登入成功時呼叫
    function login(token, userAuthorities, userName) {
        // 確保傳進來的 userAuthorities 是一組陣列（防呆）
        const authArray = Array.isArray(userAuthorities) ? userAuthorities : [userAuthorities];

        // 儲存 JWT 字串
        localStorage.setItem('token', token)
        
        // 儲存權限：必須用 JSON.stringify() 把陣列轉成標準 JSON 字串
        localStorage.setItem('authorities', JSON.stringify(authArray))

        // 儲存使用者姓名
        const displayName = userName || ''
        localStorage.setItem('name', displayName)
        
        // 更新 Pinia 狀態
        isLoggedIn.value = true
        authorities.value = authArray // 直接賦值陣列，千萬不要用 JSON.parse()
        name.value = displayName
        
        startAutoRefreshTimer();
        console.log("Pinia 權限與使用者資訊更新成功：", { authorities: authorities.value, name: name.value });
    }

    // 登出時呼叫
    function logout() {
        stopAutoRefreshTimer();
        // 清除 JWT
        localStorage.removeItem("token");
        // 清除角色 / 權限
        localStorage.removeItem("authorities");
        // 清除姓名
        localStorage.removeItem("name");

        isLoggedIn.value = false
        authorities.value = []
        name.value = ''
        console.log("已登出，JWT 已清除");
    }

    // 更新使用者姓名（修改個人資料後即時更新）
    function updateName(newName) {
        const displayName = newName || ''
        localStorage.setItem('name', displayName)
        name.value = displayName
    }

    // 主動向後端刷新 Token
    async function refreshToken() {
        const token = localStorage.getItem('token');
        if (!token) return false;

        if (refreshPromise) {
            return refreshPromise;
        }

        refreshPromise = (async () => {
            try {
                const res = await fetch('/api/auth/refresh', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Content-Type': 'application/json'
                    }
                });

                if (res.ok) {
                    const data = await res.json();
                    if (data.token) {
                        login(data.token, data.authorities || authorities.value, data.name || name.value);
                        console.log("Token 自動續期成功");
                        return true;
                    }
                } else if (res.status === 401 || res.status === 403) {
                    console.warn("Token 續期失效或已過期，自動登出");
                    logout();
                }
                return false;
            } catch (err) {
                console.error("Token 刷新失敗:", err);
                return false;
            } finally {
                refreshPromise = null;
            }
        })();

        return refreshPromise;
    }

    // 檢查並在即將到期時自動續期 (預設剩餘時間小於 2 小時即續期)
    async function checkAndRefreshToken(thresholdSeconds = 7200) {
        const token = localStorage.getItem('token');
        if (!token) return;

        const payload = parseJwtPayload(token);
        if (!payload || !payload.exp) return;

        const nowSec = Math.floor(Date.now() / 1000);
        const remainingSec = payload.exp - nowSec;

        if (remainingSec <= 0) {
            console.warn("JWT 已過期");
            logout();
        } else if (remainingSec <= thresholdSeconds) {
            console.log(`JWT 即將到期（剩餘約 ${Math.round(remainingSec / 60)} 分鐘），正在自動續期...`);
            await refreshToken();
        }
    }

    // 若啟動時已處於登入狀態，立即開啟定時器與有效性檢查
    if (isLoggedIn.value) {
        startAutoRefreshTimer();
        checkAndRefreshToken();
    }

    return { 
        isLoggedIn, 
        authorities, 
        name, 
        isEmployee,
        isSuperAdmin,
        userPosition,
        hasPermission,
        hasAnyPermission,
        login, 
        logout, 
        updateName, 
        refreshToken, 
        checkAndRefreshToken,
        startAutoRefreshTimer,
        stopAutoRefreshTimer
    }
})

