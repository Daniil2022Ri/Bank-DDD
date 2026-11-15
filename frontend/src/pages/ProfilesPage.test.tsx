import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { ProfilesPage } from './ProfilesPage';
import { deleteProfile, getPassport, getRegistration, listProfiles } from '../api/profile';
import type { Profile } from '../types';

vi.mock('../api/profile', () => ({
  listProfiles: vi.fn(),
  getPassport: vi.fn(),
  getRegistration: vi.fn(),
  deleteProfile: vi.fn(),
  updateProfile: vi.fn(),
  createProfile: vi.fn(),
  createRegistration: vi.fn(),
  createActualRegistration: vi.fn(),
  createPassport: vi.fn(),
}));

const listMock = vi.mocked(listProfiles);
const deleteMock = vi.mocked(deleteProfile);
const passportMock = vi.mocked(getPassport);
const registrationMock = vi.mocked(getRegistration);

const profiles: Profile[] = [
  { id: 1, phoneNumber: 79990000000, email: 'ivan@mail.ru', nameOnCard: 'IVAN IVANOV', passportId: 10 },
  { id: 2, phoneNumber: 79991112233, email: 'petr@mail.ru', nameOnCard: 'PETR PETROV', passportId: 11 },
];

function renderPage() {
  return render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/profiles']}>
        <Routes>
          <Route path="/profiles" element={<ProfilesPage />} />
          <Route path="/login" element={<div>Страница входа</div>} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
}

beforeEach(() => {
  localStorage.clear();
  vi.clearAllMocks();
  localStorage.setItem(
    'bank-ddd.session',
    JSON.stringify({ token: 'jwt', profileId: 100, role: 'ROLE_USER' }),
  );
  passportMock.mockResolvedValue({
    id: 10,
    series: 4509,
    number: 123456,
    lastName: 'Иванов',
    firstName: 'Иван',
    gender: 'MALE',
    birthDate: '1990-01-01',
    birthPlace: 'Москва',
    issuedBy: 'ОВД',
    dateOfIssue: '2010-05-20',
    divisionCode: 770001,
    registrationId: 7,
  });
  registrationMock.mockResolvedValue({
    id: 7,
    country: 'Россия',
    city: 'Москва',
    street: 'Тверская',
    houseNumber: '1',
    index: 101000,
  });
});

describe('ProfilesPage', () => {
  it('показывает список профилей и детали выбранного', async () => {
    listMock.mockResolvedValue(profiles);
    renderPage();

    expect((await screen.findAllByText('Профиль #1')).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/Профиль #2/).length).toBeGreaterThan(0);
    expect(await screen.findByText('+79990000000')).toBeInTheDocument();
    expect(await screen.findByText('4509 123456')).toBeInTheDocument();
  });

  it('удаляет профиль после подтверждения', async () => {
    const user = userEvent.setup();
    listMock.mockResolvedValue([profiles[0]]);
    deleteMock.mockResolvedValue(undefined);
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    renderPage();

    await screen.findAllByText('Профиль #1');
    await user.click(screen.getByRole('button', { name: 'Удалить' }));

    expect(deleteMock).toHaveBeenCalledWith(1);
  });

  it('не удаляет профиль без подтверждения', async () => {
    const user = userEvent.setup();
    listMock.mockResolvedValue([profiles[0]]);
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    renderPage();

    await screen.findAllByText('Профиль #1');
    await user.click(screen.getByRole('button', { name: 'Удалить' }));

    expect(deleteMock).not.toHaveBeenCalled();
  });

  it('перенаправляет на вход без сессии', () => {
    localStorage.clear();
    renderPage();

    expect(screen.getByText('Страница входа')).toBeInTheDocument();
  });
});
