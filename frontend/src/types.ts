export type Role = 'ROLE_USER' | 'ROLE_ADMIN';

export interface AuthSession {
  token: string;
  profileId: number;
  role: Role;
}

export interface UserDto {
  id: number;
  profileId: number;
  role: Role;
}

export interface JwtResponse {
  token: string;
  tokenType: string;
  role: Role;
  profileId: number;
  expiresInSeconds: number;
}

export interface Registration {
  id?: number;
  country: string;
  region?: string;
  city?: string;
  district?: string;
  locality?: string;
  street?: string;
  houseNumber?: string;
  houseBlock?: string;
  flatNumber?: string;
  index: number;
}

export type Gender = 'MALE' | 'FEMALE';

export interface Passport {
  id?: number;
  series: number;
  number: number;
  lastName: string;
  firstName: string;
  middleName?: string;
  gender: Gender;
  birthDate: string;
  birthPlace: string;
  issuedBy: string;
  dateOfIssue: string;
  divisionCode: number;
  expirationDate?: string;
  registrationId: number;
}

export interface Profile {
  id?: number;
  phoneNumber: number;
  email?: string;
  nameOnCard?: string;
  inn?: number;
  snils?: number;
  passportId: number;
  actualRegistrationId?: number;
}

export interface ApiErrorBody {
  status: number;
  error: string;
  message: string;
  path: string;
}
