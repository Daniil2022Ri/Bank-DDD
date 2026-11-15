import { useState } from 'react';
import { errorMessage } from '../api/client';
import { createActualRegistration, createPassport, createProfile, createRegistration } from '../api/profile';
import { Field, FormRow } from './Field';
import { Modal } from './Modal';
import {
  hasErrors,
  validateAddress,
  validatePassport,
  validateProfileForm,
  type AddressForm,
  type Errors,
  type PassportForm,
  type ProfileForm,
} from '../lib/validation';
import type { Profile } from '../types';

interface CreateProfileWizardProps {
  onClose: () => void;
  onCreated: (profile: Profile) => void;
}

const emptyAddress: AddressForm = {
  country: 'Россия',
  region: '',
  city: '',
  district: '',
  locality: '',
  street: '',
  houseNumber: '',
  houseBlock: '',
  flatNumber: '',
  index: '',
};

const emptyPassport: PassportForm = {
  series: '',
  number: '',
  lastName: '',
  firstName: '',
  middleName: '',
  gender: 'MALE',
  birthDate: '',
  birthPlace: '',
  issuedBy: '',
  dateOfIssue: '',
  divisionCode: '',
  expirationDate: '',
};

const emptyProfile: ProfileForm = {
  phoneNumber: '',
  email: '',
  nameOnCard: '',
  inn: '',
  snils: '',
};

function toNumber(value: string): number {
  return Number(value);
}

function optionalNumber(value: string): number | undefined {
  return value.trim() ? Number(value) : undefined;
}

function optionalText(value: string): string | undefined {
  return value.trim() ? value.trim() : undefined;
}

export function CreateProfileWizard({ onClose, onCreated }: CreateProfileWizardProps) {
  const [step, setStep] = useState(1);
  const [address, setAddress] = useState<AddressForm>(emptyAddress);
  const [passport, setPassport] = useState<PassportForm>(emptyPassport);
  const [profile, setProfile] = useState<ProfileForm>(emptyProfile);
  const [sameAddress, setSameAddress] = useState(true);
  const [actualAddress, setActualAddress] = useState<AddressForm>(emptyAddress);
  const [registrationId, setRegistrationId] = useState<number | null>(null);
  const [passportId, setPassportId] = useState<number | null>(null);
  const [errors, setErrors] = useState<Errors>({});
  const [banner, setBanner] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  function updateAddress(field: keyof AddressForm, value: string) {
    setAddress((previous) => ({ ...previous, [field]: value }));
  }

  function updatePassport(field: keyof PassportForm, value: string) {
    setPassport((previous) => ({ ...previous, [field]: value }));
  }

  function updateProfile(field: keyof ProfileForm, value: string) {
    setProfile((previous) => ({ ...previous, [field]: value }));
  }

  async function submitAddress() {
    const nextErrors = validateAddress(address);
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) {
      return;
    }
    setBusy(true);
    setBanner(null);
    try {
      const created = await createRegistration({
        ...address,
        index: toNumber(address.index),
      });
      setRegistrationId(created.id ?? null);
      setStep(2);
    } catch (error) {
      setBanner(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function submitPassport() {
    const nextErrors = validatePassport(passport);
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) {
      return;
    }
    if (!registrationId) {
      setBanner('Сначала сохраните регистрацию');
      return;
    }
    setBusy(true);
    setBanner(null);
    try {
      const created = await createPassport({
        series: toNumber(passport.series),
        number: toNumber(passport.number),
        lastName: passport.lastName.trim(),
        firstName: passport.firstName.trim(),
        middleName: optionalText(passport.middleName),
        gender: passport.gender,
        birthDate: passport.birthDate,
        birthPlace: passport.birthPlace.trim(),
        issuedBy: passport.issuedBy.trim(),
        dateOfIssue: passport.dateOfIssue,
        divisionCode: toNumber(passport.divisionCode),
        expirationDate: optionalText(passport.expirationDate),
        registrationId,
      });
      setPassportId(created.id ?? null);
      setStep(3);
    } catch (error) {
      setBanner(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  async function submitProfile() {
    const nextErrors = validateProfileForm(profile);
    if (!sameAddress) {
      Object.assign(nextErrors, validateAddress(actualAddress));
    }
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) {
      return;
    }
    if (!passportId) {
      setBanner('Сначала сохраните паспорт');
      return;
    }
    setBusy(true);
    setBanner(null);
    try {
      let actualRegistrationId: number | undefined;
      if (!sameAddress) {
        const createdActual = await createActualRegistration({
          ...actualAddress,
          index: toNumber(actualAddress.index),
        });
        actualRegistrationId = createdActual.id;
      }
      const created = await createProfile({
        phoneNumber: toNumber(profile.phoneNumber),
        email: optionalText(profile.email),
        nameOnCard: optionalText(profile.nameOnCard),
        inn: optionalNumber(profile.inn),
        snils: optionalNumber(profile.snils),
        passportId,
        actualRegistrationId,
      });
      onCreated(created);
    } catch (error) {
      setBanner(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal
      title="Новый профиль"
      subtitle="Три шага: адрес регистрации → паспорт → контактные данные"
      onClose={onClose}
      wide
    >
      <ol className="steps">
        <li className={`steps__item${step === 1 ? ' steps__item--active' : ''}${step > 1 ? ' steps__item--done' : ''}`}>
          Регистрация
        </li>
        <li className={`steps__item${step === 2 ? ' steps__item--active' : ''}${step > 2 ? ' steps__item--done' : ''}`}>
          Паспорт
        </li>
        <li className={`steps__item${step === 3 ? ' steps__item--active' : ''}`}>Профиль</li>
      </ol>

      {banner ? <div className="alert alert--error">{banner}</div> : null}

      {step === 1 ? (
        <div className="form">
          <FormRow>
            <Field label="Страна" error={errors.country}>
              <input className="input" value={address.country} onChange={(e) => updateAddress('country', e.target.value)} />
            </Field>
            <Field label="Индекс" error={errors.index}>
              <input
                className="input"
                inputMode="numeric"
                value={address.index}
                onChange={(e) => updateAddress('index', e.target.value)}
                placeholder="101000"
              />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Регион">
              <input className="input" value={address.region} onChange={(e) => updateAddress('region', e.target.value)} />
            </Field>
            <Field label="Город">
              <input className="input" value={address.city} onChange={(e) => updateAddress('city', e.target.value)} />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Район">
              <input className="input" value={address.district} onChange={(e) => updateAddress('district', e.target.value)} />
            </Field>
            <Field label="Населённый пункт">
              <input className="input" value={address.locality} onChange={(e) => updateAddress('locality', e.target.value)} />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Улица">
              <input className="input" value={address.street} onChange={(e) => updateAddress('street', e.target.value)} />
            </Field>
            <Field label="Дом">
              <input className="input" value={address.houseNumber} onChange={(e) => updateAddress('houseNumber', e.target.value)} />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Корпус">
              <input className="input" value={address.houseBlock} onChange={(e) => updateAddress('houseBlock', e.target.value)} />
            </Field>
            <Field label="Квартира">
              <input className="input" value={address.flatNumber} onChange={(e) => updateAddress('flatNumber', e.target.value)} />
            </Field>
          </FormRow>
          <div className="form__actions">
            <button type="button" className="btn btn--ghost" onClick={onClose}>
              Отмена
            </button>
            <button type="button" className="btn btn--primary" onClick={submitAddress} disabled={busy}>
              Далее: паспорт
            </button>
          </div>
        </div>
      ) : null}

      {step === 2 ? (
        <div className="form">
          <FormRow>
            <Field label="Серия" error={errors.series}>
              <input
                className="input"
                inputMode="numeric"
                value={passport.series}
                onChange={(e) => updatePassport('series', e.target.value)}
                placeholder="4509"
              />
            </Field>
            <Field label="Номер" error={errors.number}>
              <input
                className="input"
                inputMode="numeric"
                value={passport.number}
                onChange={(e) => updatePassport('number', e.target.value)}
                placeholder="123456"
              />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Фамилия" error={errors.lastName}>
              <input className="input" value={passport.lastName} onChange={(e) => updatePassport('lastName', e.target.value)} />
            </Field>
            <Field label="Имя" error={errors.firstName}>
              <input className="input" value={passport.firstName} onChange={(e) => updatePassport('firstName', e.target.value)} />
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Отчество">
              <input className="input" value={passport.middleName} onChange={(e) => updatePassport('middleName', e.target.value)} />
            </Field>
            <Field label="Пол">
              <select
                className="input"
                value={passport.gender}
                onChange={(e) => updatePassport('gender', e.target.value)}
              >
                <option value="MALE">Мужской</option>
                <option value="FEMALE">Женский</option>
              </select>
            </Field>
          </FormRow>
          <FormRow>
            <Field label="Дата рождения" error={errors.birthDate}>
              <input
                className="input"
                type="date"
                value={passport.birthDate}
                onChange={(e) => updatePassport('birthDate', e.target.value)}
              />
            </Field>
            <Field label="Место рождения" error={errors.birthPlace}>
              <input className="input" value={passport.birthPlace} onChange={(e) => updatePassport('birthPlace', e.target.value)} />
            </Field>
          </FormRow>
          <Field label="Кем выдан" error={errors.issuedBy}>
            <input className="input" value={passport.issuedBy} onChange={(e) => updatePassport('issuedBy', e.target.value)} />
          </Field>
          <FormRow>
            <Field label="Дата выдачи" error={errors.dateOfIssue}>
              <input
                className="input"
                type="date"
                value={passport.dateOfIssue}
                onChange={(e) => updatePassport('dateOfIssue', e.target.value)}
              />
            </Field>
            <Field label="Код подразделения" error={errors.divisionCode}>
              <input
                className="input"
                inputMode="numeric"
                value={passport.divisionCode}
                onChange={(e) => updatePassport('divisionCode', e.target.value)}
                placeholder="770001"
              />
            </Field>
          </FormRow>
          <Field label="Срок действия (необязательно)">
            <input
              className="input"
              type="date"
              value={passport.expirationDate}
              onChange={(e) => updatePassport('expirationDate', e.target.value)}
            />
          </Field>
          <div className="form__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setStep(1)}>
              Назад
            </button>
            <button type="button" className="btn btn--primary" onClick={submitPassport} disabled={busy}>
              Далее: профиль
            </button>
          </div>
        </div>
      ) : null}

      {step === 3 ? (
        <div className="form">
          <FormRow>
            <Field label="Телефон" error={errors.phoneNumber} hint="11 цифр, начиная с 7">
              <input
                className="input"
                value={profile.phoneNumber}
                onChange={(e) => updateProfile('phoneNumber', e.target.value)}
                placeholder="79990000000"
              />
            </Field>
            <Field label="Email" error={errors.email}>
              <input
                className="input"
                type="email"
                value={profile.email}
                onChange={(e) => updateProfile('email', e.target.value)}
                placeholder="user@mail.ru"
              />
            </Field>
          </FormRow>
          <Field label="Имя на карте">
            <input
              className="input"
              value={profile.nameOnCard}
              onChange={(e) => updateProfile('nameOnCard', e.target.value)}
              placeholder="IVAN IVANOV"
            />
          </Field>
          <FormRow>
            <Field label="ИНН (необязательно)" error={errors.inn}>
              <input
                className="input"
                value={profile.inn}
                onChange={(e) => updateProfile('inn', e.target.value)}
                placeholder="123456789012"
              />
            </Field>
            <Field label="СНИЛС (необязательно)" error={errors.snils}>
              <input
                className="input"
                value={profile.snils}
                onChange={(e) => updateProfile('snils', e.target.value)}
                placeholder="12345678901"
              />
            </Field>
          </FormRow>

          <label className="checkbox">
            <input
              type="checkbox"
              checked={sameAddress}
              onChange={(e) => setSameAddress(e.target.checked)}
            />
            <span>Фактический адрес совпадает с регистрацией</span>
          </label>

          {!sameAddress ? (
            <div className="form form--nested">
              <FormRow>
                <Field label="Фактическая страна" error={errors.country}>
                  <input
                    className="input"
                    value={actualAddress.country}
                    onChange={(e) => setActualAddress({ ...actualAddress, country: e.target.value })}
                  />
                </Field>
                <Field label="Фактический индекс" error={errors.index}>
                  <input
                    className="input"
                    value={actualAddress.index}
                    onChange={(e) => setActualAddress({ ...actualAddress, index: e.target.value })}
                  />
                </Field>
              </FormRow>
              <FormRow>
                <Field label="Город">
                  <input
                    className="input"
                    value={actualAddress.city}
                    onChange={(e) => setActualAddress({ ...actualAddress, city: e.target.value })}
                  />
                </Field>
                <Field label="Улица">
                  <input
                    className="input"
                    value={actualAddress.street}
                    onChange={(e) => setActualAddress({ ...actualAddress, street: e.target.value })}
                  />
                </Field>
              </FormRow>
            </div>
          ) : null}

          <div className="form__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setStep(2)}>
              Назад
            </button>
            <button type="button" className="btn btn--primary" onClick={submitProfile} disabled={busy}>
              Создать профиль
            </button>
          </div>
        </div>
      ) : null}
    </Modal>
  );
}
