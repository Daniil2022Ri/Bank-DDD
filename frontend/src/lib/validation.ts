export type Errors = Record<string, string>;

export function isBlank(value: string | undefined | null): boolean {
  return !value || !value.trim();
}

export function required(value: string | undefined | null, label: string): string | null {
  return isBlank(value) ? `${label} обязательно` : null;
}

export function digits(value: string, label: string, length: number): string | null {
  if (isBlank(value)) {
    return `${label} обязательно`;
  }
  if (!new RegExp(`^\\d{${length}}$`).test(value.trim())) {
    return `${label}: нужно ${length} цифр`;
  }
  return null;
}

export function optionalDigits(value: string, label: string, length: number): string | null {
  if (isBlank(value)) {
    return null;
  }
  if (!new RegExp(`^\\d{${length}}$`).test(value.trim())) {
    return `${label}: нужно ${length} цифр`;
  }
  return null;
}

export function positiveNumber(value: string, label: string): string | null {
  if (isBlank(value)) {
    return `${label} обязательно`;
  }
  if (!/^\d+$/.test(value.trim())) {
    return `${label}: только цифры`;
  }
  if (Number(value) <= 0) {
    return `${label} должно быть больше нуля`;
  }
  return null;
}

export function optionalEmail(value: string): string | null {
  if (isBlank(value)) {
    return null;
  }
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(value.trim()) ? null : 'Некорректный email';
}

export function pastDate(value: string, label: string): string | null {
  if (isBlank(value)) {
    return `${label} обязательно`;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return `${label}: некорректная дата`;
  }
  return date.getTime() > Date.now() ? `${label} не может быть в будущем` : null;
}

export function phoneNumber(value: string): string | null {
  if (isBlank(value)) {
    return 'Телефон обязателен';
  }
  return /^7\d{10}$/.test(value.trim()) ? null : 'Телефон в формате 7XXXXXXXXXX';
}

export interface LoginForm {
  profileId: string;
  password: string;
}

export function validateLogin(form: LoginForm): Errors {
  const errors: Errors = {};
  addError(errors, 'profileId', positiveNumber(form.profileId, 'ID профиля'));
  addError(errors, 'password', required(form.password, 'Пароль'));
  return errors;
}

export function validateRegister(form: LoginForm & { confirm: string }): Errors {
  const errors = validateLogin(form);
  if (form.password.length > 0 && form.password.length < 6) {
    errors.password = 'Пароль должен быть не короче 6 символов';
  }
  if (form.confirm !== form.password) {
    errors.confirm = 'Пароли не совпадают';
  }
  return errors;
}

export interface AddressForm {
  country: string;
  region: string;
  city: string;
  district: string;
  locality: string;
  street: string;
  houseNumber: string;
  houseBlock: string;
  flatNumber: string;
  index: string;
}

export function validateAddress(form: AddressForm): Errors {
  const errors: Errors = {};
  addError(errors, 'country', required(form.country, 'Страна'));
  addError(errors, 'index', positiveNumber(form.index, 'Индекс'));
  return errors;
}

export interface PassportForm {
  series: string;
  number: string;
  lastName: string;
  firstName: string;
  middleName: string;
  gender: 'MALE' | 'FEMALE';
  birthDate: string;
  birthPlace: string;
  issuedBy: string;
  dateOfIssue: string;
  divisionCode: string;
  expirationDate: string;
}

export function validatePassport(form: PassportForm): Errors {
  const errors: Errors = {};
  addError(errors, 'series', digits(form.series, 'Серия', 4));
  addError(errors, 'number', digits(form.number, 'Номер', 6));
  addError(errors, 'lastName', required(form.lastName, 'Фамилия'));
  addError(errors, 'firstName', required(form.firstName, 'Имя'));
  addError(errors, 'birthDate', pastDate(form.birthDate, 'Дата рождения'));
  addError(errors, 'birthPlace', required(form.birthPlace, 'Место рождения'));
  addError(errors, 'issuedBy', required(form.issuedBy, 'Кем выдан'));
  addError(errors, 'dateOfIssue', pastDate(form.dateOfIssue, 'Дата выдачи'));
  addError(errors, 'divisionCode', digits(form.divisionCode, 'Код подразделения', 6));
  return errors;
}

export interface ProfileForm {
  phoneNumber: string;
  email: string;
  nameOnCard: string;
  inn: string;
  snils: string;
}

export function validateProfileForm(form: ProfileForm): Errors {
  const errors: Errors = {};
  addError(errors, 'phoneNumber', phoneNumber(form.phoneNumber));
  addError(errors, 'email', optionalEmail(form.email));
  addError(errors, 'inn', optionalDigits(form.inn, 'ИНН', 12));
  addError(errors, 'snils', optionalDigits(form.snils, 'СНИЛС', 11));
  return errors;
}

export function hasErrors(errors: Errors): boolean {
  return Object.keys(errors).length > 0;
}

function addError(errors: Errors, field: string, message: string | null): void {
  if (message) {
    errors[field] = message;
  }
}
