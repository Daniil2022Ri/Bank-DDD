import { request } from './client';
import type { Passport, Profile, Registration } from '../types';

export const PROFILE_BASE = import.meta.env.VITE_PROFILE_URL ?? '/api/profile';

export function listProfiles(): Promise<Profile[]> {
  return request<Profile[]>('/profiles', { baseUrl: PROFILE_BASE });
}

export function getProfile(profileId: number): Promise<Profile> {
  return request<Profile>(`/profiles/${profileId}`, { baseUrl: PROFILE_BASE });
}

export function createProfile(profile: Profile): Promise<Profile> {
  return request<Profile>('/profiles', {
    method: 'POST',
    body: profile,
    baseUrl: PROFILE_BASE,
  });
}

export function updateProfile(profileId: number, profile: Profile): Promise<Profile> {
  return request<Profile>(`/profiles/${profileId}`, {
    method: 'PUT',
    body: profile,
    baseUrl: PROFILE_BASE,
  });
}

export function deleteProfile(profileId: number): Promise<void> {
  return request<void>(`/profiles/${profileId}`, {
    method: 'DELETE',
    baseUrl: PROFILE_BASE,
  });
}

export function createRegistration(registration: Registration): Promise<Registration> {
  return request<Registration>('/registrations', {
    method: 'POST',
    body: registration,
    baseUrl: PROFILE_BASE,
  });
}

export function getRegistration(registrationId: number): Promise<Registration> {
  return request<Registration>(`/registrations/${registrationId}`, { baseUrl: PROFILE_BASE });
}

export function createActualRegistration(registration: Registration): Promise<Registration> {
  return request<Registration>('/actual-registrations', {
    method: 'POST',
    body: registration,
    baseUrl: PROFILE_BASE,
  });
}

export function createPassport(passport: Passport): Promise<Passport> {
  return request<Passport>('/passports', {
    method: 'POST',
    body: passport,
    baseUrl: PROFILE_BASE,
  });
}

export function getPassport(passportId: number): Promise<Passport> {
  return request<Passport>(`/passports/${passportId}`, { baseUrl: PROFILE_BASE });
}
