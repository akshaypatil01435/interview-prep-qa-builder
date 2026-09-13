import { useState } from "react";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Button from "../components/Button";
import api from "../api/axiosInstance";

export default function Quiz() {
    const [stage, setStage] = useState("setup");
    const [questions, setQuestions] = useState([]);
    const [current, setCurrent] = useState(0);
    const [score, setScore] = useState(0);

    const startQuiz = async () => {
        const res = await api.get("/questions");
        const list = res.data.content || res.data || [];
        setQuestions(list.slice(0, 5));
        setStage("active");
        setCurrent(0);
        setScore(0);
    };

    const answer = (isCorrect) => {
        if (isCorrect) {
            setScore((s) => s + 1);
        }

        if (current + 1 < questions.length) {
            setCurrent((c) => c + 1);
        } else {
            setStage("result");
        }
    };

    if (stage === "setup") {
        return (
            <AppLayout title="Quiz">
                <Card className="max-w-md">
                    <h3 className="font-semibold text-slate-900 mb-4">
                        Start a Quiz
                    </h3>

                    <p className="text-sm text-slate-500 mb-4">
                        Pulls 5 random questions from your question bank.
                    </p>

                    <Button onClick={startQuiz}>
                        Start Quiz
                    </Button>
                </Card>
            </AppLayout>
        );
    }

    if (stage === "active") {
        const q = questions[current];

        return (
            <AppLayout title="Quiz in Progress">
                <p className="text-sm text-slate-500 mb-2">
                    Question {current + 1} / {questions.length}
                </p>

                <Card>
                    <p className="text-slate-800 font-medium mb-4">
                        {q.questionText}
                    </p>

                    <div className="flex gap-3">
                        <Button onClick={() => answer(true)}>
                            I knew this
                        </Button>

                        <Button
                            variant="secondary"
                            onClick={() => answer(false)}
                        >
                            I didn't know
                        </Button>
                    </div>
                </Card>
            </AppLayout>
        );
    }

    return (
        <AppLayout title="Quiz Result">
            <Card className="max-w-sm text-center">
                <p className="text-4xl font-bold text-slate-900">
                    {score} / {questions.length}
                </p>

                <p className="text-slate-500 mt-2">
                    {score / questions.length >= 0.7
                        ? "Great job! Keep improving."
                        : "Keep practicing — you'll get there."}
                </p>

                <Button
                    className="mt-4"
                    onClick={() => setStage("setup")}
                >
                    Retry Quiz
                </Button>
            </Card>
        </AppLayout>
    );
}