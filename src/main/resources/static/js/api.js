/**
 * 統一的 API 呼叫模組
 * 所有 API 呼叫都透過此模組進行，確保一致的請求格式和錯誤處理
 * 完全相容於未來的 Vue.js 遷移
 */

const API_BASE = '/api';

function csrfToken() {
    const cookie = document.cookie.split('; ').find(value => value.startsWith('XSRF-TOKEN='));
    return cookie ? decodeURIComponent(cookie.substring('XSRF-TOKEN='.length)) : null;
}

/**
 * 通用的 API 呼叫函數
 * @param {string} method HTTP 方法 (GET, POST, PUT, DELETE)
 * @param {string} endpoint API 端點路徑
 * @param {object} data 請求體資料 (僅用於 POST/PUT)
 * @returns {Promise} 返回 API 回應的 Promise
 */
export async function apiCall(method, endpoint, data = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin'
    };

    if (!['GET', 'HEAD', 'OPTIONS', 'TRACE'].includes(method.toUpperCase())) {
        const token = csrfToken();
        if (token) {
            options.headers['X-XSRF-TOKEN'] = token;
        }
    }

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const result = await response.json();

        // 返回完整的 API 回應，讓呼叫方決定如何處理
        return result;
    } catch (error) {
        console.error(`API 呼叫錯誤 [${method} ${endpoint}]:`, error);
        return {
            status: 'error',
            code: 500,
            data: null,
            message: '網路錯誤或伺服器無法訪問'
        };
    }
}

/**
 * 檢查 API 回應是否成功
 * @param {object} response API 回應物件
 * @returns {boolean} 是否成功
 */
export function isSuccess(response) {
    return response.status === 'success' && response.code === 200;
}

/**
 * 從 API 回應中提取資料
 * @param {object} response API 回應物件
 * @returns {*} 回應中的資料，如果失敗則返回 null
 */
export function getData(response) {
    return isSuccess(response) ? response.data : null;
}

// ============ 產品 API ============
export async function getProducts() {
    return apiCall('GET', '/products');
}

export async function getProductById(id) {
    return apiCall('GET', `/products/${id}`);
}

export async function createProduct(product) {
    return apiCall('POST', '/products', product);
}

export async function updateProduct(id, product) {
    return apiCall('PUT', `/products/${id}`, product);
}

export async function deleteProduct(id) {
    return apiCall('DELETE', `/products/${id}`);
}

export async function getProductCategories() {
    return apiCall('GET', '/products/categories/all');
}

// ============ 場地 API ============
export async function getVenues() {
    return apiCall('GET', '/venues');
}

export async function getVenueById(id) {
    return apiCall('GET', `/venues/${id}`);
}

export async function createVenue(venue) {
    return apiCall('POST', '/venues', venue);
}

export async function updateVenue(id, venue) {
    return apiCall('PUT', `/venues/${id}`, venue);
}

export async function deleteVenue(id) {
    return apiCall('DELETE', `/venues/${id}`);
}

// ============ 租賃 API ============
export async function getRentals() {
    return apiCall('GET', '/rentals');
}

export async function getRentalById(id) {
    return apiCall('GET', `/rentals/${id}`);
}

export async function createRental(rental) {
    return apiCall('POST', '/rentals', rental);
}

export async function updateRental(id, rental) {
    return apiCall('PUT', `/rentals/${id}`, rental);
}

export async function deleteRental(id) {
    return apiCall('DELETE', `/rentals/${id}`);
}

// ============ 房間類型 API ============
export async function getRoomTypes() {
    return apiCall('GET', '/room-types');
}

export async function getRoomTypeById(id) {
    return apiCall('GET', `/room-types/${id}`);
}

export async function createRoomType(roomType) {
    return apiCall('POST', '/room-types', roomType);
}

export async function updateRoomType(id, roomType) {
    return apiCall('PUT', `/room-types/${id}`, roomType);
}

export async function deleteRoomType(id) {
    return apiCall('DELETE', `/room-types/${id}`);
}

// ============ 預訂訂單 API ============
export async function getBookingOrders() {
    return apiCall('GET', '/booking-orders');
}

export async function getBookingOrderById(id) {
    return apiCall('GET', `/booking-orders/${id}`);
}

export async function createBookingOrder(order) {
    return apiCall('POST', '/booking-orders', order);
}

export async function updateBookingOrder(id, order) {
    return apiCall('PUT', `/booking-orders/${id}`, order);
}

export async function deleteBookingOrder(id) {
    return apiCall('DELETE', `/booking-orders/${id}`);
}

// ============ 首頁 API ============
export async function getHomeDashboard() {
    return apiCall('GET', '/home/dashboard');
}

export async function getRegisterInfo() {
    return apiCall('GET', '/home/register-info');
}

export async function getBookingConfig() {
    return apiCall('GET', '/home/config');
}

// ============ 餐廳 API ============
export async function getRestaurantList() {
    return apiCall('GET', '/restaurants/list');
}

export async function getRestaurantTimes() {
    return apiCall('GET', '/restaurants/times');
}

export async function getReservations() {
    return apiCall('GET', '/restaurants/reservations');
}
