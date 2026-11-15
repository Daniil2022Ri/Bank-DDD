import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError, errorMessage, request } from './client';

const fetchMock = vi.fn();

beforeEach(() => {
  vi.stubGlobal('fetch', fetchMock);
  fetchMock.mockReset();
});

afterEach(() => {
  vi.unstubAllGlobals();
});

function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

describe('request', () => {
  it('отправляет JSON и Bearer-токен', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { ok: true }));

    await request('/profiles', { method: 'POST', body: { a: 1 }, token: 'jwt', baseUrl: '/api/profile' });

    expect(fetchMock).toHaveBeenCalledWith('/api/profile/profiles', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        Authorization: 'Bearer jwt',
      },
      body: JSON.stringify({ a: 1 }),
    });
  });

  it('возвращает разобранный ответ', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { id: 1, phoneNumber: 79990000000 }));

    await expect(request('/profiles/1')).resolves.toEqual({ id: 1, phoneNumber: 79990000000 });
  });

  it('возвращает undefined для 204', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));

    await expect(request('/profiles/1', { method: 'DELETE' })).resolves.toBeUndefined();
  });

  it('бросает ApiError с сообщением бэкенда', async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(404, { status: 404, error: 'Not Found', message: 'Profile with id 1 not found', path: '/x' }),
    );

    await expect(request('/profiles/1')).rejects.toMatchObject({
      name: 'ApiError',
      status: 404,
      message: 'Profile with id 1 not found',
    });
  });

  it('подставляет сообщение по умолчанию, если тело не JSON', async () => {
    fetchMock.mockImplementation(async () => new Response('oops', { status: 500 }));

    await expect(request('/x')).rejects.toMatchObject({ status: 500, message: 'Ошибка 500' });
  });
});

describe('errorMessage', () => {
  it('достаёт сообщение из ApiError', () => {
    expect(errorMessage(new ApiError(409, 'Конфликт'))).toBe('Конфликт');
  });

  it('возвращает текст обычной ошибки', () => {
    expect(errorMessage(new Error('boom'))).toBe('boom');
  });

  it('возвращает заглушку для неизвестной ошибки', () => {
    expect(errorMessage('strange')).toBe('Неизвестная ошибка');
  });
});
