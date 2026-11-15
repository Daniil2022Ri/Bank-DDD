import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, register } from '../api/auth';
import { errorMessage } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { Field } from '../components/Field';
import { hasErrors, validateLogin, validateRegister, type Errors } from '../lib/validation';

type Mode = 'login' | 'register';

export function LoginPage() {
  const navigate = useNavigate();
  const { signIn } = useAuth();
  const [mode, setMode] = useState<Mode>('login');
  const [profileId, setProfileId] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [errors, setErrors] = useState<Errors>({});
  const [banner, setBanner] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  function switchMode(next: Mode) {
    setMode(next);
    setErrors({});
    setBanner(null);
    setConfirm('');
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = { profileId, password };
    const nextErrors = mode === 'register' ? validateRegister({ ...form, confirm }) : validateLogin(form);
    setErrors(nextErrors);
    setBanner(null);
    if (hasErrors(nextErrors)) {
      return;
    }

    setBusy(true);
    try {
      if (mode === 'register') {
        await register(Number(profileId), password);
      }
      const jwt = await login(Number(profileId), password);
      signIn({ token: jwt.token, profileId: jwt.profileId, role: jwt.role });
      navigate('/profiles', { replace: true });
    } catch (error) {
      setBanner(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="auth">
      <section className="auth__intro">
        <div className="brand brand--large">
          <span className="brand__mark">₿</span>
          <div>
            <h1 className="brand__title">Bank-DDD</h1>
            <p className="brand__subtitle">микросервисный банк · MVP профиля</p>
          </div>
        </div>

        <p className="auth__lead">
          Личный кабинет клиента: паспорт, регистрация и контактные данные в одном месте.
          Вход через сервис <code>authorization</code>, данные — через <code>profile</code>.
        </p>

        <ul className="auth__features">
          <li>
            <span className="auth__feature-icon">🔐</span>
            JWT-аутентификация
          </li>
          <li>
            <span className="auth__feature-icon">🪪</span>
            Паспорт и адрес регистрации
          </li>
          <li>
            <span className="auth__feature-icon">🧾</span>
            Аудит изменений на бэкенде
          </li>
        </ul>
      </section>

      <section className="auth__panel">
        <div className="card auth__card">
          <div className="tabs" role="tablist">
            <button
              type="button"
              role="tab"
              aria-selected={mode === 'login'}
              className={`tabs__item${mode === 'login' ? ' tabs__item--active' : ''}`}
              onClick={() => switchMode('login')}
            >
              Вход
            </button>
            <button
              type="button"
              role="tab"
              aria-selected={mode === 'register'}
              className={`tabs__item${mode === 'register' ? ' tabs__item--active' : ''}`}
              onClick={() => switchMode('register')}
            >
              Регистрация
            </button>
          </div>

          <h2 className="auth__title">{mode === 'login' ? 'С возвращением' : 'Создать аккаунт'}</h2>
          <p className="auth__hint">
            {mode === 'login'
              ? 'Введите ID профиля и пароль, чтобы открыть личный кабинет.'
              : 'ID профиля — это номер клиента. Пароль — минимум 6 символов.'}
          </p>

          {banner ? <div className="alert alert--error">{banner}</div> : null}

          <form onSubmit={handleSubmit} noValidate>
            <Field label="ID профиля" error={errors.profileId} hint="Например, 100">
              <input
                className="input"
                inputMode="numeric"
                autoComplete="username"
                value={profileId}
                onChange={(event) => setProfileId(event.target.value)}
                placeholder="100"
              />
            </Field>

            <Field label="Пароль" error={errors.password}>
              <input
                className="input"
                type="password"
                autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="••••••••"
              />
            </Field>

            {mode === 'register' ? (
              <Field label="Повторите пароль" error={errors.confirm}>
                <input
                  className="input"
                  type="password"
                  autoComplete="new-password"
                  value={confirm}
                  onChange={(event) => setConfirm(event.target.value)}
                  placeholder="••••••••"
                />
              </Field>
            ) : null}

            <button className="btn btn--primary btn--block" type="submit" disabled={busy}>
              {busy ? 'Подождите…' : mode === 'login' ? 'Войти' : 'Зарегистрироваться и войти'}
            </button>
          </form>

          <p className="auth__footnote">
            Нет аккаунта?{' '}
            <button type="button" className="link" onClick={() => switchMode(mode === 'login' ? 'register' : 'login')}>
              {mode === 'login' ? 'Создайте его за минуту' : 'Вернитесь ко входу'}
            </button>
          </p>
        </div>
      </section>
    </main>
  );
}
