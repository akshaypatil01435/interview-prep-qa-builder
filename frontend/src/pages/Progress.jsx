import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer
} from "recharts";

export default function Progress() {
    const topicData = [
        { name: "Java", solved: 45, total: 60 },
        { name: "Spring Boot", solved: 20, total: 40 },
        { name: "SQL", solved: 15, total: 30 },
    ];

    return (
        <AppLayout title="Progress & Analytics">
            <div className="grid grid-cols-2 gap-6">
                <Card>
                    <h3 className="font-semibold text-slate-900 mb-4">
                        Topic Progress
                    </h3>

                    <ResponsiveContainer width="100%" height={250}>
                        <BarChart data={topicData}>
                            <XAxis dataKey="name" fontSize={12} />
                            <YAxis fontSize={12} />
                            <Tooltip />

                            <Bar
                                dataKey="solved"
                                fill="#2563eb"
                                radius={[4, 4, 0, 0]}
                            />
                        </BarChart>
                    </ResponsiveContainer>
                </Card>

                <Card>
                    <h3 className="font-semibold text-slate-900 mb-4">
                        Strong vs Weak Areas
                    </h3>

                    <ul className="space-y-2 text-sm">
                        {topicData.map((t) => (
                            <li
                                key={t.name}
                                className="flex justify-between"
                            >
                                <span>{t.name}</span>

                                <span
                                    className={
                                        t.solved / t.total > 0.6
                                            ? "text-green-600"
                                            : "text-amber-600"
                                    }
                                >
                  {Math.round((t.solved / t.total) * 100)}%
                </span>
                            </li>
                        ))}
                    </ul>
                </Card>
            </div>
        </AppLayout>
    );
}
