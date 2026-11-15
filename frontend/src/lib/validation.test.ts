import { describe, expect, it } from 'vitest';
import {
  hasErrors,
  validateAddress,
  validateLogin,
  validatePassport,
  validateProfileForm,
  validateRegister,
} from '../lib/validation';

describe('validateLogin', () => {
  it('пропускает корректные данные', () => {
    expect(validateLogin({ profileId: '100', password: 'secret123' })).toEqual({});
  });

  it('требует ID профиля и пароль', () => {
    const errors = validateLogin({ profileId: '', password: '' });
    expect(errors.profileId).toMatch(/обязательно/);
    expect(errors.password).toMatch(/обязательно/);
  });

  it('отклоняет нечисловой ID профиля', () => {
    expect(validateLogin({ profileId: 'abc', password: 'secret123' }).profileId).toMatch(/только цифры/);
  });
});

describe('validateRegister', () => {
  it('требует совпадения паролей', () => {
    const errors = validateRegister({ profileId: '100', password: 'secret123', confirm: 'secret124' });
    expect(errors.confirm).toBe('Пароли не совпадают');
  });

  it('требует минимальную длину пароля', () => {
    const errors = validateRegister({ profileId: '100', password: '123', confirm: '123' });
    expect(errors.password).toMatch(/не короче 6/);
  });
});

describe('validateAddress', () => {
  it('требует страну и индекс', () => {
    const errors = validateAddress({
      country: '',
      region: '',
      city: '',
      district: '',
      locality: '',
      street: '',
      houseNumber: '',
      houseBlock: '',
      flatNumber: '',
      index: '',
    });
    expect(errors.country).toMatch(/обязательно/);
    expect(errors.index).toMatch(/обязательно/);
  });

  it('принимает адрес с индексом', () => {
    const errors = validateAddress({
      country: 'Россия',
      region: 'Москва',
      city: 'Москва',
      district: '',
      locality: '',
      street: 'Тверская',
      houseNumber: '1',
      houseBlock: '',
      flatNumber: '',
      index: '101000',
    });
    expect(errors).toEqual({});
  });
});

describe('validatePassport', () => {
  const valid = {
    series: '4509',
    number: '123456',
    lastName: 'Иванов',
    firstName: 'Иван',
    middleName: '',
    gender: 'MALE' as const,
    birthDate: '1990-01-01',
    birthPlace: 'Москва',
    issuedBy: 'ОВД',
    dateOfIssue: '2010-05-20',
    divisionCode: '770001',
    expirationDate: '',
  };

  it('пропускает корректный паспорт', () => {
    expect(validatePassport(valid)).toEqual({});
  });

  it('проверяет формат серии и кода подразделения', () => {
    const errors = validatePassport({ ...valid, series: '45', divisionCode: '77' });
    expect(errors.series).toMatch(/4 цифр/);
    expect(errors.divisionCode).toMatch(/6 цифр/);
  });

  it('не пропускает дату рождения из будущего', () => {
    const future = new Date();
    future.setFullYear(future.getFullYear() + 1);
    const iso = future.toISOString().slice(0, 10);
    expect(validatePassport({ ...valid, birthDate: iso }).birthDate).toMatch(/будущем/);
  });
});

describe('validateProfileForm', () => {
  it('требует телефон в формате 7XXXXXXXXXX', () => {
    expect(validateProfileForm({ phoneNumber: '999', email: '', nameOnCard: '', inn: '', snils: '' }).phoneNumber)
      .toMatch(/7XXXXXXXXXX/);
  });

  it('проверяет email и ИНН', () => {
    const errors = validateProfileForm({
      phoneNumber: '79990000000',
      email: 'not-an-email',
      nameOnCard: '',
      inn: '123',
      snils: '',
    });
    expect(errors.email).toMatch(/email/);
    expect(errors.inn).toMatch(/12 цифр/);
  });

  it('пропускает корректные данные без необязательных полей', () => {
    const errors = validateProfileForm({
      phoneNumber: '79990000000',
      email: '',
      nameOnCard: '',
      inn: '',
      snils: '',
    });
    expect(errors).toEqual({});
  });
});

describe('hasErrors', () => {
  it('возвращает false для пустого объекта', () => {
    expect(hasErrors({})).toBe(false);
  });

  it('возвращает true при наличии ошибок', () => {
    expect(hasErrors({ field: 'ошибка' })).toBe(true);
  });
});
