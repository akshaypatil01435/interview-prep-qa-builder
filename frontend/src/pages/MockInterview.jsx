import { useState } from "react";
import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import Button from "../components/Button";
import api from "../api/axiosInstance";

export default function MockInterview() {
    const [stage, setStage] = useState("setup");
    const [questions, setQuestions] = useState([]);
    const [current, setCurrent] = useState(0);
    const [answers, setAnswers] = useState([]);
    const [draft, setDraft] = useState("");

    const start = async () => {
        const res = await api.get("/questions");
        const list = res.data.content || res.data || [];
        setQuestions(list.slice(0, 5));
        setStage("active");
    };

    const submitAnswer = () => {
        setAnswers([...answers, draft]);
        setDraft("");

        if (current + 1 < questions.length) {
            setCurrent((c) => c + 1);
        } else {
            setStage("summary");
        }
    };

    if (stage === "setup") {
        return (
            <AppLayout title="Mock Interview">
                <Card className="max-w-md">
                    <h3 className="font-semibold mb-4">
                        Start a Mock Interview
                    </h3>

                    <Button onClick={start}>
                        Start Mock Interview
                    </Button>
                </Card>
            </AppLayout>
        );
    }

    if (stage === "active") {
        return (
            <AppLayout title="Mock Interview">
                <p className="text-sm text-slate-500 mb-2">
                    Question {current + 1} / {questions.length}
                </p>

                <Card>
                    <p className="font-medium text-slate-800 mb-4">
                        {questions[current].questionText}
                    </p>

                    <textarea
                        className="w-full border border-slate-200 rounded-lg p-3 text-sm h-32"
                        placeholder="Type your answer..."
                        value={draft}
                        onChange={(e) => setDraft(e.target.value)}
                    />

                    <Button
                        className="mt-3"
                        onClick={submitAnswer}
                    >
                        Submit Answer
                    </Button>
                </Card>
            </AppLayout>
        );
    }

    return (
        <AppLayout title="Mock Interview Summary">
            <Card className="max-w-md text-center">
                <p className="text-2xl font-semibold text-slate-900">
                    {answers.length} / {questions.length} answered
                </p>

                <p className="text-slate-500 mt-2">
                    Completion:{" "}
                    {Math.round(
                        (answers.length / questions.length) * 100
                    )}
                    %
                </p>

                <Button
                    className="mt-4"
                    onClick={() => setStage("setup")}
                >
                    Practice Again
                </Button>
            </Card>
        </AppLayout>
    );
}