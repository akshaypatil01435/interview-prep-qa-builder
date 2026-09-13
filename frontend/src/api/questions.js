import api from "./axiosInstance";

// Question bank endpoints
export const getAllQuestions = (params = {}) => api.get("/questions", { params });
export const getQuestionById = (id) => api.get(`/questions/${id}`);

// Solved status
export const markQuestionSolved = (id) => api.put(`/questions/${id}/solved`);
export const unmarkQuestionSolved = (id) => api.delete(`/questions/${id}/solved`);

// Bookmark status (via /api/questions/{id}/bookmark)
export const bookmarkQuestion = (id) => api.put(`/questions/${id}/bookmark`);
export const unbookmarkQuestion = (id) => api.delete(`/questions/${id}/bookmark`);

// Admin Question management
export const createQuestion = (data) => api.post("/admin/questions", data);
export const updateQuestion = (id, data) => api.put(`/admin/questions/${id}`, data);
export const deleteQuestion = (id) => api.delete(`/admin/questions/${id}`);

// Topics endpoints
export const getAllTopics = () => api.get("/topics");
export const createTopic = (data) => api.post("/admin/topics", data);
export const updateTopic = (id, data) => api.put(`/admin/topics/${id}`, data);
export const deleteTopic = (id) => api.delete(`/admin/topics/${id}`);