import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Badge from "../components/Badge";
import { getBookmarks, removeBookmark } from "../api/bookmarks";
import { Bookmark as BookmarkIcon, CheckCircle2, Trash2 } from "lucide-react";

export default function Bookmarks() {
  const navigate = useNavigate();
  const [bookmarks, setBookmarks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadBookmarks = () => {
    setLoading(true);
    setError("");
    getBookmarks()
      .then((res) => setBookmarks(res.data || []))
      .catch(() => setError("Couldn't load your bookmarks. Try again shortly."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadBookmarks();
  }, []);

  const handleRemove = async (questionId) => {
    try {
      await removeBookmark(questionId);
      setBookmarks((prev) => prev.filter((item) => item.id !== questionId));
    } catch {
      setError("Failed to remove bookmark. Please try again.");
    }
  };

  if (loading) {
    return (
      <AppLayout title="Bookmarked Questions">
        <p className="text-slate-400 py-12">Loading your bookmarks...</p>
      </AppLayout>
    );
  }

  if (error) {
    return (
      <AppLayout title="Bookmarked Questions">
        <p className="text-red-600 text-sm py-4">{error}</p>
      </AppLayout>
    );
  }

  if (bookmarks.length === 0) {
    return (
      <AppLayout title="Bookmarked Questions">
        <div className="text-center py-20">
          <BookmarkIcon className="w-10 h-10 text-slate-300 mx-auto mb-3" />
          <p className="text-slate-500 mb-4">You haven't bookmarked any questions yet.</p>
          <button
            onClick={() => navigate("/questions")}
            className="text-blue-600 text-sm font-medium hover:underline cursor-pointer"
          >
            Explore Question Bank
          </button>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Bookmarked Questions">
      <div className="flex justify-between items-center text-xs text-slate-400 mb-3">
        <span>{bookmarks.length} bookmarked question{bookmarks.length !== 1 && "s"}</span>
      </div>

      <div className="space-y-3">
        {bookmarks.map((q) => (
          <Card key={q.id} className="flex justify-between items-center hover:border-slate-300 transition-colors">
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

            <button
              onClick={(e) => {
                e.stopPropagation();
                handleRemove(q.id);
              }}
              className="p-2 text-slate-400 hover:text-red-600 rounded-lg hover:bg-slate-100 transition-colors cursor-pointer ml-3"
              title="Remove from bookmarks"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          </Card>
        ))}
      </div>
    </AppLayout>
  );
}
