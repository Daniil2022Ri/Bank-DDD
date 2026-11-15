import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { LoginPage } from './LoginPage';
import { login, register } from '../api/auth';
import { ApiError } from '../api/client';

vi.mock('../api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
}));

const loginMock = vi.mocked(login);
const registerMock = vi.mocked(register);

function renderLogin() {
  return render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/login']}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/profiles" element={<div>Профили клиента</div>} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
}

beforeEach(() => {
  localStorage.clear();
  vi.clearAllMocks();
});

describe('LoginPage', () => {
  it('показывает ошибки валидации при пустой форме', async () => {
    const user = userEvent.setup();
    renderLogin();

    await user.click(screen.getByRole('button', { name: 'Войти' }));

    expect(await screen.findByText('ID профиля обязательно')).toBeInTheDocument();
    expect(screen.getByText('Пароль обязательно')).toBeInTheDocument();
    expect(loginMock).not.toHaveBeenCalled();
  });

  it('логинится и переходит в кабинет', async () => {
    const user = userEvent.setup();
    loginMock.mockResolvedValue({
      token: 'jwt-token',
      tokenType: 'Bearer',
      role: 'ROLE_USER',
      profileId: 100,
      expiresInSeconds: 3600,
    });
    renderLogin();

    await user.type(screen.getByPlaceholderText('100'), '100');
    await user.type(screen.getByPlaceholderText('••••••••'), 'secret123');
    await user.click(screen.getByRole('button', { name: 'Войти' }));

    expect(await screen.findByText('Профили клиента')).toBeInTheDocument();
    expect(loginMock).toHaveBeenCalledWith(100, 'secret123');
    expect(JSON.parse(localStorage.getItem('bank-ddd.session') ?? '{}')).toMatchObject({
      token: 'jwt-token',
      profileId: 100,
    });
  });

  it('показывает ошибку бэкенда при неверных данных', async () => {
    const user = userEvent.setup();
    loginMock.mockRejectedValue(new ApiError(401, 'Invalid profileId or password'));
    renderLogin();

    await user.type(screen.getByPlaceholderText('100'), '100');
    await user.type(screen.getByPlaceholderText('••••••••'), 'wrongPass');
    await user.click(screen.getByRole('button', { name: 'Войти' }));

    expect(await screen.findByText('Invalid profileId or password')).toBeInTheDocument();
  });

  it('регистрирует пользователя и сразу логинит', async () => {
    const user = userEvent.setup();
    registerMock.mockResolvedValue({ id: 1, profileId: 100, role: 'ROLE_USER' });
    loginMock.mockResolvedValue({
      token: 'jwt-token',
      tokenType: 'Bearer',
      role: 'ROLE_USER',
      profileId: 100,
      expiresInSeconds: 3600,
    });
    renderLogin();

    await user.click(screen.getByRole('tab', { name: 'Регистрация' }));
    await user.type(screen.getByPlaceholderText('100'), '100');
    const passwords = screen.getAllByPlaceholderText('••••••••');
    await user.type(passwords[0], 'secret123');
    await user.type(passwords[1], 'secret123');
    await user.click(screen.getByRole('button', { name: 'Зарегистрироваться и войти' }));

    expect(await screen.findByText('Профили клиента')).toBeInTheDocument();
    expect(registerMock).toHaveBeenCalledWith(100, 'secret123');
  });

  it('требует совпадения паролей при регистрации', async () => {
    const user = userEvent.setup();
    renderLogin();

    await user.click(screen.getByRole('tab', { name: 'Регистрация' }));
    await user.type(screen.getByPlaceholderText('100'), '100');
    const passwords = screen.getAllByPlaceholderText('••••••••');
    await user.type(passwords[0], 'secret123');
    await user.type(passwords[1], 'secret124');
    await user.click(screen.getByRole('button', { name: 'Зарегистрироваться и войти' }));

    expect(await screen.findByText('Пароли не совпадают')).toBeInTheDocument();
    expect(registerMock).not.toHaveBeenCalled();
  });
});
