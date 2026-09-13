import { useEffect, useState, useCallback } from "react";
import AppLayout from "../../layouts/AppLayout";
import Button from "../../components/Button";
import Badge from "../../components/Badge";
import Card from "../../components/Card";
import {
  getAllQuestions,
  getAllTopics,
  createQuestion,
  updateQuestion,
  deleteQuestion,
} from "../../api/questions";
import { Trash2, Pencil, Plus, X, Search, ChevronLeft, ChevronRight } from "lucide-react";

export default function AdminQuestions() {
  const [questions, setQuestions] = useState([]);
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  // Pagination & Filters
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState("");
  const [topicFilter, setTopicFilter] = useState("");
  const [difficultyFilter, setDifficultyFilter] = useState("");

  // Modal State
  const [modalOpen, setModalOpen] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState(null);
  const [form, setForm] = useState({
    questionText: "",
    answerText: "",
    difficulty: "EASY",
    topicId: "",
  });
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [formError, setFormError] = useState("");

  // Load topics
  useEffect(() => {
    getAllTopics()
      .then((res) => {
        setTopics(res.data);
        if (res.data.length > 0 && !form.topicId) {
          setForm((f) => ({ ...f, topicId: res.data[0].id }));
        }
      })
      .catch(() => {});
  }, []);

  const loadQuestions = useCallback(() => {
    setLoading(true);
    setError("");

    const params = {
      page,
      size: 15,
      sort: "createdAt",
    };
    if (search.trim()) params.query = search.trim();
    if (topicFilter) params.topicId = topicFilter;
    if (difficultyFilter) params.difficulty = difficultyFilter;

    getAllQuestions(params)
      .then((res) => {
        const pageData = res.data;
        setQuestions(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
        setTotalElements(pageData.totalElements || 0);
      })
      .catch(() => setError("Failed to load questions from backend."))
      .finally(() => setLoading(false));
  }, [page, search, topicFilter, difficultyFilter]);

  useEffect(() => {
    loadQuestions();
  }, [loadQuestions]);

  const openCreateModal = () => {
    setEditingQuestion(null);
    setForm({
      questionText: "",
      answerText: "",
      difficulty: "EASY",
      topicId: topics[0]?.id || "",
    });
    setFormError("");
    setModalOpen(true);
  };

  const openEditModal = (q) => {
    setEditingQuestion(q);
    setForm({
      questionText: q.questionText,
      answerText: q.answerText || "",
      difficulty: q.difficulty || "EASY",
      topicId: q.topic?.id || topics[0]?.id || "",
    });
    setFormError("");
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditingQuestion(null);
    setFormError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError("");

    if (!form.questionText.trim()) {
      setFormError("Question text is required.");
      return;
    }
    if (!form.answerText.trim()) {
      setFormError("Answer text is required.");
      return;
    }
    if (!form.topicId) {
      setFormError("Please select a topic.");
      return;
    }

    setFormSubmitting(true);
    try {
      const payload = {
        questionText: form.questionText.trim(),
        answerText: form.answerText.trim(),
        difficulty: form.difficulty,
        topicId: Number(form.topicId),
      };

      if (editingQuestion) {
        await updateQuestion(editingQuestion.id, payload);
        setSuccessMsg("Question updated successfully.");
      } else {
        await createQuestion(payload);
        setSuccessMsg("Question created successfully.");
      }

      closeModal();
      loadQuestions();
      setTimeout(() => setSuccessMsg(""), 3000);
    } catch (err) {
      setFormError(
        err.response?.data?.message || "Failed to save question. Please try again."
      );
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleRemove = async (id) => {
    if (!confirm("Are you sure you want to delete this question?")) {
      return;
    }

    try {
      await deleteQuestion(id);
      setSuccessMsg("Question deleted.");
      loadQuestions();
      setTimeout(() => setSuccessMsg(""), 3000);
    } catch (err) {
      setError(
        err.response?.data?.message || "Failed to delete question. Try again."
      );
    }
  };

  return (
    <AppLayout title="Manage Questions">
      {/* Top Controls */}
      <div className="flex justify-between items-center mb-4 flex-wrap gap-3">
        <div className="flex gap-2 flex-1 min-w-[280px]">
          <div className="relative flex-1">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              placeholder="Search questions..."
              className="pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg w-full bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <select
            value={topicFilter}
            onChange={(e) => {
              setTopicFilter(e.target.value);
              setPage(0);
            }}
            className="border border-slate-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Topics</option>
            {topics.map((t) => (
              <option key={t.id} value={t.id}>
                {t.name}
              </option>
            ))}
          </select>

          <select
            value={difficultyFilter}
            onChange={(e) => {
              setDifficultyFilter(e.target.value);
              setPage(0);
            }}
            className="border border-slate-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Difficulties</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <Button onClick={openCreateModal} className="flex items-center gap-1.5">
          <Plus className="w-4 h-4" /> Add Question
        </Button>
      </div>

      {successMsg && (
        <div className="bg-green-50 border border-green-200 text-green-700 text-sm px-4 py-2.5 rounded-lg mb-4">
          {successMsg}
        </div>
      )}

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-2.5 rounded-lg mb-4">
          {error}
        </div>
      )}

      {/* Table */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-slate-500 text-left border-b border-slate-200">
            <tr>
              <th className="p-3.5 font-medium">Question</th>
              <th className="p-3.5 font-medium w-36">Topic</th>
              <th className="p-3.5 font-medium w-28">Difficulty</th>
              <th className="p-3.5 font-medium w-24 text-right">Actions</th>
            </tr>
          </thead>

          <tbody className="divide-y divide-slate-100">
            {loading && (
              <tr>
                <td colSpan={4} className="p-6 text-center text-slate-400">
                  Loading questions...
                </td>
              </tr>
            )}

            {!loading && questions.length === 0 && (
              <tr>
                <td colSpan={4} className="p-8 text-center text-slate-400">
                  No questions found. Click "Add Question" to create one.
                </td>
              </tr>
            )}

            {!loading &&
              questions.map((q) => (
                <tr key={q.id} className="hover:bg-slate-50 transition-colors">
                  <td className="p-3.5 text-slate-800 font-medium">
                    <p className="line-clamp-2">{q.questionText}</p>
                  </td>

                  <td className="p-3.5">
                    <Badge color="blue">{q.topic?.name}</Badge>
                  </td>

                  <td className="p-3.5">
                    <Badge
                      color={
                        q.difficulty === "EASY"
                          ? "emerald"
                          : q.difficulty === "HARD"
                          ? "red"
                          : "amber"
                      }
                    >
                      {q.difficulty}
                    </Badge>
                  </td>

                  <td className="p-3.5 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        onClick={() => openEditModal(q)}
                        className="p-1.5 text-slate-400 hover:text-blue-600 rounded-md hover:bg-slate-100 cursor-pointer"
                        title="Edit Question"
                      >
                        <Pencil className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => handleRemove(q.id)}
                        className="p-1.5 text-slate-400 hover:text-red-600 rounded-md hover:bg-slate-100 cursor-pointer"
                        title="Delete Question"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>

        {/* Pagination bar */}
        {!loading && totalPages > 1 && (
          <div className="flex justify-between items-center p-3.5 border-t border-slate-100 bg-slate-50 text-xs text-slate-500">
            <span>
              Total: {totalElements} questions (Page {page + 1} of {totalPages})
            </span>
            <div className="flex gap-2">
              <Button
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                <ChevronLeft className="w-3.5 h-3.5 inline mr-1" />
                Previous
              </Button>
              <Button
                variant="secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
                <ChevronRight className="w-3.5 h-3.5 inline ml-1" />
              </Button>
            </div>
          </div>
        )}
      </div>

      {/* Add / Edit Question Modal */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-lg max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-slate-900 text-lg">
                {editingQuestion ? "Edit Question" : "Add New Question"}
              </h3>
              <button
                onClick={closeModal}
                className="text-slate-400 hover:text-slate-600 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {formError && (
              <p className="text-sm text-red-600 mb-3 bg-red-50 p-2.5 rounded-lg border border-red-200">
                {formError}
              </p>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Topic *
                </label>
                <select
                  value={form.topicId}
                  onChange={(e) => setForm({ ...form, topicId: e.target.value })}
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  {topics.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Difficulty *
                </label>
                <select
                  value={form.difficulty}
                  onChange={(e) => setForm({ ...form, difficulty: e.target.value })}
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Question Text *
                </label>
                <textarea
                  rows={3}
                  value={form.questionText}
                  onChange={(e) => setForm({ ...form, questionText: e.target.value })}
                  placeholder="Enter the question..."
                  className="w-full border border-slate-200 rounded-lg p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">
                  Model Answer / Explanation *
                </label>
                <textarea
                  rows={6}
                  value={form.answerText}
                  onChange={(e) => setForm({ ...form, answerText: e.target.value })}
                  placeholder="Enter the detailed answer..."
                  className="w-full border border-slate-200 rounded-lg p-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <Button type="button" variant="secondary" onClick={closeModal}>
                  Cancel
                </Button>
                <Button type="submit" disabled={formSubmitting}>
                  {formSubmitting
                    ? "Saving..."
                    : editingQuestion
                    ? "Save Changes"
                    : "Create Question"}
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}
    </AppLayout>
  );
}