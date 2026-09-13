import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Badge from "../components/Badge";
import Button from "../components/Button";
import {
  getQuestionById,
  getAllQuestions,
  markQuestionSolved,
  unmarkQuestionSolved,
  bookmarkQuestion,
  unbookmarkQuestion,
} from "../api/questions";
import { Bookmark, BookmarkCheck, CheckCircle2, Circle, ArrowLeft, ArrowRight } from "lucide-react";

export default function QuestionDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [question, setQuestion] = useState(null);
  const [error, setError] = useState("");
  const [allQuestions, setAllQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    setError("");
    setLoading(true);
    getQuestionById(id)
      .then((res) => setQuestion(res.data))
      .catch(() => setError("Question not found."))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    getAllQuestions({ page: 0, size: 100 })
      .then((res) => setAllQuestions(res.data.content || []))
      .catch(() => {});
  }, []);

  const toggleBookmark = async () => {
    if (!question || actionLoading) return;
    setActionLoading(true);
    try {
      if (question.bookmarked) {
        await unbookmarkQuestion(question.id);
        setQuestion((prev) => ({ ...prev, bookmarked: false }));
      } else {
        await bookmarkQuestion(question.id);
        setQuestion((prev) => ({ ...prev, bookmarked: true }));
      }
    } catch {
      setError("Failed to update bookmark.");
    } finally {
      setActionLoading(false);
    }
  };

  const toggleSolved = async () => {
    if (!question || actionLoading) return;
    setActionLoading(true);
    try {
      if (question.solved) {
        await unmarkQuestionSolved(question.id);
        setQuestion((prev) => ({ ...prev, solved: false }));
      } else {
        await markQuestionSolved(question.id);
        setQuestion((prev) => ({ ...prev, solved: true }));
      }
    } catch {
      setError("Failed to update solved status.");
    } finally {
      setActionLoading(false);
    }
  };

  const currentIndex = allQuestions.findIndex((q) => q.id === Number(id));
  const prevQuestion = currentIndex > 0 ? allQuestions[currentIndex - 1] : null;
  const nextQuestion =
    currentIndex >= 0 && currentIndex < allQuestions.length - 1 ? allQuestions[currentIndex + 1] : null;

  if (error) {
    return (
      <AppLayout title="Question">
        <div className="py-8">
          <p className="text-red-600 text-sm mb-4">{error}</p>
          <Button variant="secondary" onClick={() => navigate("/questions")}>
            <ArrowLeft className="w-4 h-4 inline mr-1" /> Back to Question Bank
          </Button>
        </div>
      </AppLayout>
    );
  }

  if (loading || !question) {
    return (
      <AppLayout title="Question">
        <p className="text-slate-400 py-12">Loading question details...</p>
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Question Details">
      <div className="mb-4">
        <button
          onClick={() => navigate("/questions")}
          className="text-sm text-slate-500 hover:text-blue-600 inline-flex items-center gap-1 mb-3 cursor-pointer"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Question Bank
        </button>

        <div className="flex gap-2">
          <Badge color="blue">{question.topic?.name}</Badge>
          <Badge
            color={
              question.difficulty === "EASY"
                ? "emerald"
                : question.difficulty === "HARD"
                ? "red"
                : "amber"
            }
          >
            {question.difficulty}
          </Badge>
          {question.solved && (
            <span className="flex items-center gap-1 text-xs text-emerald-600 font-medium">
              <CheckCircle2 className="w-4 h-4" />
              Solved
            </span>
          )}
        </div>
      </div>

      <h2 className="text-xl font-semibold text-slate-900 mb-4">{question.questionText}</h2>

      {/* Action Buttons */}
      <div className="flex gap-3 mb-6 items-center">
        <Button
          variant="secondary"
          onClick={toggleBookmark}
          disabled={actionLoading}
          className="flex items-center gap-1.5"
        >
          {question.bookmarked ? (
            <BookmarkCheck className="w-4 h-4 text-blue-600" />
          ) : (
            <Bookmark className="w-4 h-4 text-slate-500" />
          )}
          {question.bookmarked ? "Bookmarked" : "Bookmark"}
        </Button>

        <Button
          variant={question.solved ? "secondary" : "primary"}
          onClick={toggleSolved}
          disabled={actionLoading}
          className="flex items-center gap-1.5"
        >
          {question.solved ? (
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          ) : (
            <Circle className="w-4 h-4" />
          )}
          {question.solved ? "Mark as Unsolved" : "Mark as Solved"}
        </Button>
      </div>

      {/* Answer Card */}
      <Card className="mb-6">
        <h3 className="font-semibold text-slate-900 mb-3 text-base">Model Answer</h3>
        <p className="text-slate-700 whitespace-pre-line leading-relaxed">{question.answerText}</p>
      </Card>

      {/* Next / Previous Navigation */}
      <div className="flex justify-between mt-8 pt-4 border-t border-slate-200">
        <Button
          variant="secondary"
          disabled={!prevQuestion}
          onClick={() => prevQuestion && navigate(`/questions/${prevQuestion.id}`)}
        >
          <ArrowLeft className="w-4 h-4 inline mr-1" />
          Previous Question
        </Button>

        <Button
          variant="secondary"
          disabled={!nextQuestion}
          onClick={() => nextQuestion && navigate(`/questions/${nextQuestion.id}`)}
        >
          Next Question
          <ArrowRight className="w-4 h-4 inline ml-1" />
        </Button>
      </div>
    </AppLayout>
  );
}
