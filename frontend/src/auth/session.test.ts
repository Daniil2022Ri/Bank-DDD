import { beforeEach, describe, expect, it } from 'vitest';
import { clearSession, getToken, loadSession, saveSession } from './session';

const session = { token: 'jwt-token', profileId: 100, role: 'ROLE_USER' as const };

beforeEach(() => {
  localStorage.clear();
});

describe('session', () => {
  it('сохраняет и читает сессию', () => {
    saveSession(session);

    expect(loadSession()).toEqual(session);
    expect(getToken()).toBe('jwt-token');
  });

  it('возвращает null без сессии', () => {
    expect(loadSession()).toBeNull();
    expect(getToken()).toBeNull();
  });

  it('удаляет сессию', () => {
    saveSession(session);
    clearSession();

    expect(loadSession()).toBeNull();
  });

  it('игнорирует битые данные в localStorage', () => {
    localStorage.setItem('bank-ddd.session', '{not-json');

    expect(loadSession()).toBeNull();
  });
});
