import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createProfile, deleteProfile, getPassport, listProfiles, updateProfile } from './profile';
import { login, register } from './auth';

const fetchMock = vi.fn();

beforeEach(() => {
  vi.stubGlobal('fetch', fetchMock);
  fetchMock.mockReset();
  fetchMock.mockResolvedValue(
    new Response(JSON.stringify({}), { status: 200, headers: { 'Content-Type': 'application/json' } }),
  );
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('profile api', () => {
  it('listProfiles делает GET /profiles', async () => {
    await listProfiles();
    expect(fetchMock).toHaveBeenCalledWith('/api/profile/profiles', expect.objectContaining({ method: 'GET' }));
  });

  it('createProfile отправляет POST с телом', async () => {
    const profile = { phoneNumber: 79990000000, passportId: 1 };
    await createProfile(profile);
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/profile/profiles',
      expect.objectContaining({ method: 'POST', body: JSON.stringify(profile) }),
    );
  });

  it('updateProfile делает PUT по id', async () => {
    await updateProfile(5, { phoneNumber: 79990000000, passportId: 1 });
    expect(fetchMock).toHaveBeenCalledWith('/api/profile/profiles/5', expect.objectContaining({ method: 'PUT' }));
  });

  it('deleteProfile делает DELETE по id', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));
    await deleteProfile(5);
    expect(fetchMock).toHaveBeenCalledWith('/api/profile/profiles/5', expect.objectContaining({ method: 'DELETE' }));
  });

  it('getPassport делает GET /passports/{id}', async () => {
    await getPassport(3);
    expect(fetchMock).toHaveBeenCalledWith('/api/profile/passports/3', expect.objectContaining({ method: 'GET' }));
  });
});

describe('auth api', () => {
  it('login отправляет profileId и пароль', async () => {
    await login(100, 'secret123');
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/authorization/auth',
      expect.objectContaining({ method: 'POST', body: JSON.stringify({ profileId: 100, password: 'secret123' }) }),
    );
  });

  it('register создаёт пользователя', async () => {
    await register(100, 'secret123');
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/authorization/users',
      expect.objectContaining({ method: 'POST', body: JSON.stringify({ profileId: 100, password: 'secret123' }) }),
    );
  });
});
