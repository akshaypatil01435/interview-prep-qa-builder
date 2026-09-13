import api from "./axiosInstance";

// Real backend endpoints for bookmarks:
// GET /api/bookmarks -> List<QuestionResponse>
// PUT /api/bookmarks/{questionId} -> QuestionResponse (bookmarked = true)
// DELETE /api/bookmarks/{questionId} -> QuestionResponse (bookmarked = false)

export const getBookmarks = () => api.get("/bookmarks");
export const addBookmark = (questionId) => api.put(`/bookmarks/${questionId}`);
export const removeBookmark = (questionId) => api.delete(`/bookmarks/${questionId}`);

// Backwards compatibility alias if called with (userId, questionId)
export const getBookmarksForUser = () => api.get("/bookmarks");
