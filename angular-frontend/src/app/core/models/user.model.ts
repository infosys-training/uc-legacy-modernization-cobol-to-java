export interface UserSecurity {
  userId: string;
  firstName: string | null;
  lastName: string | null;
  password?: string;
  userType: string;
}

export interface AuthResponse {
  authenticated: boolean;
  userId: string;
  userType?: string;
  firstName?: string;
  lastName?: string;
  token?: string;
}
