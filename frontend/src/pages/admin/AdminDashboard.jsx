import AppLayout from "../../layouts/AppLayout";
import StatCard from "../../components/StatCard";
import {
    Users,
    FileQuestion,
    Layers,
    BarChart2
} from "lucide-react";

export default function AdminDashboard() {
    return (
        <AppLayout title="Admin Dashboard">
            <div className="grid grid-cols-4 gap-4">
                <StatCard
                    label="Total Users"
                    value="1,245"
                    icon={Users}
                />

                <StatCard
                    label="Total Questions"
                    value="860"
                    icon={FileQuestion}
                />

                <StatCard
                    label="Total Topics"
                    value="8"
                    icon={Layers}
                />

                <StatCard
                    label="Quiz Attempts"
                    value="3,240"
                    icon={BarChart2}
                />
            </div>
        </AppLayout>
    );
}