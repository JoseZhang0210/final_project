/**
 * 密碼驗證與強度計算共用邏輯
 */

export function validatePasswordFormat(password) {
  if (!password) {
    return "請輸入密碼";
  }
  if (password.length < 6) {
    return "密碼長度至少需為 6 個字元";
  }
  return "";
}

export function validateConfirmPasswordFormat(password, confirmPassword) {
  if (!confirmPassword) {
    return "請輸入確認密碼";
  }
  if (password !== confirmPassword) {
    return "兩次輸入的密碼不一致，請重新確認";
  }
  return "";
}

/**
 * 計算密碼強度 (0~4)
 */
export function calculatePasswordStrength(password) {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 6) score += 1;
  if (password.length >= 8) score += 1;
  if (/[0-9]/.test(password) && /[a-zA-Z]/.test(password)) score += 1;
  if (/[^a-zA-Z0-9]/.test(password)) score += 1;
  return score;
}

export function getStrengthDetails(score) {
  switch (score) {
    case 1:
      return {
        width: "25%",
        text: "弱 (建議加入英文或數字)",
        className: "strength-weak",
      };
    case 2:
      return {
        width: "50%",
        text: "中等 (建議加入符號或延長)",
        className: "strength-medium",
      };
    case 3:
      return {
        width: "75%",
        text: "良好",
        className: "strength-good",
      };
    case 4:
      return {
        width: "100%",
        text: "極強",
        className: "strength-strong",
      };
    default:
      return {
        width: "0%",
        text: "",
        className: "",
      };
  }
}

