import type { AuthSession } from '../types';

export const SESSION_STORAGE_KEY = 'bank-ddd.session';

export function loadSession(): AuthSession | null {
  try {
    const raw = localStorage.getItem(SESSION_STORAGE_KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw) as Partial<AuthSession>;
    if (typeof parsed.token === 'string' && typeof parsed.profileId === 'number') {
      return { token: parsed.token, profileId: parsed.profileId, role: parsed.role ?? 'ROLE_USER' };
    }
    return null;
  } catch {
    return null;
  }
}

export function saveSession(session: AuthSession): void {
  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
}

export function clearSession(): void {
  localStorage.removeItem(SESSION_STORAGE_KEY);
}

export function getToken(): string | null {
  return loadSession()?.token ?? null;
}
