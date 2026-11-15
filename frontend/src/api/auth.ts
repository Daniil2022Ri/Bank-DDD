import { request } from './client';
import type { JwtResponse, UserDto } from '../types';

export const AUTH_BASE = import.meta.env.VITE_AUTH_URL ?? '/api/authorization';

export function login(profileId: number, password: string): Promise<JwtResponse> {
  return request<JwtResponse>('/auth', {
    method: 'POST',
    body: { profileId, password },
    baseUrl: AUTH_BASE,
  });
}

export function register(profileId: number, password: string): Promise<UserDto> {
  return request<UserDto>('/users', {
    method: 'POST',
    body: { profileId, password },
    baseUrl: AUTH_BASE,
  });
}
