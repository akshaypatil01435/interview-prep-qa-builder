import { useEffect, useState, useCallback } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Badge from "../components/Badge";
import Button from "../components/Button";
import { getAllQuestions, getAllTopics, bookmarkQuestion, unbookmarkQuestion } from "../api/questions";
import { Bookmark, BookmarkCheck, CheckCircle2, X, ChevronLeft, ChevronRight } from "lucide-react";

export default function QuestionBank() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [questions, setQuestions] = useState([]);
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Filter & Pagination States
  const [search, setSearch] = useState(searchParams.get("q") || "");
  const [selectedTopicId, setSelectedTopicId] = useState("");
  const [difficultyFilter, setDifficultyFilter] = useState("");
  const [sortBy, setSortBy] = useState("createdAt");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // Load topics list
  useEffect(() => {
    getAllTopics()
      .then((res) => setTopics(res.data))
      .catch((err) => console.error("Could not load topics:", err));
  }, []);

  // Synchronize when query param "q" changes from AppLayout search
  useEffect(() => {
    const q = searchParams.get("q");
    if (q !== null && q !== search) {
      setSearch(q);
      setPage(0);
    }
  }, [searchParams]);

  // Fetch questions from backend with server-side filter, search, sort, and pagination
  const fetchQuestions = useCallback(() => {
    setLoading(true);
    setError("");

    const params = {
      page,
      size: pageSize,
      sort: sortBy,
    };
    if (search.trim()) params.query = search.trim();
    if (selectedTopicId) params.topicId = selectedTopicId;
    if (difficultyFilter) params.difficulty = difficultyFilter;

    getAllQuestions(params)
      .then((res) => {
        const pageData = res.data;
        setQuestions(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
        setTotalElements(pageData.totalElements || 0);
      })
      .catch(() => setError("Couldn't load questions. Please check if the backend is running."))
      .finally(() => setLoading(false));
  }, [page, search, selectedTopicId, difficultyFilter, sortBy]);

  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    setSearchParams(search ? { q: search } : {});
  };

  const clearSearch = () => {
    setSearch("");
    setPage(0);
    setSearchParams({});
  };

  const toggleBookmark = async (q) => {
    try {
      if (q.bookmarked) {
        await unbookmarkQuestion(q.id);
      } else {
        await bookmarkQuestion(q.id);
      }
      setQuestions((prev) =>
        prev.map((item) =>
          item.id === q.id ? { ...item, bookmarked: !item.bookmarked } : item
        )
      );
    } catch {
      setError("Couldn't update bookmark. Please try again.");
    }
  };

  return (
    <AppLayout title="Question Bank">
      {/* Search & Filter Bar */}
      <div className="flex gap-3 mb-4 flex-wrap">
        <form onSubmit={handleSearchSubmit} className="relative flex-1 min-w-[200px]">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search questions (press Enter)..."
            className="w-full border border-slate-200 rounded-lg px-4 py-2 text-sm pr-9 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {search && (
            <X
              className="w-4 h-4 text-slate-400 absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer hover:text-slate-600"
              onClick={clearSearch}
            />
          )}
        </form>

        <select
          value={selectedTopicId}
          onChange={(e) => {
            setSelectedTopicId(e.target.value);
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

        <select
          value={sortBy}
          onChange={(e) => {
            setSortBy(e.target.value);
            setPage(0);
          }}
          className="border border-slate-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="createdAt">Newest First</option>
          <option value="questionText">Question A-Z</option>
          <option value="difficulty">Difficulty</option>
        </select>
      </div>

      {/* Meta Counter */}
      {!loading && !error && (
        <div className="flex justify-between items-center text-xs text-slate-400 mb-3">
          <span>
            Showing {questions.length} of {totalElements} question{totalElements !== 1 && "s"}
          </span>
          {totalPages > 1 && (
            <span>
              Page {page + 1} of {totalPages}
            </span>
          )}
        </div>
      )}

      {loading && <p className="text-slate-400 py-6">Loading questions...</p>}
      {error && <p className="text-red-600 text-sm py-2">{error}</p>}

      {/* Questions List */}
      {!loading && !error && (
        <div className="space-y-3">
          {questions.map((q) => (
            <Card
              key={q.id}
              className="flex items-center justify-between hover:border-slate-300 transition-colors"
            >
              <div
                className="cursor-pointer flex-1"
                onClick={() => navigate(`/questions/${q.id}`)}
              >
                <div className="flex items-center gap-2 mb-2">
                  <Badge color="blue">{q.topic?.name}</Badge>
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
                  {q.solved && (
                    <span className="flex items-center gap-1 text-xs text-emerald-600 font-medium">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Solved
                    </span>
                  )}
                </div>
                <p className="text-slate-800 font-medium">{q.questionText}</p>
              </div>

              <div className="ml-4">
                {q.bookmarked ? (
                  <BookmarkCheck
                    className="w-5 h-5 text-blue-600 cursor-pointer hover:text-blue-800 transition-colors"
                    title="Remove bookmark"
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleBookmark(q);
                    }}
                  />
                ) : (
                  <Bookmark
                    className="w-5 h-5 text-slate-400 cursor-pointer hover:text-blue-600 transition-colors"
                    title="Add bookmark"
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleBookmark(q);
                    }}
                  />
                )}
              </div>
            </Card>
          ))}

          {questions.length === 0 && (
            <p className="text-slate-400 text-center py-12">
              No questions match your current search or filters.
            </p>
          )}

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-3 pt-6">
              <Button
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                <ChevronLeft className="w-4 h-4 inline mr-1" />
                Previous
              </Button>
              <span className="text-sm text-slate-600 px-2">
                Page {page + 1} of {totalPages}
              </span>
              <Button
                variant="secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
                <ChevronRight className="w-4 h-4 inline ml-1" />
              </Button>
            </div>
          )}
        </div>
      )}
    </AppLayout>
  );
}
