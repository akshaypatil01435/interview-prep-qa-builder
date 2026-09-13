import AppLayout from "../layouts/AppLayout";
import StatCard from "../components/StatCard";
import Card from "../components/Card";
import ProgressBar from "../components/ProgressBar";

import {
    BookOpen,
    CheckCircle,
    Flame,
    TrendingUp,
} from "lucide-react";

export default function Dashboard() {
    const topics = [
        { name: "Java", solved: 45, total: 60 },
        { name: "Spring Boot", solved: 20, total: 40 },
        { name: "SQL", solved: 15, total: 30 },
    ];

    return (
        <AppLayout title="Dashboard">
            <p className="text-slate-500 mb-6">
                Good morning, Akshay. Keep your interview preparation on track.
            </p>

            <div className="grid grid-cols-4 gap-4 mb-6">
                <StatCard
                    label="Questions Practiced"
                    value="124"
                    icon={BookOpen}
                />

                <StatCard
                    label="Questions Solved"
                    value="87"
                    icon={CheckCircle}
                />

                <StatCard
                    label="Current Streak"
                    value="7 days"
                    icon={Flame}
                />

                <StatCard
                    label="Overall Progress"
                    value="68%"
                    icon={TrendingUp}
                />
            </div>

            <Card className="mb-6">
                <h3 className="font-semibold text-slate-900 mb-4">
                    Continue Practice
                </h3>

                <div className="grid grid-cols-3 gap-4">
                    {topics.map((t) => (
                        <div
                            key={t.name}
                            className="border border-slate-100 rounded-lg p-4"
                        >
                            <p className="font-medium text-slate-800 mb-2">
                                {t.name}
                            </p>

                            <ProgressBar
                                percent={(t.solved / t.total) * 100}
                            />

                            <p className="text-xs text-slate-500 mt-2">
                                {t.solved}/{t.total} questions
                            </p>
                        </div>
                    ))}
                </div>
            </Card>
        </AppLayout>
    );
}