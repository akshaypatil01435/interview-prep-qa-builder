import { createContext, useContext, useState, useEffect } from "react";
import { getCurrentUser } from "../api/users";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchUser = async () => {
    try {
      const res = await getCurrentUser();
      setUser(res.data);
    } catch (err) {
      // Token is invalid/expired, or /users/me doesn't exist on the backend yet
      console.error("Failed to load current user:", err);
      logout();
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchUser();
    } else {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const login = (jwt, authenticatedUser = null) => {
    localStorage.setItem("token", jwt);
    setUser(authenticatedUser);
    setToken(jwt);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        login,
        logout,
        loading,
        isAuthenticated: !!token,
        isAdmin: user?.role === "ADMIN",
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
