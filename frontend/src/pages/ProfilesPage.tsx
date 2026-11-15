import { useCallback, useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { errorMessage } from '../api/client';
import {
  deleteProfile,
  getPassport,
  getRegistration,
  listProfiles,
  updateProfile,
} from '../api/profile';
import { useAuth } from '../auth/AuthContext';
import { CreateProfileWizard } from '../components/CreateProfileWizard';
import { Field, FormRow } from '../components/Field';
import { Modal } from '../components/Modal';
import { hasErrors, validateProfileForm, type Errors } from '../lib/validation';
import type { Passport, Profile, Registration } from '../types';

interface EditProfileModalProps {
  profile: Profile;
  onClose: () => void;
  onSaved: (profile: Profile) => void;
}

function EditProfileModal({ profile, onClose, onSaved }: EditProfileModalProps) {
  const [form, setForm] = useState({
    phoneNumber: String(profile.phoneNumber),
    email: profile.email ?? '',
    nameOnCard: profile.nameOnCard ?? '',
    inn: profile.inn ? String(profile.inn) : '',
    snils: profile.snils ? String(profile.snils) : '',
  });
  const [errors, setErrors] = useState<Errors>({});
  const [banner, setBanner] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function handleSubmit() {
    const nextErrors = validateProfileForm(form);
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) {
      return;
    }
    setBusy(true);
    setBanner(null);
    try {
      const saved = await updateProfile(profile.id as number, {
        ...profile,
        phoneNumber: Number(form.phoneNumber),
        email: form.email.trim() || undefined,
        nameOnCard: form.nameOnCard.trim() || undefined,
        inn: form.inn.trim() ? Number(form.inn) : undefined,
        snils: form.snils.trim() ? Number(form.snils) : undefined,
      });
      onSaved(saved);
    } catch (error) {
      setBanner(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal title={`Профиль #${profile.id}`} subtitle="Контактные данные клиента" onClose={onClose}>
      {banner ? <div className="alert alert--error">{banner}</div> : null}
      <div className="form">
        <FormRow>
          <Field label="Телефон" error={errors.phoneNumber}>
            <input
              className="input"
              value={form.phoneNumber}
              onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
            />
          </Field>
          <Field label="Email" error={errors.email}>
            <input
              className="input"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
          </Field>
        </FormRow>
        <Field label="Имя на карте">
          <input
            className="input"
            value={form.nameOnCard}
            onChange={(e) => setForm({ ...form, nameOnCard: e.target.value })}
          />
        </Field>
        <FormRow>
          <Field label="ИНН" error={errors.inn}>
            <input className="input" value={form.inn} onChange={(e) => setForm({ ...form, inn: e.target.value })} />
          </Field>
          <Field label="СНИЛС" error={errors.snils}>
            <input className="input" value={form.snils} onChange={(e) => setForm({ ...form, snils: e.target.value })} />
          </Field>
        </FormRow>
        <div className="form__actions">
          <button type="button" className="btn btn--ghost" onClick={onClose}>
            Отмена
          </button>
          <button type="button" className="btn btn--primary" onClick={handleSubmit} disabled={busy}>
            {busy ? 'Сохраняем…' : 'Сохранить'}
          </button>
        </div>
      </div>
    </Modal>
  );
}

function DetailRow({ label, value }: { label: string; value: string | number | undefined | null }) {
  return (
    <div className="kv">
      <span className="kv__label">{label}</span>
      <span className="kv__value">{value ?? '—'}</span>
    </div>
  );
}

export function ProfilesPage() {
  const { session, signOut } = useAuth();
  const navigate = useNavigate();
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [passport, setPassport] = useState<Passport | null>(null);
  const [registration, setRegistration] = useState<Registration | null>(null);
  const [loading, setLoading] = useState(true);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [wizardOpen, setWizardOpen] = useState(false);
  const [editing, setEditing] = useState<Profile | null>(null);

  const selected = profiles.find((profile) => profile.id === selectedId) ?? null;

  const reload = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const list = await listProfiles();
      setProfiles(list);
      setSelectedId((current) => {
        if (current && list.some((item) => item.id === current)) {
          return current;
        }
        return list[0]?.id ?? null;
      });
    } catch (loadError) {
      setError(errorMessage(loadError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  useEffect(() => {
    if (!selected) {
      setPassport(null);
      setRegistration(null);
      return;
    }
    let cancelled = false;
    setDetailsLoading(true);
    (async () => {
      try {
        const loadedPassport = await getPassport(selected.passportId);
        if (cancelled) {
          return;
        }
        setPassport(loadedPassport);
        if (loadedPassport.registrationId) {
          const loadedRegistration = await getRegistration(loadedPassport.registrationId);
          if (!cancelled) {
            setRegistration(loadedRegistration);
          }
        }
      } catch (detailsError) {
        if (!cancelled) {
          setError(errorMessage(detailsError));
        }
      } finally {
        if (!cancelled) {
          setDetailsLoading(false);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [selected]);

  if (!session) {
    return <Navigate to="/login" replace />;
  }

  function handleLogout() {
    signOut();
    navigate('/login', { replace: true });
  }

  async function handleDelete(profile: Profile) {
    if (!profile.id) {
      return;
    }
    if (!window.confirm(`Удалить профиль #${profile.id}? Действие необратимо.`)) {
      return;
    }
    try {
      await deleteProfile(profile.id);
      setNotice(`Профиль #${profile.id} удалён`);
      await reload();
    } catch (deleteError) {
      setError(errorMessage(deleteError));
    }
  }

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <span className="brand__mark">₿</span>
          <div>
            <span className="brand__title">Bank-DDD</span>
            <span className="brand__subtitle">Личный кабинет</span>
          </div>
        </div>
        <div className="topbar__actions">
          <a
            className="btn btn--ghost btn--small"
            href="http://localhost:8089/api/profile/swagger-ui/index.html"
            target="_blank"
            rel="noreferrer"
          >
            Swagger API
          </a>
          <span className="chip">
            #{session.profileId} · {session.role === 'ROLE_ADMIN' ? 'админ' : 'клиент'}
          </span>
          <button type="button" className="btn btn--ghost btn--small" onClick={handleLogout}>
            Выйти
          </button>
        </div>
      </header>

      <main className="workspace">
        <aside className="sidebar">
          <div className="sidebar__header">
            <div>
              <h2 className="section-title">Профили</h2>
              <p className="muted">{profiles.length} записей</p>
            </div>
            <button type="button" className="btn btn--primary btn--small" onClick={() => setWizardOpen(true)}>
              + Новый
            </button>
          </div>

          {loading ? <p className="muted">Загружаем профили…</p> : null}

          {!loading && profiles.length === 0 ? (
            <div className="empty">
              <p className="empty__title">Пока пусто</p>
              <p className="muted">Создайте первый профиль — это займёт три шага.</p>
              <button type="button" className="btn btn--primary" onClick={() => setWizardOpen(true)}>
                Создать профиль
              </button>
            </div>
          ) : null}

          <ul className="profile-list">
            {profiles.map((profile) => (
              <li key={profile.id}>
                <button
                  type="button"
                  className={`profile-item${profile.id === selectedId ? ' profile-item--active' : ''}`}
                  onClick={() => setSelectedId(profile.id ?? null)}
                >
                  <span className="profile-item__avatar">{(profile.nameOnCard ?? 'P').slice(0, 1)}</span>
                  <span className="profile-item__body">
                    <span className="profile-item__title">Профиль #{profile.id}</span>
                    <span className="profile-item__meta">{profile.email ?? `+${profile.phoneNumber}`}</span>
                  </span>
                </button>
              </li>
            ))}
          </ul>
        </aside>

        <section className="detail">
          {notice ? <div className="alert alert--success">{notice}</div> : null}
          {error ? <div className="alert alert--error">{error}</div> : null}

          {!selected ? (
            <div className="empty empty--large">
              <p className="empty__title">Выберите профиль</p>
              <p className="muted">Или создайте новый — паспорт и адрес добавим по шагам.</p>
            </div>
          ) : (
            <>
              <div className="detail__header">
                <div>
                  <h2 className="detail__title">Профиль #{selected.id}</h2>
                  <p className="muted">ID профиля #{selected.id} · паспорт #{selected.passportId}</p>
                </div>
                <div className="detail__actions">
                  <button type="button" className="btn btn--ghost" onClick={() => setEditing(selected)}>
                    Редактировать
                  </button>
                  <button type="button" className="btn btn--danger" onClick={() => handleDelete(selected)}>
                    Удалить
                  </button>
                </div>
              </div>

              <div className="cards">
                <div className="card">
                  <h3 className="card__title">Контакты</h3>
                  <DetailRow label="Телефон" value={`+${selected.phoneNumber}`} />
                  <DetailRow label="Email" value={selected.email} />
                  <DetailRow label="Имя на карте" value={selected.nameOnCard} />
                  <DetailRow label="ИНН" value={selected.inn} />
                  <DetailRow label="СНИЛС" value={selected.snils} />
                </div>

                <div className="card">
                  <h3 className="card__title">Паспорт</h3>
                  {detailsLoading ? <p className="muted">Загружаем…</p> : null}
                  {!detailsLoading && passport ? (
                    <>
                      <DetailRow label="Серия и номер" value={`${passport.series} ${passport.number}`} />
                      <DetailRow
                        label="ФИО"
                        value={`${passport.lastName} ${passport.firstName}${passport.middleName ? ` ${passport.middleName}` : ''}`}
                      />
                      <DetailRow label="Пол" value={passport.gender === 'MALE' ? 'Мужской' : 'Женский'} />
                      <DetailRow label="Дата рождения" value={passport.birthDate} />
                      <DetailRow label="Кем выдан" value={passport.issuedBy} />
                      <DetailRow label="Дата выдачи" value={passport.dateOfIssue} />
                      <DetailRow label="Код подразделения" value={passport.divisionCode} />
                    </>
                  ) : null}
                  {!detailsLoading && !passport ? <p className="muted">Нет данных</p> : null}
                </div>

                <div className="card">
                  <h3 className="card__title">Регистрация</h3>
                  {registration ? (
                    <>
                      <DetailRow label="Страна" value={registration.country} />
                      <DetailRow label="Регион" value={registration.region} />
                      <DetailRow label="Город" value={registration.city} />
                      <DetailRow label="Улица" value={registration.street} />
                      <DetailRow label="Дом" value={registration.houseNumber} />
                      <DetailRow label="Квартира" value={registration.flatNumber} />
                      <DetailRow label="Индекс" value={registration.index} />
                    </>
                  ) : (
                    <p className="muted">Загружаем…</p>
                  )}
                </div>
              </div>
            </>
          )}
        </section>
      </main>

      {wizardOpen ? (
        <CreateProfileWizard
          onClose={() => setWizardOpen(false)}
          onCreated={(created) => {
            setWizardOpen(false);
            setNotice(`Профиль #${created.id} создан`);
            void reload();
          }}
        />
      ) : null}

      {editing ? (
        <EditProfileModal
          profile={editing}
          onClose={() => setEditing(null)}
          onSaved={(saved) => {
            setEditing(null);
            setNotice(`Профиль #${saved.id} обновлён`);
            void reload();
          }}
        />
      ) : null}
    </div>
  );
}
