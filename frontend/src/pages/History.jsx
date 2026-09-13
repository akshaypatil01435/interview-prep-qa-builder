import AppLayout from "../layouts/AppLayout";
import Card from "../components/Card";

export default function History() {
    const activity = [
        { when: "Today", text: "Java question practiced" },
        { when: "Yesterday", text: "SQL quiz completed" },
        { when: "2 days ago", text: "Spring Boot question solved" },
    ];

    return (
        <AppLayout title="Practice History">
            <div className="space-y-3">
                {activity.map((a, i) => (
                    <Card
                        key={i}
                        className="flex justify-between"
                    >
            <span className="text-slate-800">
              {a.text}
            </span>

                        <span className="text-sm text-slate-400">
              {a.when}
            </span>
                    </Card>
                ))}
            </div>
        </AppLayout>
    );
}