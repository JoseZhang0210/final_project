import axios from 'axios'

const TOKEN_KEY = 'venueRentalJwtToken'
const USERNAME_KEY = 'venueRentalUsername'
const AUTHORITIES_KEY = 'venueRentalAuthorities'

// ------------------------------------------------------------
// JWT 登入
// ------------------------------------------------------------
export async function login(username, password) {
  const response = await axios.post('/api/auth/login', {
    username,
    password,
  })

  return response.data
}

// ------------------------------------------------------------
// 儲存 Session
// ------------------------------------------------------------
export function saveSession(username, loginResult) {
  sessionStorage.setItem(TOKEN_KEY, loginResult.token)
  sessionStorage.setItem(USERNAME_KEY, username)

  sessionStorage.setItem(
    AUTHORITIES_KEY,
    JSON.stringify(loginResult.authorities ?? []),
  )
}

// ------------------------------------------------------------
// 讀取 Session
// ------------------------------------------------------------
export function loadSession() {
  let authorities = []

  try {
    authorities = JSON.parse(
      sessionStorage.getItem(AUTHORITIES_KEY) ?? '[]',
    )
  } catch {
    authorities = []
  }

  return {
    token: sessionStorage.getItem(TOKEN_KEY) ?? '',
    username: sessionStorage.getItem(USERNAME_KEY) ?? '',
    authorities,
  }
}

// ------------------------------------------------------------
// 清除 Session
// ------------------------------------------------------------
export function clearSession() {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USERNAME_KEY)
  sessionStorage.removeItem(AUTHORITIES_KEY)
}

// ------------------------------------------------------------
// JWT Header
// ------------------------------------------------------------
function createJwtConfig(token) {
  return {
    headers: {
      Authorization: 'Bearer ' + token,
    },
  }
}

// ------------------------------------------------------------
// Venue READ
// ------------------------------------------------------------
export async function getVenues(token) {
  const response = await axios.get(
    '/api/venues',
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Venue CREATE
// ------------------------------------------------------------
export async function createVenue(token, venue) {
  const response = await axios.post(
    '/api/venues',
    venue,
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Venue UPDATE
// ------------------------------------------------------------
export async function updateVenue(token, venueId, venue) {
  const response = await axios.put(
    '/api/venues/' + venueId,
    venue,
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Venue DELETE
// ------------------------------------------------------------
export async function deleteVenue(token, venueId) {
  const response = await axios.delete(
    '/api/venues/' + venueId,
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Rental READ
// ------------------------------------------------------------
export async function getRentals(token) {
  const response = await axios.get(
    '/api/rentals',
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// 統一 API 錯誤訊息
// ------------------------------------------------------------
export function getApiErrorMessage(error) {
  const status = error?.response?.status
  const data = error?.response?.data

  if (status === 401 || status === 403) {
    return '登入驗證失敗（HTTP ' + status + '），請重新登入'
  }

  if (typeof data === 'string' && data.trim()) {
    return data
  }

  if (data?.message) {
    return data.message
  }

  return error?.message ?? 'API 發生未知錯誤'
}

// ------------------------------------------------------------
// Rental CREATE
// ------------------------------------------------------------
export async function createRental(token, rental) {
  const response = await axios.post(
    '/api/rentals',
    rental,
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Rental UPDATE
// ------------------------------------------------------------
export async function updateRental(token, rentalId, rental) {
  const response = await axios.put(
    '/api/rentals/' + rentalId,
    rental,
    createJwtConfig(token),
  )

  return response.data
}

// ------------------------------------------------------------
// Rental DELETE
// ------------------------------------------------------------
export async function deleteRental(token, rentalId) {
  const response = await axios.delete(
    '/api/rentals/' + rentalId,
    createJwtConfig(token),
  )

  return response.data
}
