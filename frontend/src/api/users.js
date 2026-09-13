import api from "./axiosInstance";

export const getCurrentUser = () => api.get("/users/me");
export const updateProfile = (data) => api.put("/users/me", data);
export const changePassword = (data) => api.put("/users/me/password", data);
